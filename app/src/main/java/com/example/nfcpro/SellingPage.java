package com.example.nfcpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.nfcpro.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SellingPage extends AppCompatActivity {

    private BottomNavigationView bta;
    private SessionManager sessionManager;
    private String boothId;
    private String adminId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellingpage);

        // 세션 매니저 초기화
        sessionManager = new SessionManager(this);

        // Intent에서 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            boothId = intent.getStringExtra("BOOTH_ID");
            adminId = intent.getStringExtra("ADMIN_ID");

            // 세션 저장
            if (boothId != null && adminId != null) {
                sessionManager.saveSession(boothId, adminId);
            } else {
                // 저장된 세션이 있는지 확인
                SessionManager.SessionData sessionData = sessionManager.getSession();
                if (sessionData != null) {
                    boothId = sessionData.getBoothId();
                    adminId = sessionData.getAdminId();
                } else {
                    // 세션 정보가 없으면 로그인 화면으로 이동
                    redirectToLogin();
                    return;
                }
            }
        } else {
            redirectToLogin();
            return;
        }

        // Initialize BottomNavigationView
        bta = findViewById(R.id._userbottomNavigation);

        // Set default fragment with booth info
        loadFragment(new sellpage(), "one");

        bta.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String tag = "";

                switch (item.getItemId()) {
                    case R.id.nav_sales:
                        fragment = createFragmentWithBoothInfo(new sellpage(), "one");
                        tag = "one";
                        break;
                    case R.id.nav_payment:
                        fragment = createFragmentWithBoothInfo(new listofpay(), "two");
                        tag = "two";
                        break;
                    case R.id.nav_sales_status:
                        fragment = createFragmentWithBoothInfo(new lamkimg(), "three");
                        tag = "three";
                        break;
                    case R.id.nav_my_page:
                        fragment = createFragmentWithBoothInfo(new maypage(), "four");
                        tag = "four";
                        break;
                }

                return loadFragment(fragment, tag);
            }
        });
    }

    private Fragment createFragmentWithBoothInfo(Fragment fragment, String tag) {
        Bundle args = new Bundle();
        args.putString("BOOTH_ID", boothId);
        args.putString("ADMIN_ID", adminId);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id._userfragmentContainerView, fragment, tag);
            ft.commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(this, LoginAdmin.class);
        startActivity(loginIntent);
        finish();
    }

    // 앱 종료 시에도 세션 유지
    @Override
    protected void onStop() {
        super.onStop();
        if (boothId != null && adminId != null) {
            sessionManager.saveSession(boothId, adminId);
        }
    }
}