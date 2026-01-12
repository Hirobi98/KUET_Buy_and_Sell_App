package com.example.kuet_buy_and_sell_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Auto-login buyer for convenience in Main
        SessionManager.getInstance().startUserSession("1807001", "Rahim");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_marketplace) {
                selectedFragment = new MarketplaceFragment();
            } else if (itemId == R.id.nav_dashboard) {
                selectedFragment = new SellerDashboardFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load Default
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new MarketplaceFragment())
                .commit();

        // Mock Navigation Handling (since I missed creating the menu resource)
        // I will add a temporary button listener logic or just fix the menu resource in
        // next turn.
    }

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
