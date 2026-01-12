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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.FirebaseManager;
import com.example.myapplication.data.MockDatabase;
import com.example.myapplication.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AuthFragment extends Fragment {

    private String role; // "Buyer" or "Seller"
    private boolean isLoginMode = true;

    private View layoutHeader;
    private TextView tvAuthTitle, tvAuthSubtitle;
    private TextInputLayout tilName, tilEmail, tilPhone, tilRoll, tilShop, tilPassword;
    private TextInputEditText etName, etEmail, etPhone, etRoll, etShop, etPassword;
    private Button btnSubmit, btnSwitchMode, btnBack;

    public static AuthFragment newInstance(String role) {
        AuthFragment fragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putString("ROLE", role);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        if (getArguments() != null) {
            role = getArguments().getString("ROLE");
        }

        bindViews(view);
        setupUI();
        setupListeners();

        return view;
    }

    private void bindViews(View view) {
        layoutHeader = view.findViewById(R.id.layoutHeader);
        tvAuthTitle = view.findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = view.findViewById(R.id.tvAuthSubtitle);

        tilName = view.findViewById(R.id.tilName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilRoll = view.findViewById(R.id.tilRoll);
        tilShop = view.findViewById(R.id.tilShop);
        tilPassword = view.findViewById(R.id.tilPassword);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etRoll = view.findViewById(R.id.etRoll);
        etShop = view.findViewById(R.id.etShop);
        etPassword = view.findViewById(R.id.etPassword);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSwitchMode = view.findViewById(R.id.btnSwitchMode);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void setupUI() {
        int colorRes = role.equals("Seller") ? R.color.seller_cyan : R.color.pink_dark;
        int colorInt = ContextCompat.getColor(getContext(), colorRes);

        layoutHeader.setBackgroundColor(colorInt);
        btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), colorRes));

        updateFormVisibility();
    }

    private void updateFormVisibility() {
        tvAuthTitle.setText(role + (isLoginMode ? "\nLogin" : "\nSignup"));
        tvAuthSubtitle.setVisibility(isLoginMode ? View.GONE : View.VISIBLE);
        btnSubmit.setText(isLoginMode ? "LOGIN" : "SIGN UP");
        btnSwitchMode.setText(isLoginMode ? "Need an account? Sign Up" : "Already have an account? Login");

        // Reset all to GONE first
        tilName.setVisibility(View.GONE);
        tilEmail.setVisibility(View.GONE);
        tilPhone.setVisibility(View.GONE);
        tilRoll.setVisibility(View.GONE);
        tilShop.setVisibility(View.GONE);

        if (isLoginMode) {
            // LOGIN MODE - Always use Email + Password
            tilEmail.setVisibility(View.VISIBLE);
        } else {
            // SIGNUP MODE - All fields relevant to role
            tilName.setVisibility(View.VISIBLE);
            tilEmail.setVisibility(View.VISIBLE);

            if (role.equals("Buyer")) {
                tilRoll.setVisibility(View.VISIBLE);
                tilRoll.setHint("KUET ROLL");
            } else {
                tilPhone.setVisibility(View.VISIBLE);
                tilPhone.setHint("PHONE NUMBER");
                tilShop.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupListeners() {
        btnSwitchMode.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateFormVisibility();
        });

        btnBack.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToWelcome());

        btnSubmit.setOnClickListener(v -> {
            boolean success = false;
            // Clear errors
            etEmail.setError(null);
            etPassword.setError(null);

            if (isLoginMode)
                handleLogin();
            else
                handleSignup();
        });
    }

    private void handleLogin() {
        // Always use Email for login
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter Email and Password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseManager.getInstance().login(email, password, role, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).navigateToMarketplace();
                }
            }

            @Override
            public void onFailure(String message) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Login Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String roll = etRoll.getText().toString().trim();
        String shopName = etShop.getText().toString().trim();

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Buyer") && roll.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your KUET Roll", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Seller") && (phone.isEmpty() || shopName.isEmpty())) {
            Toast.makeText(getContext(), "Please enter phone number and shop name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct the User object
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setRole(role);
        if (role.equals("Buyer")) {
            newUser.setRoll(roll);
        } else {
            newUser.setPhone(phone);
            newUser.setShopName(shopName);
        }
        newUser.setPassword(password); // Set password for Auth

        // The provided snippet for signup success logic.
        // The `if (!identifier.contains("@"))` part seems to be a remnant from
        // `handleLogin`
        // or an incomplete thought. For signup, we typically use the provided email
        // directly.
        // Assuming `email` is the primary identifier for Firebase signup.
        // If the intention was to construct an email if `etEmail` was not used,
        // that logic would need to be explicitly added. For now, we use `email`.

        FirebaseManager.getInstance().signUp(newUser, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    Toast.makeText(getContext(), "Account Created! Please Login.", Toast.LENGTH_LONG).show();
                    // Requirement: Bring login page after signup
                    FirebaseManager.getInstance().logout(); // Ensure we don't auto-login
                    isLoginMode = true; // Switch back to login view
                    updateFormVisibility();
                }
            }

            @Override
            public void onFailure(String message) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Signup Failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
