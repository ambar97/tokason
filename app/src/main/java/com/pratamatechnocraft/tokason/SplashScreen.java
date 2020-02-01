package com.pratamatechnocraft.tokason;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pratamatechnocraft.tokason.Service.SessionManager;

public class SplashScreen extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, PERMISSION_CODE
            );
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sessionManager = new SessionManager(SplashScreen.this);
                    if (sessionManager.isLoggin()) {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent home = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(home);
                        finish();
                    }


                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            sessionManager = new SessionManager(SplashScreen.this);
                            if (sessionManager.isLoggin()) {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent home = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(home);
                                finish();
                            }


                        }
                    }, 500);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            sessionManager = new SessionManager(SplashScreen.this);
                            if (sessionManager.isLoggin()) {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent home = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(home);
                                finish();
                            }


                        }
                    }, 500);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
