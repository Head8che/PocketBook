package com.example.pocketbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewProfileFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private TextView firstLastName, userName;
    private TextView editProfile;
    private static final String USERS = "users";
    User userInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_profile_layout, container, false);
//        String userInfo = this.getArguments().getString("userEmail");

        database= FirebaseDatabase.getInstance();
        userRef = database.getReference(USERS);

        firstLastName = v.findViewById(R.id.first_last_name);
//        firstLastName.setText(userInfo);

        userName = v.findViewById(R.id.user_name);
        editProfile = v.findViewById(R.id.edit_profile_button);

        mBooksRecycler = v.findViewById(R.id.recycler_books);
        mAdapter = new BookAdapter(mQuery);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), numColumns));
        mBooksRecycler.setAdapter(mAdapter);



//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditProfileFragment nextFrag = new EditProfileFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container,nextFrag).commit();
//            }
//        });


//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot keyId : snapshot.getChildren()){
//                    if(keyId.child("email").getValue().equals(email)){
//                        firstLastName.setText(keyId.child("fullName").getValue(String.class));
//                        userName.setText(keyId.child("userName").getValue(String.class));
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

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