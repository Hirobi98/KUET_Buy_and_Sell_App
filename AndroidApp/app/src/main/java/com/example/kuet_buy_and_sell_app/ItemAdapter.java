package com.example.kuet_buy_and_sell_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private boolean isSellerView;
    private DatabaseInterface db;

    public ItemAdapter(Context context, List<Item> itemList, boolean isSellerView) {
        this.context = context;
        this.itemList = itemList;
        this.isSellerView = isSellerView;
        this.db = MockDatabase.getInstance();
    }

    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        SessionManager session = SessionManager.getInstance();

        // 1. Set Basic Data
        holder.itemName.setText(item.getName());
        holder.price.setText("৳ " + item.getPrice());
        holder.category.setText(item.getCategory());
        holder.description.setText(item.getDescription());
        holder.ownerName.setText("Seller: " + item.getSellerName());
        holder.sellerPhone.setText("Phone: " + item.getSellerPhone());

        // Status Styling
        holder.status.setText(item.getStatus());
        if ("Sold".equalsIgnoreCase(item.getStatus())) {
            holder.status.setTextColor(Color.RED);
        } else if ("Accepted".equalsIgnoreCase(item.getStatus())) {
            holder.status.setTextColor(Color.parseColor("#2ecc71")); // Green
        } else {
            holder.status.setTextColor(Color.BLACK);
        }

        // Image Handling (Mock)
        // In real app, use Glide/Picasso with item.getImagePath()
        holder.itemImage.setImageResource(R.drawable.ic_launcher_background);

        // 2. Control Logic (Ported from cardcontroller.java)

        // Reset Visibility first
        holder.btnAccept.setVisibility(View.GONE);
        holder.btnDecline.setVisibility(View.GONE);
        holder.btnMarkSold.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);
        holder.btnAction.setVisibility(View.GONE);
        holder.btnReview.setVisibility(View.GONE);
        holder.buyerInfoLabel.setVisibility(View.GONE);

        if (isSellerView) {
            // --- SELLER VIEW LOGIC ---
            holder.btnDelete.setVisibility(View.VISIBLE);

            if ("Pending".equalsIgnoreCase(item.getStatus())) {
                // Request from Buyer
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnDecline.setVisibility(View.VISIBLE);

                holder.buyerInfoLabel.setVisibility(View.VISIBLE);
                holder.buyerInfoLabel.setText(
                        "Request from Buyer: " + (item.getBuyerRoll() != null ? item.getBuyerRoll() : "Unknown"));

            } else {
                if (!"Sold".equalsIgnoreCase(item.getStatus())) {
                    holder.btnMarkSold.setVisibility(View.VISIBLE);
                }
            }

        } else {
            // --- BUYER / MARKETPLACE VIEW LOGIC ---

            // "Buy Now" only if Available
            if ("Available".equalsIgnoreCase(item.getStatus())) {
                holder.btnAction.setVisibility(View.VISIBLE);
            }

            // Review Logic: If Accepted and I am the buyer (and not the owner)
            // Note: Simplification - checking if status is Accepted.
            // In real app we check if current user == item.buyerRoll logic.
            // Assuming Marketplace shows everything, we need to know if *this* user bought
            // it.
            if ("Accepted".equalsIgnoreCase(item.getStatus())) {
                if (session.isUserLoggedIn() && session.getUserRoll().equals(item.getBuyerRoll())) {
                    holder.btnReview.setVisibility(View.VISIBLE);
                }
            }

            if ("Reviewed".equalsIgnoreCase(item.getStatus())) {
                holder.status.setText("Status: Completed & Reviewed");
                holder.status.setTextColor(Color.parseColor("#2ecc71"));
            }
        }

        // 3. Click Listeners
        holder.btnAction.setOnClickListener(v -> { // Buy Now
            if (!session.isUserLoggedIn()) {
                Toast.makeText(context, "Please Login First", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.requestPurchase(item.getId(), session.getUserRoll())) {
                Toast.makeText(context, "Purchase Requested!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }
        });

        holder.btnAccept.setOnClickListener(v -> {
            if (db.updateItemStatus(item.getId(), "Accepted")) {
                notifyItemChanged(position);
            }
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (db.updateItemStatus(item.getId(), "Available")) {
                notifyItemChanged(position);
            }
        });

        holder.btnMarkSold.setOnClickListener(v -> {
            if (db.updateItemStatus(item.getId(), "Sold")) {
                notifyItemChanged(position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (db.deleteItem(item.getId())) {
                itemList.remove(position); // Quick remove from list
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
            }
        });

        holder.btnReview.setOnClickListener(v -> showReviewDialog(item, position));
    }

    private void showReviewDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Leave a Review");

        final EditText input = new EditText(context);
        input.setHint("Write a comment...");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String comment = input.getText().toString();
            if (db.addReview(item.getId(), sessionUserRoll(), 5, comment)) {
                // Update local status to hide button
                item.setStatus("Reviewed");
                notifyItemChanged(position);
                Toast.makeText(context, "Review Submitted!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private String sessionUserRoll() {
        return SessionManager.getInstance().getUserRoll();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, ownerName, sellerPhone, category, price, status, description, buyerInfoLabel;
        ImageView itemImage;
        Button btnAccept, btnDecline, btnMarkSold, btnDelete, btnAction, btnReview;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameLabel);
            ownerName = itemView.findViewById(R.id.ownerNameLabel);
            sellerPhone = itemView.findViewById(R.id.sellerPhoneLabel);
            category = itemView.findViewById(R.id.categoryLabel);
            price = itemView.findViewById(R.id.priceLabel);
            status = itemView.findViewById(R.id.statusLabel);
            description = itemView.findViewById(R.id.descriptionLabel);
            buyerInfoLabel = itemView.findViewById(R.id.buyerInfoLabel);
            itemImage = itemView.findViewById(R.id.itemImage);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnMarkSold = itemView.findViewById(R.id.btnMarkSold);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAction = itemView.findViewById(R.id.btnAction);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
