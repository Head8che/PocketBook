package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.example.pocketbook.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /* UNCOMMENT OUT THE LINE BELOW TO ACCESS LOGIN PAGE */
//         FirebaseAuth.getInstance().signOut();

        /* Duration of wait in milliseconds */
        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* keep intent in condition because auth could be async */
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                } else {
                    mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
