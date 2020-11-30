package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Displays list of books given a search query
 * A {@link Fragment} subclass.
 * Use the {@link #newInstance(User,int) newInstance}  method to create an instance of this fragment.
 */
public class SearchMainFragment extends Fragment {

    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private User currentUser;
    private int pos;

    private FirestoreRecyclerOptions<Book> options;
    private ListenerRegistration listenerRegistration;

    /**
     * Required empty public constructor
     */
    public SearchMainFragment() {

    }

    /**
     * Method to create a new instance of SearchMainFragment
     * @param user current user as a User object
     * @param position int
     * @return a new instance of SearchMainFragment
     */
    public static SearchMainFragment newInstance(User user, int position) {
        SearchMainFragment searchMainFragment = new SearchMainFragment();
        Bundle args = new Bundle();
        args.putSerializable("SF_USER", user);
        args.putInt("type", position);
        searchMainFragment.setArguments(args);
        return searchMainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the book argument passed to the newInstance() method
        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("SF_USER");
            this.pos = getArguments().getInt("type");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Retrieving books that do not belong to user
        mQuery = mFirestore.collection("catalogue")
                .whereNotEqualTo("owner",currentUser.getEmail()).limit(LIMIT);

        options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();

        EventListener<QuerySnapshot> dataListener = (snapshots, error) -> {
            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (error != null) {
                        Log.e("SCROLL_UPDATE_ERROR", "Listen failed.", error);
                        return;
                    }

                    DocumentSnapshot document = dc.getDocument();

                    Book book = FirebaseIntegrity.getBookFromFirestore(document);

                    if (book != null) {

                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("SCROLL_UPDATE", "New doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("SCROLL_UPDATE", "Modified doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("SCROLL_UPDATE", "Removed doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        mBooksRecycler = v.findViewById(R.id.search_recycler_books);
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));

        mAdapter = new LinearBookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(null);
    }

    public void updateQuery(String newText){


        newText = newText.toLowerCase();

        if (newText.equals("")) {
            mBooksRecycler.setAdapter(null);
            return;
        }

        if(pos == 0) { // searching all books
            mQuery = mFirestore.collection("catalogue")
                    .whereArrayContains("keywords", newText);
        }
        else { // searching in available books only

            mQuery = mFirestore.collection("catalogue")
                    .whereEqualTo("nonExchange", true)
                    .whereArrayContains("keywords", newText);
        }
        // Stop listening
        mAdapter.stopListening();

        // reset adapter and recycler
        options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        mAdapter = new LinearBookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);

        // Listen to new query
        mAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Override
    public void onStart() {
        super.onStart();

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



