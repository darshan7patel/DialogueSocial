package com.appiq.dialoguesocial.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.appiq.dialoguesocial.R;
import androidx.appcompat.widget.Toolbar;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!isNetworkAvailable()){
           setContentView(R.layout.connection);
           Toasty.normal(MainActivity.this, "Please enable your Internet Connection & try Again", Toasty.LENGTH_SHORT).show();
           findViewById(R.id.angry_btn).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   finish();
               }
           });
        }else {
            Toasty.info(this, "Please Wait", Toasty.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            return true;
        }else {
            return false;
        }
    }
}
//Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("Closing the App")
//                    .setMessage("No Internet Connection,check your settings")
//                    .setPositiveButton("Close", new DialogInterface.OnClickListener()
//                    {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        finish();
//        }
//        })
//        .show();