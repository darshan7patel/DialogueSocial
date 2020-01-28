package com.appiq.dialoguesocial.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.appiq.dialoguesocial.BuildConfig;
import com.appiq.dialoguesocial.R;
import com.appiq.dialoguesocial.activities.ShowImage;
import com.appiq.dialoguesocial.modals.Images;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private Context mCtx;
    public static List<Images> imagesList;

    public ImagesAdapter(Context mCtx, List<Images> imagesList) {
        this.mCtx = mCtx;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_images, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder imageViewHolder, int i) {

        Images img = imagesList.get(i);
//        setting title for image
//        imageViewHolder.textView.setText(img.title);
        Glide.with(mCtx)
                .load(img.url)
                .into(imageViewHolder.imageView);

        final String url = img.url;

        imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(mCtx, ShowImage.class);
                mainIntent.putExtra("image", url);
                //start activity
                mCtx.startActivity(mainIntent);

            }
        });

        if (img.isFavourite){
            imageViewHolder.checkBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

//        TextView textView;
        ImageView imageView;

        CheckBox checkBox;
        ImageButton buttonShare, buttondownload;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

//            textView = itemView.findViewById(R.id.text_view_title);
            imageView = itemView.findViewById(R.id.image_view);

            checkBox = itemView.findViewById(R.id.checkbox_favourite);
            buttonShare = itemView.findViewById(R.id.btn_share);
            buttondownload = itemView.findViewById(R.id.btn_download);

            checkBox.setOnCheckedChangeListener(this);
            buttonShare.setOnClickListener(this);
            buttondownload.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btn_share:
                    shareImage(imagesList.get(getAdapterPosition()));
                    Toasty.info(mCtx, " Please Select an App for Sharing", Toasty.LENGTH_SHORT).show();
                    break;
                case R.id.btn_download:
                    downloadImage(imagesList.get(getAdapterPosition()));
//                    Toasty.normal(mCtx, "Please select an Application", Toasty.LENGTH_SHORT).show();
                    break;
            }

        }

        private void shareImage(Images img){
            ((Activity)mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Glide.with(mCtx)
                    .asBitmap()
                    .load(img.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            ((Activity)mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                            mCtx.startActivity(Intent.createChooser(intent, "Dialogue Social"));
                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp){
            Uri bmpUri = null;

            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "dialogue_social" + System.currentTimeMillis() + ".png");

                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

        private void downloadImage(final Images img){
            ((Activity)mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(img.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity)mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            Uri uri = saveImageAndGetUri(resource, img.id);

                            if (uri != null){
                                intent.setDataAndType(uri, "image/*");
                                Toasty.success(mCtx,"Image Saved to gallery", Toasty.LENGTH_SHORT).show();
//                                mCtx.startActivity(Intent.createChooser(intent, "Dialogue Social"));
                            }
                        }
                    });
        }

        private Uri saveImageAndGetUri(Bitmap bitmap, String id){
            if (ContextCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);
                    intent.setData(uri);

                    mCtx.startActivity(intent);
                    Toasty.normal(mCtx, "Please grant the Storage permission in Permissions", Toasty.LENGTH_SHORT).show();
                }else {
                    ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                return null;
            }

            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/DialogueSocial");
            folder.mkdir();

            File file = new File(folder, id + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                Toasty.success(mCtx,"Download done", Toasty.LENGTH_SHORT).show();
                out.flush();
                out.close();

//                return Uri.fromFile(file);
                return FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (FirebaseAuth.getInstance().getCurrentUser() == null){
                Toasty.error(mCtx, "Please Login first", Toasty.LENGTH_SHORT).show();
                buttonView.setChecked(false);
                return;
            }

            int position = getAdapterPosition();
            Images img = imagesList.get(position);

            DatabaseReference dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(img.category);

            if(isChecked){
                dbFavs.child(img.id).setValue(img);
            }else {
                dbFavs.child(img.id).setValue(null);
            }

        }
    }
}
