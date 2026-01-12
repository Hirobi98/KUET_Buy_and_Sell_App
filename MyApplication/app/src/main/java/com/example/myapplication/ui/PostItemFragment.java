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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.FirebaseManager;
import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class PostItemFragment extends Fragment {

    private TextInputEditText etName, etPrice, etCategory, etDesc;
    private Button btnChooseFile, btnCancel, btnPost;
    private TextView tvFileName;

    // Removed Image Picker Variables

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item, container, false);

        etName = view.findViewById(R.id.etItemName);
        etPrice = view.findViewById(R.id.etItemPrice);
        etCategory = view.findViewById(R.id.etItemCategory);
        etDesc = view.findViewById(R.id.etItemDescription);

        btnChooseFile = view.findViewById(R.id.btnChooseFile);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnPost = view.findViewById(R.id.btnPost);
        tvFileName = view.findViewById(R.id.tvFileName);

        setupListeners();

        return view;
    }

    private void setupListeners() {
        btnChooseFile.setOnClickListener(v -> {
            // Just set a dummy text (Simulating selection)
            tvFileName.setText("Default Image Selected");
            tvFileName.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(getContext(), "Default Image Selected", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateToMarketplace();
        });

        btnPost.setOnClickListener(v -> handlePost());
    }

    private void handlePost() {
        String name = etName.getText().toString();
        String priceStr = etPrice.getText().toString();
        String category = etCategory.getText().toString();
        String desc = etDesc.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(getContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid Price", Toast.LENGTH_SHORT).show();
            return;
        }

        User currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        // DIRECT POST (No Upload)
        // Pass null as imageUrl so ProductAdapter uses default
        // R.drawable.ic_launcher_foreground (Green Android)
        saveItemToFirestore(name, price, desc, category, currentUser, null);
    }

    private void saveItemToFirestore(String name, double price, String desc, String category, User currentUser,
            String imageUrl) {
        Item newItem = new Item(
                "ID_" + System.currentTimeMillis(),
                name,
                price,
                desc,
                category,
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getPhone(),
                imageUrl,
                "Available");

        FirebaseManager.getInstance().addItem(newItem, new FirebaseManager.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Item Posted Successfully!", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).navigateToMarketplace();
                }
            }

            @Override
            public void onFailure(String message) {
                if (getContext() != null) {
                    btnPost.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to Post: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
