package com.example.pocketbook.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadyForPickupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadyForPickupFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private User currentUser;
    private Fragment requestFragment = this;
    private boolean firstTimeFragLoads = true;

    FirestoreRecyclerOptions<Book> options;

    public static ReadyForPickupFragment newInstance(User user) {
        ReadyForPickupFragment readyForPickupfragment = new ReadyForPickupFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        readyForPickupfragment.setArguments(args);
        return readyForPickupfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue").whereEqualTo("owner",
                currentUser.getEmail()).whereEqualTo("status", "ACCEPTED")
                .limit(LIMIT);

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

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("VMBBF_LISTENER", "Listen failed.", e);
                return;
            }

            if ((snapshot != null) && snapshot.exists()) {
                currentUser = FirebaseIntegrity.getUserFromFirestore(snapshot);

                if (currentUser == null) {
                    return;
                }

                // TODO: Add isAdded to other listeners
                // if fragment can have a manager; tests crash without this line
                if ((!firstTimeFragLoads) && requestFragment.isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ReadyForPickupFragment.this)
                            .attach(ReadyForPickupFragment.this)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    firstTimeFragLoads = false;
                }
            }
            else if (requestFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(ReadyForPickupFragment.this).commitAllowingStateLoss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View rootView = inflater.inflate(R.layout.fragment_ready_for_pickup, container, false);
        mBooksRecycler = rootView.findViewById(R.id.readyForPickupBooksRecyclerBooks);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), numColumns));
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        mAdapter = new BookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);

        ImageView backButton = rootView.findViewById(R.id.readyForPickupBooksFragBackBtn);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}