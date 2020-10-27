package com.example.pocketbook.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.content.Intent;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signUp, login, forgotPass;
    private EditText userEmail, userPassword;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp = findViewById(R.id.RegisterBtn);
        login = findViewById(R.id.LoginBtn);
        forgotPass = findViewById(R.id.ForgotPass);
        userEmail = findViewById(R.id.UserReg);
        userPassword = findViewById(R.id.PasswordReg);

        mAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
                // the book UID will actually be gotten onClick, but it's hardcoded for now
                intent.putExtra("FIRESTORE_BOOK_UID", "fBtIHykc3KqxgPmwlNYO");
                startActivity(intent);
            }
        });

        // Initialize Firestore and main RecyclerView
//        initFirestore();
////        generateData();
//        initRecyclerView();

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        if (email.isEmpty()) {
            userEmail.setError("Need an email!");
            userEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            userPassword.setError("Password Required");
            userPassword.requestFocus();
            return;
        }
        Register(email, password);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });
    }

//    private void generateData(){
//        // for demonstration purposes
//        for(int i=1; i<7; i++) {
//            String uniqueID = UUID.randomUUID().toString();
//            Book book = new Book(uniqueID, "Book "+i, "LeFabulous", uniqueID, "eden", "none", "available","none");
//            mFirestore.collection("books").document(book.getId()).set(book);
//        }
//    }


    public void Register(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Welcome to Pocketbook.",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), ViewMyBookActivity.class);
                            startActivity(intent);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to Login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}