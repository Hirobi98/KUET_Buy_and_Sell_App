package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.data.FirebaseManager;
import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private TextView tvTitle;
    private Button btnLogout, btnPostItem, btnNotificationsTop; // Added logic
    private View scrollFilters;
    private View btnNotifications; // Field

    private View chipAll, chipElectronics, chipFurniture, chipBooks, chipClothing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);

        bindViews(view);

        // Initial adapter setup with empty list
        // Initial adapter setup with empty list
        adapter = new ProductAdapter(new ArrayList<>(), "MARKETPLACE", this::handleItemAction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setupListeners();

        // Fetch Data
        refreshList();

        return view;
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnPostItem = view.findViewById(R.id.btnPostItem);
        btnNotificationsTop = view.findViewById(R.id.btnNotificationsTop);
        scrollFilters = view.findViewById(R.id.scrollFilters);

        chipAll = view.findViewById(R.id.chipAll);
        chipElectronics = view.findViewById(R.id.chipElectronics);
        chipFurniture = view.findViewById(R.id.chipFurniture);
        chipBooks = view.findViewById(R.id.chipBooks);
        chipClothing = view.findViewById(R.id.chipClothing);

        btnNotifications = view.findViewById(R.id.btnNotifications);
    }

    private void handleItemAction(Item item, String action) {
        User currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        switch (action) {
            case "BUY":
                FirebaseManager.getInstance().requestItem(item.getId(), currentUser.getEmail(),
                        new FirebaseManager.DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                Toast.makeText(getContext(), "Request Sent to Seller!", Toast.LENGTH_SHORT).show();
                                refreshList();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case "ACCEPT":
                FirebaseManager.getInstance().respondToRequest(item.getId(), true,
                        new FirebaseManager.DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                Toast.makeText(getContext(), "Item Accepted!", Toast.LENGTH_SHORT).show();
                                refreshList();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case "DECLINE":
                FirebaseManager.getInstance().respondToRequest(item.getId(), false,
                        new FirebaseManager.DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                Toast.makeText(getContext(), "Item Declined", Toast.LENGTH_SHORT).show();
                                refreshList();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case "REVIEW":
                showReviewDialog(item);
                break;

            case "DELETE":
                // Optional
                break;
        }
    }

    private void showReviewDialog(Item item) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Give Review");

        View view = getLayoutInflater().inflate(R.layout.dialog_review, null); // Need layout
        // Fallback to programmatic view if layout missing:
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final android.widget.EditText etReview = new android.widget.EditText(getContext());
        etReview.setHint("Write your review...");
        layout.addView(etReview);

        // Simple Rating (1-5 input for now or proper RatingBar)
        final android.widget.EditText etRating = new android.widget.EditText(getContext());
        etRating.setHint("Rating (1.0 - 5.0)");
        etRating.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etRating);

        builder.setView(layout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String review = etReview.getText().toString();
            String ratingStr = etRating.getText().toString();
            if (review.isEmpty() || ratingStr.isEmpty())
                return;

            float rating = Float.parseFloat(ratingStr);
            FirebaseManager.getInstance().submitReview(item.getId(), review, rating,
                    new FirebaseManager.DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            Toast.makeText(getContext(), "Review Submitted!", Toast.LENGTH_SHORT).show();
                            refreshList();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupListeners() {
        // Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseManager.getInstance().logout();
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).navigateToWelcome();
        });

        // Post Item (Seller Only)
        btnPostItem.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToPostItem());

        // Notification Button (Header)
        btnNotificationsTop.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToNotifications());

        // Filters
        chipAll.setOnClickListener(v -> filterList("All"));
        chipElectronics.setOnClickListener(v -> filterList("Electronics"));
        chipFurniture.setOnClickListener(v -> filterList("Furniture"));
        chipBooks.setOnClickListener(v -> filterList("Books-Notes"));
        chipClothing.setOnClickListener(v -> filterList("Clothing"));

        // Notification Button
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToNotifications());
        } else {
            // Fallback
            tvTitle.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToNotifications());
        }
    }

    // Store full list to filter locally
    private List<Item> fullItemList = new ArrayList<>();

    private void filterList(String category) {
        if (category.equals("All")) {
            adapter.setItemList(fullItemList);
            return;
        }

        List<Item> filtered = new ArrayList<>();
        for (Item item : fullItemList) {
            if (item.getCategory() != null && (item.getCategory().equalsIgnoreCase(category) ||
                    (category.contains("Books") && item.getCategory().contains("Book")))) {
                filtered.add(item);
            }
        }

        if (filtered.isEmpty()) {
            Toast.makeText(getContext(), "No items in " + category, Toast.LENGTH_SHORT).show();
        }
        adapter.setItemList(filtered);
    }

    public void refreshList() {
        FirebaseManager.getInstance().fetchItems(new FirebaseManager.DataCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> data) {
                fullItemList = data;
                updateTitleAndList();
            }

            @Override
            public void onFailure(String message) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Error loading items: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTitleAndList() {
        if (getActivity() == null)
            return;

        User user = FirebaseManager.getInstance().getCurrentUser();
        // Fallback or loading state
        if (user == null) {
            // Maybe session expired?
            return;
        }

        if (user.isSeller()) {
            tvTitle.setText("Seller Dashboard");
            btnPostItem.setVisibility(View.VISIBLE);
            btnNotificationsTop.setVisibility(View.GONE);
            scrollFilters.setVisibility(View.GONE);

            // Filter for Seller Items
            List<Item> sellerItems = new ArrayList<>();
            for (Item item : fullItemList) {
                if (item.getSellerEmail() != null && item.getSellerEmail().equals(user.getEmail())) {
                    sellerItems.add(item);
                }
            }
            adapter.setItemList(sellerItems);

        } else {
            tvTitle.setText("KUET HUB (" + user.getRole() + ")");
            btnPostItem.setVisibility(View.GONE);
            btnNotificationsTop.setVisibility(View.VISIBLE);
            scrollFilters.setVisibility(View.VISIBLE);
            adapter.setItemList(fullItemList);
        }
    }
}
