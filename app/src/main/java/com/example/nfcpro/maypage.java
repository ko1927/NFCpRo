package com.example.nfcpro;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class maypage extends Fragment {
    private TextView boothNameText;
    private TextView boothCodeText;
    private TextView boothLocationText;
    private DatabaseReference databaseReference;
    private static final String DATABASE_URL = "https://nfctogo-f4da1-default-rtdb.firebaseio.com/";

    public maypage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference("nfcpro");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maypage, container, false);

        // Initialize TextViews
        boothNameText = view.findViewById(R.id.boothNameText);
        boothCodeText = view.findViewById(R.id.boothCodeText);
        boothLocationText = view.findViewById(R.id.boothLocationText);

        // Load booth data
        loadBoothData();

        return view;
    }

    private void loadBoothData() {
        // Get booth earnings data
        databaseReference.child("rank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get booth info for the one with highest earnings
                long highestEarnings = 0;
                String topBoothId = "";

                for (DataSnapshot boothSnapshot : dataSnapshot.getChildren()) {
                    long earnings = boothSnapshot.getValue(Long.class);
                    if (earnings > highestEarnings) {
                        highestEarnings = earnings;
                        topBoothId = boothSnapshot.getKey();
                    }
                }

                final long finalEarnings = highestEarnings;
                final String finalBoothId = topBoothId;

                // Get detailed booth information
                databaseReference.child("booths").child(topBoothId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot boothSnapshot) {
                                String boothName = boothSnapshot.child("name").getValue(String.class);

                                // Update UI
                                boothNameText.setText("부스 이름: " + boothName);
                                boothCodeText.setText("부스 코드: " + finalBoothId);
                                boothLocationText.setText("총 매출: ₩" + String.format("%,d", finalEarnings));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}