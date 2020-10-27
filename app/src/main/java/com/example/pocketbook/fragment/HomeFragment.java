package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.UUID;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mBooksRecycler = v.findViewById(R.id.recycler_books);
        mAdapter = new BookAdapter(mQuery);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), numColumns));
        mBooksRecycler.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore and main RecyclerView
        initFirestore();
//        generateData();
//        initRecyclerView();
    }

    private void initFirestore(){
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all books
        mQuery = mFirestore.collection("books")
                .limit(LIMIT);
    }


//    private void generateData(){
//        // for demonstration purposes
//        for(int i=1; i<7; i++) {
//            String uniqueID = UUID.randomUUID().toString();
//            Book book = new Book(uniqueID, "Book "+i, "LeFabulous", uniqueID, "eden", "none", "available","none");
//            mFirestore.collection("books").document(book.getId()).set(book);
//        }
//    }

    private void retrieveData() {
        // for demonstration purposes
        // query to retrieve all books
        Query query = mFirestore.collection("books");

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