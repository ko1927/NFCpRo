package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToolBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // UI 레이아웃 설정 (activity_main.xml)

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar); // 툴바 레이아웃에서 참조
        setSupportActionBar(toolbar); // 액션바 대신 툴바 사용

    }

    // 메뉴 항목 생성 (툴바 메뉴)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuToolbar, menu);  // 메뉴 XML 파일 연결
        return true;
    }

    // 메뉴 항목 클릭 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button1:
                // 판매 품목 클릭 시 동작
                return true;
            case R.id.button2:
                // 결제 내역 클릭 시 동작
                return true;
            case R.id.button3:
                // 매출 순위 클릭 시 동작
                return true;
            case R.id.button4:
                // 마이페이지 클릭 시 동작
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
