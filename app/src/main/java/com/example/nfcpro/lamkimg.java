package com.example.nfcpro;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class lamkimg extends Fragment {
    private static final String DATABASE_URL = "https://nfctogo-f4da1-default-rtdb.firebaseio.com/";
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private DatabaseReference databaseRef;
    private TextView firstPlace, secondPlace, thirdPlace;
    private Map<String, String> boothNames = new HashMap<>();

    public static lamkimg newInstance() {
        return new lamkimg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRef = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference().child("nfcpro");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamkimg, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadBoothData();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rankings_recycler_view);
        firstPlace = view.findViewById(R.id.first_place_booth);
        secondPlace = view.findViewById(R.id.second_place_booth);
        thirdPlace = view.findViewById(R.id.third_place_booth);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RankingAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void loadBoothData() {
        databaseRef.child("booths").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot booth : dataSnapshot.getChildren()) {
                    String boothId = booth.getKey();
                    String boothName = booth.child("name").getValue(String.class);
                    boothNames.put(boothId, boothName);
                }
                loadRankings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void loadRankings() {
        databaseRef.child("rank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BoothRanking> boothRankings = new ArrayList<>();

                // 모든 부스 데이터를 BoothRanking 객체로 변환
                for (DataSnapshot boothSnapshot : dataSnapshot.getChildren()) {
                    String boothId = boothSnapshot.getKey();
                    Long rankValue = boothSnapshot.getValue(Long.class);
                    String boothName = boothNames.getOrDefault(boothId, boothId);

                    if (rankValue != null) {
                        boothRankings.add(new BoothRanking(boothId, boothName, rankValue));
                    }
                }

                // 수익 기준으로 정렬
                Collections.sort(boothRankings, (a, b) -> b.rankValue.compareTo(a.rankValue));

                // 정렬된 데이터로 RankingItem 리스트 생성
                List<RankingItem> rankingItems = new ArrayList<>();
                for (int i = 0; i < boothRankings.size(); i++) {
                    BoothRanking ranking = boothRankings.get(i);
                    rankingItems.add(new RankingItem(
                            i + 1,  // 1부터 시작하는 순위
                            ranking.boothName,
                            String.format("%,d원", ranking.rankValue)
                    ));
                }

                // UI 업데이트
                updateTopThree(boothRankings);
                adapter.setRankingList(rankingItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void updateTopThree(List<BoothRanking> rankings) {
        if (rankings.size() >= 1) {
            firstPlace.setText(rankings.get(0).boothName);
        }
        if (rankings.size() >= 2) {
            secondPlace.setText(rankings.get(1).boothName);
        }
        if (rankings.size() >= 3) {
            thirdPlace.setText(rankings.get(2).boothName);
        }
    }

    private static class BoothRanking {
        String boothId;
        String boothName;
        Long rankValue;

        BoothRanking(String boothId, String boothName, Long rankValue) {
            this.boothId = boothId;
            this.boothName = boothName;
            this.rankValue = rankValue;
        }
    }
}