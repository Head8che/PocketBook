package com.example.pocketbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * splashscreen(Intro loading Screen to the PocketBook App)
 */
public class SplashScreenActivity extends AppCompatActivity {

    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        FirebaseIntegrity.copyObjectBetweenCollectionsFirebase("test_catalogue", "Book", "cataloguer");
//        FirebaseIntegrity.cleanObjectCollection("Book");

        /* UNCOMMENT OUT THE LINE BELOW TO ACCESS LOGIN PAGE */
//         FirebaseAuth.getInstance().signOut();

        /* Duration of wait in milliseconds */
        int SPLASH_DISPLAY_LENGTH = 1500;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* keep intent in condition because auth could be async */
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mainIntent = new Intent(getApplicationContext(), HomeActivity.class);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection("users").document(Objects.requireNonNull(user.getEmail()));

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User currentUser = FirebaseIntegrity.getUserFromFirestore(document);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("CURRENT_USER", currentUser);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("SPLASH", "No such document");
                                }
                            } else {
                                Log.d("SPLASH", "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
