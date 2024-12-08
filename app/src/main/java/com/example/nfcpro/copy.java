package com.example.nfcpro;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class copy extends Fragment implements NfcAdapter.ReaderCallback {
    private static final String TAG = "BalanceFragment";
    private TextView balanceText;
    private TextView userNameText;
    private TextView cardIdText;
    private NfcAdapter nfcAdapter;
    private DatabaseReference rootRef;

    public copy() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext());
        rootRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_copy, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        balanceText = view.findViewById(R.id.balanceText);
        userNameText = view.findViewById(R.id.userNameText);
        cardIdText = view.findViewById(R.id.cardIdText);

        // 초기 상태 설정
        balanceText.setText("카드를 태그해주세요");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(requireActivity(), this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(requireActivity());
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String cardId = bytesToHex(tag.getId());

        // Firebase에서 카드 ID로 사용자 검색
        rootRef.child("card_user_map").child(cardId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = dataSnapshot.getValue(String.class);
                if (userId != null) {
                    loadUserInfo(userId, cardId);
                } else {
                    requireActivity().runOnUiThread(() ->
                            showError("등록되지 않은 카드입니다."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                requireActivity().runOnUiThread(() ->
                        showError("사용자 정보 조회에 실패했습니다."));
            }
        });
    }

    private void loadUserInfo(String userId, String cardId) {
        rootRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Integer balance = dataSnapshot.child("balance").getValue(Integer.class);

                    requireActivity().runOnUiThread(() -> {
                        userNameText.setText("이름: " + name);
                        cardIdText.setText("카드 ID: " + cardId);
                        balanceText.setText("잔액: " +
                                (balance != null ? String.format("%,d원", balance) : "0원"));
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                requireActivity().runOnUiThread(() ->
                        showError("사용자 정보 로드에 실패했습니다."));
            }
        });
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}