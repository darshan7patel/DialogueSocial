package com.appiq.dialoguesocial.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.appiq.dialoguesocial.R;
import com.appiq.dialoguesocial.fragments.FavouritesFragment;
import com.appiq.dialoguesocial.fragments.FragmentHome;
import com.appiq.dialoguesocial.fragments.SettingsFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private AdView adView;
    private InterstitialAd minterstitialAd;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this,
                "ca-app-pub-4917662253610608~4795498041");

        minterstitialAd = new InterstitialAd(this);
        minterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        minterstitialAd.loadAd(new AdRequest.Builder().build());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        adView = findViewById(R.id.adView);

        AdRequest madRequest = new AdRequest.Builder().addTestDevice("0D8A5B864E9980F3A438CBF9F4EC8992").build();
        adView.loadAd(madRequest);

        displayFragment(new FragmentHome());
    }

    private void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_area, fragment)
                .commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        switch ((menuItem.getItemId())){
            case R.id.nav_home:
                fragment = new FragmentHome();
                break;
            case R.id.nav_fav:
                fragment = new FavouritesFragment();
                break;
            case R.id.nav_set:
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new FragmentHome();
                break;
        }
        displayFragment(fragment);
        displayInterstitial();
        return true;
    }
    public void displayInterstitial() {
        if (minterstitialAd.isLoaded()) {
            minterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            minterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }
}
