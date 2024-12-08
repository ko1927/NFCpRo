package com.example.nfcpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class inBooth extends Fragment {
    private RecyclerView recyclerView;
    private BoothListAdapter adapter;
    private DatabaseReference boothsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_booth, container, false);
        initializeViews(view);
        loadBoothData();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BoothListAdapter(booth -> {
            // 클릭된 부스의 실제 ID를 전달
            Intent intent = new Intent(getActivity(), InBoothDetail.class);
            intent.putExtra("boothId", booth.getBoothId()); // 실제 선택된 부스 ID 전달
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Initialize Firebase reference
        boothsRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("nfcpro")
                .child("booths");
    }

    private void loadBoothData() {
        boothsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BoothData> boothList = new ArrayList<>();

                for (DataSnapshot booth : dataSnapshot.getChildren()) {
                    String boothId = booth.getKey();
                    String name = booth.child("name").getValue(String.class);
                    String description = booth.child("description").getValue(String.class);
                    String location = booth.child("location").getValue(String.class);

                    boothList.add(new BoothData(boothId, name, description, location));
                }

                adapter.setBooths(boothList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}