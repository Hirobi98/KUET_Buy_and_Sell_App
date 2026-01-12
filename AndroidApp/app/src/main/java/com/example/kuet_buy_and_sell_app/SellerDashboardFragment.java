package com.example.kuet_buy_and_sell_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SellerDashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private DatabaseInterface db;
    private TextView lblTotalPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = MockDatabase.getInstance();
        SessionManager session = SessionManager.getInstance();

        // Ensure Seller is logged in (Mocking this check, or redirect if not)
        if (!session.isSellerLoggedIn()) {
            // For testing, auto-login mock seller
            session.startSellerSession("01700000001", "Rahim");
        }

        recyclerView = view.findViewById(R.id.recyclerViewSeller);
        lblTotalPosts = view.findViewById(R.id.lblTotalPosts);
        FloatingActionButton fab = view.findViewById(R.id.fabPostItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Seller View = true
        List<Item> myItems = db.getItemsBySeller(session.getSellerPhone());
        adapter = new ItemAdapter(getContext(), myItems, true);
        recyclerView.setAdapter(adapter);

        lblTotalPosts.setText("Total Posts: " + myItems.size());

        fab.setOnClickListener(v -> {
            // Mock Post Item Action
            Toast.makeText(getContext(), "Post Item Feature (Mock)", Toast.LENGTH_SHORT).show();
            // In real app, open PostItemActivity or Dialog
            // For now, let's mock adding an item to see it update
            db.addItem("New Item Mock", 100.0, "Misc", "Desc", null, session.getSellerPhone(), session.getSellerName());
            refresh();
        });
    }

    private void refresh() {
        SessionManager session = SessionManager.getInstance();
        List<Item> myItems = db.getItemsBySeller(session.getSellerPhone());
        adapter.updateList(myItems);
        lblTotalPosts.setText("Total Posts: " + myItems.size());
    }
}
