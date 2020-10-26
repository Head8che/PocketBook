package com.example.pocketbook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

//    private static final int numColumns = 2;
//    private static final int LIMIT = 20;
//
//    private FirebaseFirestore mFirestore;
//    private Query mQuery;
//
//    private RecyclerView mBooksRecycler;
//    private BookAdapter mAdapter;
//
//    Button viewMyBookBtn;

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
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                if(email.isEmpty()){
                    userEmail.setError("Need an email!");
                    userEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    userPassword.setError("Password Required");
                    userPassword.requestFocus();
                    return;
                }
                Register(email,password);

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });


//        mBooksRecycler = findViewById(R.id.recycler_books);

//        viewMyBookBtn = findViewById(R.id.viewMyBookBtn);
//
//        viewMyBookBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ViewMyBookActivity.class);
//                startActivity(intent);
//            }
//        });

//        // Initialize Firestore and main RecyclerView
//        initFirestore();
////        generateData();
//        initRecyclerView();
//    }
//
//    private void initFirestore(){
//        mFirestore = FirebaseFirestore.getInstance();
//
//        // Query to retrieve all books
//        mQuery = mFirestore.collection("books")
//                .limit(LIMIT);
//    }
//
//    private void initRecyclerView() {
//        if (mQuery == null)
//            Log.d(TAG, "No query, not initializing RecyclerView");
//
//        mAdapter = new BookAdapter(mQuery){
//            @Override
//            protected void onDataChanged() {
//                // Show/hide content if the query returns empty.
//                if (getItemCount() == 0)
//                    mBooksRecycler.setVisibility(View.GONE);
//                else
//                    mBooksRecycler.setVisibility(View.VISIBLE);
//            }
//        };
//
//        // use a linear layout manager
//        mBooksRecycler.setLayoutManager(new GridLayoutManager(this, numColumns));
//        mBooksRecycler.setAdapter(mAdapter);
//    }
//
//    private void generateData(){
//        // for demonstration purposes
//        for(int i=1; i<7; i++) {
//            String uniqueID = UUID.randomUUID().toString();
//            Book book = new Book(uniqueID, "Book "+i, "LeFabulous", uniqueID, "eden", "none", "available","none");
//            mFirestore.collection("books").document(book.getId()).set(book);
//        }
//    }
//
//    private void retrieveData() {
//        // for demonstration purposes
//        // query to retrieve all books
//        Query query = mFirestore.collection("books");
//
//        // Getting data once
////        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if(task.isSuccessful()){
////                    for(QueryDocumentSnapshot document : task.getResult())
////                        Log.d(TAG, document.getId() + " => " + document.getData());
////                } else
////                    Log.d(TAG, "Error getting documents: ", task.getException());
////            }
////        });
//
//        // Getting data real time
//        // listens to multiple documents
//        // differentiates what kind of changes happened
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//                for (DocumentChange dc : snapshot.getDocumentChanges()){
//                    switch (dc.getType()) {
//                        case ADDED:
//                            Log.d(TAG, "New book: " + dc.getDocument().getData());
//                            break;
//                        case MODIFIED:
//                            Log.d(TAG, "Modified book: " + dc.getDocument().getData());
//                            break;
//                        case REMOVED:
//                            Log.d(TAG, "Removed book: " + dc.getDocument().getData());
//                            break;
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Start listening for Firestore updates
//        if (mAdapter != null) {
//            mAdapter.startListening();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
    }

    public void Register(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Welcome to Pocketbook.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), ViewMyBookActivity.class);
                            startActivity(intent);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to Login.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

}