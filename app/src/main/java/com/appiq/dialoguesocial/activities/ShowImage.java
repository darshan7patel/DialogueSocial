package com.appiq.dialoguesocial.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appiq.dialoguesocial.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ShowImage extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

//        MobileAds.initialize(this,
//                "ca-app-pub-4917662253610608~4795498041");
//
//        adView = findViewById(R.id.adView);
//        AdRequest madRequest = new AdRequest.Builder().addTestDevice("0D8A5B864E9980F3A438CBF9F4EC8992").build();
//        adView.loadAd(madRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImage.super.onBackPressed();
//                finish();
            }
        });

        ImageView img = findViewById(R.id.image_view_show);
        String  url = getIntent().getStringExtra("image");
        Glide.with(this)
                .load(url)
                .into(img);
    }
}
