package com.example.nfcpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginAdmin extends AppCompatActivity {

    Button login;
    EditText admin_code, login_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin);

        login = findViewById(R.id.LOGIN_BUTTON);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginAdmin.this,SellingPage.class);
                startActivity(intent);
            }
        });
        admin_code = findViewById(R.id.ADMIN_NAME);
        login_code = findViewById(R.id.LOGIN_CODE);
    }
}