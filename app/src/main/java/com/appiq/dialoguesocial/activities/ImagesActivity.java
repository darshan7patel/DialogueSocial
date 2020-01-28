package com.appiq.dialoguesocial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appiq.dialoguesocial.R;
import com.appiq.dialoguesocial.adapters.ImagesAdapter;
import com.appiq.dialoguesocial.modals.Images;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ImagesActivity extends AppCompatActivity {

    private ShimmerFrameLayout mShimmerViewContainer;

    List<Images> imagesList;
    List<Images> favList;
    RecyclerView recyclerView;
    ImagesAdapter adapter;

    DatabaseReference dbimages, dbFavs;
    ProgressBar progressBar;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        MobileAds.initialize(this,
                "ca-app-pub-4917662253610608~4795498041");

        adView = findViewById(R.id.adView);
        AdRequest madRequest = new AdRequest.Builder().addTestDevice("0D8A5B864E9980F3A438CBF9F4EC8992").build();
        adView.loadAd(madRequest);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                setContentView(R.layout.activity_images);
                ImagesActivity.super.onBackPressed();
            }
        });

        favList = new ArrayList<>();
        imagesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImagesAdapter(this, imagesList);

        recyclerView.setAdapter(adapter);

//        progressBar = findViewById(R.id.progressbar);
        mShimmerViewContainer = findViewById(R.id.fb_img_shimmer);

        dbimages = FirebaseDatabase.getInstance().getReference("images")
                .child(category);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);

            fetchFavImages(category);
        }else {
            fetchImages(category);
        }
    }


    private void fetchFavImages(final String category){

//        progressBar.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

                if (dataSnapshot.exists()){
                    for (DataSnapshot imgSnapshot : dataSnapshot.getChildren()){

                        String id = imgSnapshot.getKey();
                        String title = imgSnapshot.child("title").getValue(String.class);
                        String desc = imgSnapshot.child("desc").getValue(String.class);
                        String url = imgSnapshot.child("url").getValue(String.class);

                        Images img = new Images(id, title, desc, url, category);
                        favList.add(img);
                    }
                }
                fetchImages(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchImages(final String category){

//        progressBar.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        dbimages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

                if (dataSnapshot.exists()){
                    for (DataSnapshot imgSnapshot : dataSnapshot.getChildren()){

                        String id = imgSnapshot.getKey();
                        String title = imgSnapshot.child("title").getValue(String.class);
                        String desc = imgSnapshot.child("desc").getValue(String.class);
                        String url = imgSnapshot.child("url").getValue(String.class);

                        Images img = new Images(id, title, desc, url, category);

                        if (isFavourite(img)){
                            img.isFavourite = true;
                        }

                        imagesList.add(img);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isFavourite(Images img){
        for (Images f: favList){
            if(f.id.equals(img.id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }
}
