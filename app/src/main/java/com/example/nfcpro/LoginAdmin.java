package com.example.nfcpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginAdmin extends AppCompatActivity {

    private Button login;
    private EditText admin_code, login_code;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin);

        // Firebase 초기화
        databaseRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");

        // UI 요소 초기화
        login = findViewById(R.id.LOGIN_BUTTON);
        admin_code = findViewById(R.id.ADMIN_NAME);
        login_code = findViewById(R.id.LOGIN_CODE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adminId = admin_code.getText().toString().trim();
                String password = login_code.getText().toString().trim();

                if (validateInput(adminId, password)) {
                    authenticateAdmin(adminId, password);
                }
            }
        });
    }

    private boolean validateInput(String adminId, String password) {
        if (adminId.isEmpty()) {
            admin_code.setError("관리자 ID를 입력해주세요");
            return false;
        }
        if (password.isEmpty()) {
            login_code.setError("비밀번호를 입력해주세요");
            return false;
        }
        return true;
    }

    private void authenticateAdmin(String adminId, String password) {
        // 로딩 표시 시작
        login.setEnabled(false);

        // booth_owners에서 해당 관리자의 boothId 확인
        databaseRef.child("booth_owners").child(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String boothId = snapshot.getValue(String.class);
                    // boothId로 부스 정보 조회
                    verifyBoothPassword(boothId, adminId, password);
                } else {
                    showError("존재하지 않는 관리자 ID입니다");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("데이터베이스 오류가 발생했습니다");
            }
        });
    }

    private void verifyBoothPassword(String boothId, String adminId, String password) {
        databaseRef.child("booths").child(boothId).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String storedHash = snapshot.getValue(String.class);
                            String inputHash = hashPassword(password);
//                            admin_code.setText(inputHash);

                            if (inputHash != null && inputHash.equals(storedHash)) {
                                loginSuccess(boothId, adminId);
                            } else {
                                showError("비밀번호가 일치하지 않습니다");
                            }
                        } else {
                            showError("부스 정보를 찾을 수 없습니다");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showError("데이터베이스 오류가 발생했습니다");
                    }
                });
    }

    private void loginSuccess(String boothId, String adminId) {
        Intent intent = new Intent(LoginAdmin.this, SellingPage.class);
        // 필요한 데이터를 다음 액티비티로 전달
        intent.putExtra("BOOTH_ID", boothId);
        intent.putExtra("ADMIN_ID", adminId);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    private void showError(String message) {
        login.setEnabled(true);
        Toast.makeText(LoginAdmin.this, message, Toast.LENGTH_SHORT).show();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}