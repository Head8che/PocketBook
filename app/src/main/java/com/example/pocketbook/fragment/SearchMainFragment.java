package com.example.pocketbook.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.adapter.RequestAdapter;
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

import java.util.ArrayList;
import java.util.Objects;

public class SearchMainFragment extends Fragment {

    private static final String TAG = "SearchMainFragment";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private User currentUser;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private SearchView searchView;


    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;
    private int pos;

    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;


//    public SearchMainFragment() {
//        // Required empty public constructor
//    }

    /**
     * Displays list of books given a search query
     * @param user
     * @return
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

    boolean isKeyboardShowing = false;
    void onKeyboardVisibilityChanged(boolean keyboardIsShowing) {
        if(keyboardIsShowing) {
            Objects.requireNonNull(getActivity())
                    .findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(getActivity())
                    .findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_main,
                container, false);

        /*
          KEYBOARD CODE SOURCE: https://stackoverflow.com/questions/4745988/
          how-do-i-detect-if-software-keyboard-is-visible-on-android-device-or-not
         */
        // ContentView is the root view of the layout of this activity/fragment
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d(TAG, "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) {
                            // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        // TODO: see if possibility to switch
        mBooksRecycler = v.findViewById(R.id.search_recycler_books);
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));

        mAdapter = new LinearBookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);
    }

    public void updateQuery(String newText){
        // TODO: add batch loading
        // TODO: descriptive message when no books found

        newText = newText.toLowerCase();

        if(pos == 0) { // searching all books
            mQuery = mFirestore.collection("catalogue")
                    .whereArrayContains("keywords", newText);
        }
        else { // searching in available books only
            // TODO: create AvailableOrRequested variable in Firebase and Book Model
            mQuery = mFirestore.collection("catalogue")
                    .whereEqualTo("status", "AVAILABLE")
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



