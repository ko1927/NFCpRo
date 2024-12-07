package com.example.nfcpro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.app.AlertDialog;

public class PaymentHandler {
    private AppCompatActivity activity;
    private Dialog dialog;
    private Handler handler = new Handler();
    private FirebasePaymentManager paymentManager;
    private ArrayList<SelectedProduct> selectedProducts;
    private int totalAmount;
    private SessionManager sessionManager;

    // Dialog 컴포넌트들을 클래스 변수로 선언
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
    }

    public void startPaymentProcess() {
        // 세션 확인
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData == null || sessionData.getBoothId() == null) {
            showError("부스 정보를 찾을 수 없습니다.");
            return;
        }

        showNfcScanDialog();
    }

    private void showNfcScanDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ic_phone_scan);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Dialog 컴포넌트 초기화
        dialogImage = dialog.findViewById(R.id.dialogImage);
        dialogTitle = dialog.findViewById(R.id.dialogTitle);
        dialogMessage = dialog.findViewById(R.id.dialogMessage);
        dialogButton = dialog.findViewById(R.id.dialogButton);

        // 초기 NFC 스캔 화면 설정
        dialogImage.setImageResource(R.drawable._060);
        dialogTitle.setText("카드를 태그해주세요");
        dialogMessage.setText("휴대폰에 카드를 가까이 대주세요");
        dialogButton.setText("취소");
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

        // 결제 프로세스 시작 (NFC 스캔 시뮬레이션)
        handler.postDelayed(() -> {
            processFirebasePayment();
        }, 2000);
    }

    private void processFirebasePayment() {
        // 스캔 완료 표시
        dialogImage.setImageResource(R.drawable._060ti);
        dialogTitle.setText("결제 처리 중");
        dialogMessage.setText("잠시만 기다려주세요...");
        dialogButton.setEnabled(false);

        SessionManager.SessionData sessionData = sessionManager.getSession();
        String boothId = sessionData.getBoothId();

        // Firebase 결제 처리
        paymentManager.processPayment(selectedProducts, totalAmount, boothId,
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
        dialogImage.setImageResource(R.drawable._060);
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
        dialogImage.setImageResource(R.drawable._060);
        dialogTitle.setText("결제 실패");
        dialogMessage.setText(error);
        dialogButton.setText("확인");
        dialogButton.setEnabled(true);
        dialogButton.setOnClickListener(v -> dialog.dismiss());
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