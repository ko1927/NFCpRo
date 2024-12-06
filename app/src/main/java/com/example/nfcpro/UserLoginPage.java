package com.example.nfcpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserLoginPage extends AppCompatActivity {

    private BottomNavigationView bta;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_page);

        // Initialize BottomNavigationView
        bta = findViewById(R.id._userbottomNavigation); // Make sure to add this ID in your layout

//         Set default fragment
        loadFragment(new inBooth(), "one");

        bta.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String tag = "";

                // Handle navigation selection
                switch (item.getItemId()) {
                    case R.id.nav_booth: // Add these IDs in your menu resource
                        fragment = new inBooth();
                        tag = "one";
                        break;
                    case R.id.nav_smoney:
                        fragment = new copy();
                        tag = "two";
                        break;
                }

                return loadFragment(fragment, tag);
            }
        });
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
}