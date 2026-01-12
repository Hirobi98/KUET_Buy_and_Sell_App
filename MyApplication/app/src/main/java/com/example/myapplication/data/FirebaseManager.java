package com.example.myapplication.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User currentUserModel;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // --- INTERFACES ---
    public interface AuthCallback {
        void onSuccess(User user);

        void onFailure(String message);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }

    // --- AUTHENTICATION ---

    public void login(String email, String password, String role, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Fetch User Details from Firestore to confirm Role
                        fetchUserDetails(auth.getCurrentUser().getUid(), role, callback);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void signUp(User user, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        // Save User to Firestore
                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    currentUserModel = user;
                                    callback.onSuccess(user);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void logout() {
        auth.signOut();
        currentUserModel = null;
    }

    private void fetchUserDetails(String uid, String role, AuthCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getRole().equalsIgnoreCase(role)) {
                            currentUserModel = user;
                            callback.onSuccess(user);
                        } else {
                            // Role mismatch or invalid user
                            auth.signOut();
                            callback.onFailure("User not found or Role mismatch");
                        }
                    } else {
                        callback.onFailure("User profile not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public User getCurrentUser() {
        return currentUserModel;
    }

    // --- FIRESTORE ITEMS ---

    public void fetchItems(DataCallback<List<Item>> callback) {
        db.collection("items").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Item item = doc.toObject(Item.class);
                            itemList.add(item);
                        }
                        callback.onSuccess(itemList);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void addItem(Item item, DataCallback<Boolean> callback) {
        db.collection("items").document(item.getId()).set(item)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateItemStatus(String itemId, String status, DataCallback<Boolean> callback) {
        db.collection("items").document(itemId).update("status", status)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void uploadImage(android.net.Uri fileUri, DataCallback<String> callback) {
        if (fileUri == null) {
            callback.onFailure("No file selected");
            return;
        }

        String fileName = "images/" + System.currentTimeMillis() + ".jpg";
        com.google.firebase.storage.StorageReference ref = com.google.firebase.storage.FirebaseStorage.getInstance()
                .getReference().child(fileName);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString())))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // --- BUYING FLOW ---

    public void fetchUserItems(String userEmail, DataCallback<List<Item>> callback) {
        // Fetch items where I am the Buyer
        db.collection("items")
                .whereEqualTo("buyerEmail", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Item> items = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Item item = doc.toObject(Item.class);
                        item.setId(doc.getId());
                        items.add(item);
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void requestItem(String itemId, String buyerEmail, DataCallback<Boolean> callback) {
        db.collection("items").document(itemId)
                .update("status", "Pending", "buyerEmail", buyerEmail)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void respondToRequest(String itemId, boolean accepted, DataCallback<Boolean> callback) {
        String newStatus = accepted ? "Accepted" : "Available";
        // If declined, clear buyerEmail? For now, keep history or specific logic:
        // If declined, makes it available again.
        if (accepted) {
            db.collection("items").document(itemId).update("status", newStatus)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } else {
            // Declined -> Reset to Available and remove buyerEmail
            db.collection("items").document(itemId)
                    .update("status", "Available", "buyerEmail", null)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }
    }

    public void submitReview(String itemId, String review, float rating, DataCallback<Boolean> callback) {
        db.collection("items").document(itemId)
                .update("status", "Sold", "review", review, "rating", rating)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
