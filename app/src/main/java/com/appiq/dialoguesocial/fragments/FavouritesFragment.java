package com.appiq.dialoguesocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appiq.dialoguesocial.R;
import com.appiq.dialoguesocial.adapters.ImagesAdapter;
import com.appiq.dialoguesocial.modals.Images;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private ShimmerFrameLayout mShimmerViewContainer;

    List<Images> favimgs;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImagesAdapter adapter;

    DatabaseReference dbfavs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favimgs = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
//        progressBar = view.findViewById(R.id.progressbar);
        mShimmerViewContainer = view.findViewById(R.id.fb_fav_shimmer);

        adapter = new ImagesAdapter(getActivity(), favimgs);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area, new SettingsFragment())
                    .commit();
            return;
        }

        dbfavs = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

//        progressBar.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        dbfavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                progressBar.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

                for (DataSnapshot category: dataSnapshot.getChildren()){

                    for (DataSnapshot imgSnapshot: category.getChildren()){

                        String id = imgSnapshot.getKey();
                        String title = imgSnapshot.child("title").getValue(String.class);
                        String desc = imgSnapshot.child("desc").getValue(String.class);
                        String url = imgSnapshot.child("url").getValue(String.class);

                        Images img = new Images(id, title, desc, url, category.getKey());
                        img.isFavourite = true;

                        favimgs.add(img);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

