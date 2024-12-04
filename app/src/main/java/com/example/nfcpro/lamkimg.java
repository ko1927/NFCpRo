package com.example.nfcpro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class lamkimg extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private RankingAdapter adapter;

    public static lamkimg newInstance() {
        lamkimg fragment = new lamkimg();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lamkimg, container, false);

        recyclerView = view.findViewById(R.id.rankings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RankingAdapter();
        recyclerView.setAdapter(adapter);

        List<RankingItem> listData = new ArrayList<>();
        listData.add(new RankingItem(4, "김이나박", "700,000원"));
        adapter.setRankingList(listData);

        TextView first = view.findViewById(R.id.first_place_booth);
        TextView second = view.findViewById(R.id.second_place_booth);
        TextView third = view.findViewById(R.id.third_place_booth);

        first.setText("나는");
        second.setText("너는");
        third.setText("우리는");

        return view;
//        return inflater.inflate(R.layout.fragment_lamkimg, container, false);
    }
}