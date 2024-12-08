package com.example.nfcpro;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.util.Log;

public class PaymentHandler implements NfcAdapter.ReaderCallback {
    private static final String TAG = "PaymentHandler";
    private AppCompatActivity activity;
    private Dialog dialog;
    private Handler handler = new Handler();
    private FirebasePaymentManager paymentManager;
    private ArrayList<SelectedProduct> selectedProducts;
    private int totalAmount;
    private SessionManager sessionManager;
    private NfcAdapter nfcAdapter;
    private DatabaseReference rootRef;

    // Dialog 컴포넌트들
    private ImageView dialogImage;
    private TextView dialogTitle;
    private TextView dialogMessage;
    private Button dialogButton;

    public PaymentHandler(AppCompatActivity activity, ArrayList<SelectedProduct> products, int totalAmount) {
        this.activity = activity;
        this.selectedProducts = products;
        this.totalAmount = totalAmount;
        this.paymentManager = new FirebasePaymentManager();
        this.sessionManager = new SessionManager(activity);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        this.rootRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");
    }

    public void startPaymentProcess() {
        // 세션 확인
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData == null || sessionData.getBoothId() == null) {
            showError("부스 정보를 찾을 수 없습니다.");
            return;
        }

        if (nfcAdapter == null) {
            showError("이 기기는 NFC를 지원하지 않습니다.");
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            showError("NFC가 활성화되어 있지 않습니다. NFC를 켜주세요.");
            return;
        }

        showNfcScanDialog();
        // NFC 리더 모드 활성화
        nfcAdapter.enableReaderMode(activity, this,
                NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String cardId = bytesToHex(tag.getId());

        // NFC 리더 모드 비활성화
        nfcAdapter.disableReaderMode(activity);

        // Firebase에서 카드 ID로 사용자 검색
        rootRef.child("card_user_map").child(cardId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = dataSnapshot.getValue(String.class);
                if (userId != null) {
                    // 카드 상태 확인
                    checkCardStatus(userId, cardId);
                } else {
                    handler.post(() -> showPaymentFailDialog("등록되지 않은 카드입니다."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.post(() -> showPaymentFailDialog("사용자 정보 조회에 실패했습니다."));
            }
        });
    }

    private void checkCardStatus(String userId, String cardId) {
        rootRef.child("card_status").child(cardId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isActive = dataSnapshot.child("isActive").getValue(Boolean.class);
                    if (isActive) {
                        // 카드가 활성 상태이면 결제 진행
                        processFirebasePayment(userId);
                        // 카드 사용 시간 업데이트
                        updateCardLastUsed(cardId);
                    } else {
                        handler.post(() -> showPaymentFailDialog("비활성화된 카드입니다."));
                    }
                } else {
                    handler.post(() -> showPaymentFailDialog("카드 상태 정보를 찾을 수 없습니다."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.post(() -> showPaymentFailDialog("카드 상태 확인에 실패했습니다."));
            }
        });
    }

    private void updateCardLastUsed(String cardId) {
        rootRef.child("card_status").child(cardId).child("lastUsed")
                .setValue(System.currentTimeMillis());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showNfcScanDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ic_phone_scan);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        dialogImage = dialog.findViewById(R.id.dialogImage);
        dialogTitle = dialog.findViewById(R.id.dialogTitle);
        dialogMessage = dialog.findViewById(R.id.dialogMessage);
        dialogButton = dialog.findViewById(R.id.dialogButton);

        dialogImage.setImageResource(R.drawable.card);
        dialogTitle.setText("카드를 태그해주세요");
        dialogMessage.setText("휴대폰에 카드를 가까이 대주세요");
        dialogButton.setText("취소");
        dialogButton.setOnClickListener(v -> {
            nfcAdapter.disableReaderMode(activity);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void processFirebasePayment(String userId) {
        dialogImage.setImageResource(R.drawable.chevronright);
        dialogTitle.setText("결제 처리 중");
        dialogMessage.setText("잠시만 기다려주세요...");
        dialogButton.setEnabled(false);

        SessionManager.SessionData sessionData = sessionManager.getSession();
        String boothId = sessionData.getBoothId();

        Log.d(TAG, "결제 처리 시작 - 부스ID: " + boothId + ", 사용자ID: " + userId);

        paymentManager.processPayment(selectedProducts, totalAmount, boothId, userId,
                new FirebasePaymentManager.PaymentCallback() {
                    @Override
                    public void onSuccess() {
                        handler.post(() -> showPaymentCompleteDialog());
                    }

                    @Override
                    public void onFailure(String error) {
                        handler.post(() -> showPaymentFailDialog(error));
                    }
                });
    }

    private void showPaymentCompleteDialog() {
        dialogImage.setImageResource(R.drawable.check);
        dialogTitle.setText("결제 완료");
        dialogMessage.setText("결제가 성공적으로 완료되었습니다");
        dialogButton.setText("확인");
        dialogButton.setEnabled(true);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            goToMainActivity();
        });
    }

    private void showPaymentFailDialog(String error) {
        dialogImage.setImageResource(R.drawable.multiply);
        dialogTitle.setText("결제 실패");
        dialogMessage.setText(error);
        dialogButton.setText("확인");
        dialogButton.setEnabled(true);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            nfcAdapter.enableReaderMode(activity, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        });
    }

    private void showError(String message) {
        new AlertDialog.Builder(activity)
                .setTitle("오류")
                .setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> {})
                .show();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(activity, SellingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}