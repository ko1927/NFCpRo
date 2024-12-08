// NFCWriterActivity.java
package com.example.nfcpro;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NFCWriterActivity extends AppCompatActivity {
    private static final String TAG = "NFCWriterActivity";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techList;

    private String userId;
    private DatabaseReference databaseRef;
    private TextView userNameText;
    private TextView userIdText;
    private TextView balanceText;
    private TextView statusText;
    private ImageView nfcIcon;
    private Handler handler;
    private boolean isProcessing = false;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcwriter);

        handler = new Handler(Looper.getMainLooper());

        // Firebase 초기화
        databaseRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");

        // UI 초기화
        initializeViews();

        // NFC 초기화
        initializeNFC();

        // 사용자 정보 로드
        loadUserInfo();

        // 뒤로가기 버튼
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // 진행 상태 다이얼로그 초기화
        createProgressDialog();

        // NFC 상태에 따른 초기 메시지 설정
        updateNfcStatus();
    }

    private void createProgressDialog() {
        progressDialog = new AlertDialog.Builder(this)
                .setTitle("NFC 카드 처리 중")
                .setMessage("카드를 그대로 유지해주세요...")
                .setCancelable(false)
                .create();
    }

    private void initializeViews() {
        userNameText = findViewById(R.id.userNameText);
        userIdText = findViewById(R.id.userIdText);
        balanceText = findViewById(R.id.balanceText);
        statusText = findViewById(R.id.statusText);
        nfcIcon = findViewById(R.id.nfcIcon);

        // 상태 텍스트 초기화
        statusText.setTextColor(Color.BLACK);
    }

    private void initializeNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            showAlert("NFC 오류", "이 기기는 NFC를 지원하지 않습니다.");
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        try {
            ndef.addDataType("*/*");
            tech.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "MimeType 오류", e);
        }

        intentFiltersArray = new IntentFilter[]{ndef, tech, tag};

        techList = new String[][]{
                new String[]{
                        Ndef.class.getName(),
                        NdefFormatable.class.getName(),
                        MifareUltralight.class.getName()
                }
        };
    }

    private void updateNfcStatus() {
        if (nfcAdapter == null) {
            setStatusMessage("이 기기는 NFC를 지원하지 않습니다.", Color.RED);
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            setStatusMessage("NFC가 꺼져 있습니다. NFC를 켜주세요.", Color.RED);
            showNFCSettings();
            return;
        }

        if (!isProcessing) {
            setStatusMessage("NFC 카드를 기기 뒷면에 태그해주세요.", Color.BLACK);
            startNfcAnimation();
        }
    }

    private void showNFCSettings() {
        new AlertDialog.Builder(this)
                .setTitle("NFC 설정")
                .setMessage("NFC가 꺼져 있습니다. NFC 설정으로 이동하시겠습니까?")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void startNfcAnimation() {
        handler.post(new Runnable() {
            private boolean isVisible = true;
            @Override
            public void run() {
                if (!isProcessing && !isFinishing()) {
                    nfcIcon.animate()
                            .alpha(isVisible ? 0.3f : 1.0f)
                            .setDuration(1000)
                            .withEndAction(() -> {
                                isVisible = !isVisible;
                                if (!isProcessing) {
                                    handler.postDelayed(this, 0);
                                }
                            })
                            .start();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        if (!isFinishing()) {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("확인", null)
                    .show();
        }
    }

    private void setStatusMessage(String message, int color) {
        runOnUiThread(() -> {
            if (statusText != null) {
                statusText.setText(message);
                statusText.setTextColor(color);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showNFCSettings();
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
        updateNfcStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        isProcessing = false;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                processTag(tag);
            }
        }
    }

    private void processTag(Tag tag) {
        if (isProcessing) return;
        isProcessing = true;

        // 진동 피드백
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }

        // 태그 ID 읽기
        byte[] tagId = tag.getId();
        String hexId = bytesToHex(tagId);

        Log.d(TAG, "Tag discovered: " + hexId);
        Log.d(TAG, "Available technologies: " + Arrays.toString(tag.getTechList()));

        progressDialog.show();
        setStatusMessage("카드가 감지되었습니다. ID: " + hexId, Color.BLUE);

        // Firebase에 데이터 쓰기
        writeCardIdToDatabase(hexId);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void writeCardIdToDatabase(String cardId) {
        // 카드 ID 생성 (실제 카드 ID 사용)
        String dbCardId = cardId;

        // Firebase에 카드 정보 저장
        Map<String, Object> updates = new HashMap<>();

        // 카드 상태 정보
        Map<String, Object> cardStatus = new HashMap<>();
        cardStatus.put("isActive", true);
        cardStatus.put("lastUsed", System.currentTimeMillis());
        updates.put("/card_status/" + dbCardId, cardStatus);

        // 카드-사용자 매핑
        updates.put("/card_user_map/" + dbCardId, userId);

        databaseRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    setStatusMessage("✅ 카드가 성공적으로 등록되었습니다!\n\n카드 ID: " + dbCardId, Color.rgb(0, 150, 0));
                    showAlert("등록 완료", "카드가 성공적으로 등록되었습니다.");
                    isProcessing = false;
                })
                .addOnFailureListener(e -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    setStatusMessage("❌ 카드 등록 실패. 다시 시도해주세요.\n" + e.getMessage(), Color.RED);
                    showAlert("등록 실패", "카드 등록에 실패했습니다. 다시 시도해주세요.");
                    isProcessing = false;
                });
    }

    private void loadUserInfo() {
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            showAlert("오류", "사용자 정보를 찾을 수 없습니다.");
            finish();
            return;
        }

        databaseRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Integer balance = dataSnapshot.child("balance").getValue(Integer.class);

                    userNameText.setText("이름: " + name);
                    userIdText.setText("ID: " + userId);
                    balanceText.setText("잔액: " + (balance != null ? String.format("%,d원", balance) : "0원"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showAlert("오류", "사용자 정보 로드에 실패했습니다.");
            }
        });
    }
}