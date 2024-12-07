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
    private SessionManager sessionManager;
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
        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maypage, container, false);

        // Initialize TextViews
        boothNameText = view.findViewById(R.id.boothNameText);
        boothCodeText = view.findViewById(R.id.boothCodeText);
        boothLocationText = view.findViewById(R.id.boothLocationText);

        // Load booth data for current session
        loadCurrentBoothData();

        return view;
    }

    private void loadCurrentBoothData() {
        // Get current session data
        SessionManager.SessionData sessionData = sessionManager.getSession();

        if (sessionData != null && sessionData.getBoothId() != null) {
            String currentBoothId = sessionData.getBoothId();

            // Update booth code immediately
            boothCodeText.setText("부스 코드: " + currentBoothId);

            // Get booth details from Firebase
            databaseReference.child("booths").child(currentBoothId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot boothSnapshot) {
                            String boothName = boothSnapshot.child("name").getValue(String.class);
                            boothNameText.setText("부스 이름: " + boothName);

                            // Get earnings for this booth
                            databaseReference.child("rank").child(currentBoothId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot rankSnapshot) {
                                            Long earnings = rankSnapshot.getValue(Long.class);
                                            if (earnings != null) {
                                                boothLocationText.setText("총 매출: ₩" + String.format("%,d", earnings));
                                            } else {
                                                boothLocationText.setText("총 매출: ₩0");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            boothLocationText.setText("매출 정보를 불러올 수 없습니다");
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            boothNameText.setText("부스 정보를 불러올 수 없습니다");
                        }
                    });
        } else {
            // No active session
            boothNameText.setText("로그인이 필요합니다");
            boothCodeText.setText("부스 코드: -");
            boothLocationText.setText("총 매출: -");
        }
    }
}