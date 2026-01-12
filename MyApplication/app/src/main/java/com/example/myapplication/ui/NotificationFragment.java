package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.data.FirebaseManager;
import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false); // Reuse Layout

        recyclerView = view.findViewById(R.id.recyclerViewProducts);

        // Hide Filters/Title if reusing layout (Quick hack or better to have separate
        // layout? Reusing is fine for speed)
        // Hide Filters
        View filterScroll = view.findViewById(R.id.scrollFilters);
        if (filterScroll != null)
            filterScroll.setVisibility(View.GONE);

        // Configure "Back" Button (Reuse Logout Button)
        Button btnLogout = view.findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setText("Back");
            // Optional: Change color if needed, but User didn't ask.
            btnLogout.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        // Hide Notification Button (if present)
        View btnNotifTop = view.findViewById(R.id.btnNotificationsTop);
        if (btnNotifTop != null) {
            btnNotifTop.setVisibility(View.GONE);
        }

        adapter = new ProductAdapter(new ArrayList<>(), "NOTIFICATION", this::handleItemAction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refreshList();

        return view;
    }

    private void refreshList() {
        User currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        FirebaseManager.getInstance().fetchUserItems(currentUser.getEmail(),
                new FirebaseManager.DataCallback<List<Item>>() {
                    @Override
                    public void onSuccess(List<Item> data) {
                        adapter.setItemList(data);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleItemAction(Item item, String action) {
        // Reuse logic or implement specific Notif logic
        User currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        switch (action) {
            case "REVIEW":
                showReviewDialog(item);
                break;
            // Add other cases if needed
        }
    }

    private void showReviewDialog(Item item) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Give Review");

        View view = getLayoutInflater().inflate(R.layout.dialog_review, null);
        final EditText etReview = view.findViewById(R.id.etReview);
        final EditText etRating = view.findViewById(R.id.etRating);

        builder.setView(view);
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
}
