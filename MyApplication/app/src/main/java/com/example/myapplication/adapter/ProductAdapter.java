package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.MockDatabase;
import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Item> itemList;
    private OnItemActionListener listener;
    private String contextType;

    public interface OnItemActionListener {
        void onItemAction(Item item, String action);
    }

    public ProductAdapter(List<Item> itemList, String contextType, OnItemActionListener listener) {
        this.itemList = itemList;
        this.contextType = contextType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Item item = itemList.get(position);
        User currentUser = com.example.myapplication.data.FirebaseManager.getInstance().getCurrentUser(); // Fully
                                                                                                          // qualified
                                                                                                          // to avoid
                                                                                                          // import
                                                                                                          // issues or I
                                                                                                          // will add
                                                                                                          // import in
                                                                                                          // next step
                                                                                                          // if this
                                                                                                          // looks ugly.
                                                                                                          // Let's use
                                                                                                          // fully
                                                                                                          // qualified
                                                                                                          // name to be
                                                                                                          // safe and
                                                                                                          // quick.

        // Bind Data
        holder.tvProductName.setText(item.getName());
        holder.tvProductPrice.setText(String.format("৳ %.1f", item.getPrice())); // Using Taka sym bol

        String sellerText = "Seller: " + item.getSellerName() + "\nPhone: " + item.getSellerPhone();
        holder.tvSellerInfo.setText(sellerText);

        holder.tvCategory.setText(item.getCategory());

        holder.tvProductDescription.setText(item.getDescription());

        // Image Loading (Glide)
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Default
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_foreground); // Default
        }

        // Review Display
        holder.tvReviewDisplay.setVisibility(View.GONE);
        if ("Sold".equalsIgnoreCase(item.getStatus()) && item.getReview() != null && !item.getReview().isEmpty()) {
            String reviewText = String.format("Review: \"%s\" (%.1f★)", item.getReview(), item.getRating());
            holder.tvReviewDisplay.setText(reviewText);
            holder.tvReviewDisplay.setVisibility(View.VISIBLE);
        }

        // Status Logic
        String status = item.getStatus();
        if (status == null)
            status = "Available";
        else
            status = status.trim();
        holder.tvStatusLabel.setText(status);
        holder.tvStatusLabel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                "Sold".equalsIgnoreCase(status) ? R.color.red_logout : R.color.green_action));

        // --- BUTTON VISIBILITY LOGIC ---
        holder.btnBuyNow.setVisibility(View.GONE);
        holder.btnAccept.setVisibility(View.GONE);
        holder.btnDecline.setVisibility(View.GONE);
        holder.btnMarkSold.setVisibility(View.GONE);
        holder.btnReview.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);

        if (currentUser == null)
            return;

        boolean isBuyer = "Buyer".equalsIgnoreCase(currentUser.getRole());
        boolean isSeller = "Seller".equalsIgnoreCase(currentUser.getRole());

        String myEmail = currentUser.getEmail();
        boolean isMyItem = (myEmail != null) && myEmail.equals(item.getSellerEmail());

        if ("MARKETPLACE".equals(contextType)) {
            // MARKETPLACE CONTEXT
            if (isBuyer) {
                // Marketplace: Only Show Buy Now
                if ("Available".equalsIgnoreCase(status)) {
                    holder.btnBuyNow.setVisibility(View.VISIBLE);
                    holder.btnBuyNow.setText("Buy Now");
                    holder.btnBuyNow.setEnabled(true);
                }
            } else if (isSeller && isMyItem) {
                // Seller sees their own items in Marketplace
                if ("Available".equalsIgnoreCase(status)) {
                    holder.btnDelete.setVisibility(View.VISIBLE);
                } else if ("Pending".equalsIgnoreCase(status)) {
                    // NEW: Show Accept/Decline for Seller in Dashboard
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnDecline.setVisibility(View.VISIBLE);
                }
            }
        } else if ("NOTIFICATION".equals(contextType)) {
            // NOTIFICATION CONTEXT (Buyer Requests)
            if (isBuyer) {
                if ("Pending".equalsIgnoreCase(status)) {
                    holder.btnBuyNow.setVisibility(View.VISIBLE);
                    holder.btnBuyNow.setText("Pending Approval");
                    holder.btnBuyNow.setEnabled(false);
                } else if ("Accepted".equalsIgnoreCase(status)) {
                    holder.btnReview.setVisibility(View.VISIBLE);
                    holder.btnReview.setText("Received & Review");
                }
            } else if (isSeller) {
                // Sellers checking notifications (Requests)
                if ("Pending".equalsIgnoreCase(status)) {
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnDecline.setVisibility(View.VISIBLE);
                }
            }
        }

        // Listeners
        holder.btnBuyNow.setOnClickListener(v -> listener.onItemAction(item, "BUY"));
        holder.btnAccept.setOnClickListener(v -> listener.onItemAction(item, "ACCEPT"));
        holder.btnDecline.setOnClickListener(v -> listener.onItemAction(item, "DECLINE"));
        holder.btnReview.setOnClickListener(v -> listener.onItemAction(item, "REVIEW"));
        holder.btnDelete.setOnClickListener(v -> listener.onItemAction(item, "DELETE"));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvSellerInfo, tvProductDescription, tvStatusLabel, tvCategory,
                tvReviewDisplay;
        ImageView imgProduct;
        Button btnBuyNow, btnAccept, btnDecline, btnMarkSold, btnReview, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSellerInfo = itemView.findViewById(R.id.tvSellerInfo);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvStatusLabel = itemView.findViewById(R.id.tvStatusLabel);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvReviewDisplay = itemView.findViewById(R.id.tvReviewDisplay);
            imgProduct = itemView.findViewById(R.id.imgProduct);

            btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnMarkSold = itemView.findViewById(R.id.btnMarkSold);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
