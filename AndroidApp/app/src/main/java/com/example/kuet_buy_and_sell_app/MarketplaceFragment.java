package com.example.kuet_buy_and_sell_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MarketplaceFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private DatabaseInterface db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marketplace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = MockDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewMarketplace);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Buyer View = false
        adapter = new ItemAdapter(getContext(), db.getAllItems(), false);
        recyclerView.setAdapter(adapter);

        // Filters
        view.findViewById(R.id.btnFilterAll).setOnClickListener(v -> loadItems(null));
        view.findViewById(R.id.btnFilterElectronics).setOnClickListener(v -> loadItems("Electronics"));
        view.findViewById(R.id.btnFilterBooks).setOnClickListener(v -> loadItems("Books"));
        view.findViewById(R.id.btnFilterVehicle).setOnClickListener(v -> loadItems("Vehicle"));
    }

    private void loadItems(String category) {
        List<Item> items;
        if (category == null) {
            items = db.getAllItems();
        } else {
            items = db.getItemsByCategory(category);
        }
        adapter.updateList(items);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list on resume
        loadItems(null);
    }
}
