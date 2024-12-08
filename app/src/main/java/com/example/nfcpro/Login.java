package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    Button admin_login,user_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        admin_login = findViewById(R.id.ADMIN_LOGIN);
        admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,LoginAdmin.class);
                startActivity(intent);
            }
        });
        user_login = findViewById(R.id.USER_LOGIN);
        user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,UserLoginPage.class);
//                Intent intent = new Intent(Login.this,NFCWriterActivity.class);
//                intent.putExtra("userId", "user1");
                startActivity(intent);
            }
        });
    }
}