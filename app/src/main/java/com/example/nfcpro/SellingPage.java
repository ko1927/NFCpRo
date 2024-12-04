package com.example.nfcpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SellingPage extends AppCompatActivity {

    private BottomNavigationView bta;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellingpage);

        // Initialize BottomNavigationView
        bta = findViewById(R.id.bottomNavigation); // Make sure to add this ID in your layout

//         Set default fragment
        loadFragment(new sellpage(), "one");

        bta.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String tag = "";

                // Handle navigation selection
                switch (item.getItemId()) {
                    case R.id.nav_sales: // Add these IDs in your menu resource
                        fragment = new sellpage();
                        tag = "one";
                        break;
                    case R.id.nav_payment:
                        fragment = new listofpay();
                        tag = "two";
                        break;
                    case R.id.nav_sales_status:
                        fragment = new lamkimg();
                        tag = "three";
                        break;
                    case R.id.nav_my_page:
                        fragment = new maypage();
                        tag = "four";
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
            ft.replace(R.id.fragmentContainerView, fragment, tag);
            ft.commitAllowingStateLoss();
            return true;
        }
        return false;
    }
}