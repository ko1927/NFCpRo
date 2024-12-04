package com.example.nfcpro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class lamkimg extends Fragment {

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
        
//        return inflater.inflate(R.layout.fragment_lamkimg, container, false);
    }
}