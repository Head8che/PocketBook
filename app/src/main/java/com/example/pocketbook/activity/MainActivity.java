package com.example.pocketbook.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;

    Button viewMyBookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBooksRecycler = findViewById(R.id.recycler_books);

        viewMyBookBtn = findViewById(R.id.viewMyBookBtn);

        viewMyBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMyBookActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Firestore and main RecyclerView
        initFirestore();
        addData();
        initRecyclerView();
    }

    private void initFirestore(){
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all books
        mQuery = mFirestore.collection("Books")
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null)
            Log.d(TAG, "No query, not initializing RecyclerView");

        mAdapter = new BookAdapter(mQuery){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0)
                    mBooksRecycler.setVisibility(View.GONE);
                else
                    mBooksRecycler.setVisibility(View.VISIBLE);
            }
        };

        // use a linear layout manager
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBooksRecycler.setAdapter(mAdapter);
    }

    private void addData(){
        // for demonstration purposes
        Book b1 = new Book("Harry Potter", "JK", "11111", "Eden", "Great book, I never read", "Owned", "none");
        Book b2 = new Book("Great Gatsby", "Fitzgerald", "22222", "Eden", "Best book on the planet", "Owned", "none");

        mFirestore.collection("Books").document(b1.getISBN()).set(b1); // overwrites B01
        mFirestore.collection("Books").document(b2.getISBN()).set(b2);
//        mFirestore.collection("Books").add(b3); // creates new document
    }

    private void retrieveData() {
        // for demonstration purposes
        // query to retrieve all books
        Query query = mFirestore.collection("Books");

        // Getting data once
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult())
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                } else
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//            }
//        });

        // Getting data real time
        // listens to multiple documents
        // differentiates what kind of changes happened
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for (DocumentChange dc : snapshot.getDocumentChanges()){
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New book: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified book: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed book: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}