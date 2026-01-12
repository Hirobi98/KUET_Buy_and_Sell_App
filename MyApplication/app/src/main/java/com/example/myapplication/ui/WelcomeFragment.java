package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button btnSeller = view.findViewById(R.id.btnEnterSeller);
        Button btnBuyer = view.findViewById(R.id.btnEnterBuyer);

        btnSeller.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToAuth("Seller"));
        btnBuyer.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToAuth("Buyer"));

        return view;
    }
}
