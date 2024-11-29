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

public class PaymentHandler {
    private AppCompatActivity activity;
    private Dialog dialog;
    private Handler handler = new Handler();

    // Dialog 컴포넌트들을 클래스 변수로 선언
    private ImageView dialogImage;
    private TextView dialogTitle;
    private TextView dialogMessage;
    private Button dialogButton;

    public PaymentHandler(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void startPaymentProcess() {
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
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

        // NFC 스캔 시뮬레이션 (실제로는 NFC 리더기 이벤트로 대체)
        handler.postDelayed(() -> {
            // 스캔 완료 표시
            dialogImage.setImageResource(R.drawable._060ti);
            dialogTitle.setText("스캔 완료");
            dialogMessage.setText("결제가 진행됩니다");
            dialogButton.setEnabled(false);

            // 결제 처리 시뮬레이션
            handler.postDelayed(() -> {
                showPaymentCompleteDialog();
            }, 1500);
        }, 2000);
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

    private void goToMainActivity() {
        Intent intent = new Intent(activity, SellingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}