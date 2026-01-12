package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.ui.MarketplaceFragment;
import com.example.myapplication.ui.WelcomeFragment;
import com.example.myapplication.ui.AuthFragment;
import com.example.myapplication.ui.PostItemFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start with Welcome Fragment
        if (savedInstanceState == null) {
            navigateToWelcome();
        }
    }

    public void navigateToWelcome() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new WelcomeFragment())
                .commit();
    }

    public void navigateToAuth(String role) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, AuthFragment.newInstance(role))
                .addToBackStack(null)
                .commit();
    }

    public void navigateToMarketplace() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MarketplaceFragment())
                .commit();
    }

    public void navigateToNotifications() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new com.example.myapplication.ui.NotificationFragment())
                .addToBackStack(null)
                .commit();
    }

    // Refresh Current Fragment if it is MarketplaceFragment())
    public void navigateToPostItem() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PostItemFragment())
                .addToBackStack(null)
                .commit();
    }
}