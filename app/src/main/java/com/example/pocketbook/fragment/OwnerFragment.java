package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.adapter.ProfileAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.ScrollUpdate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owner Profile Page fragment that contains the user Profile (Books/Info)
 */
public class OwnerFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;

    private Query readyForPickupBooksQuery;
    private Query borrowedBooksQuery;
    private Query requestedBooksQuery;
    private Query ownedBooksQuery;

    private RecyclerView mReadyForPickupBooksRecycler;
    private RecyclerView mBorrowedBooksRecycler;
    private RecyclerView mRequestedBooksRecycler;
    private RecyclerView mOwnedBooksRecycler;

    private ProfileAdapter readyForPickupBookAdapter;
    private ProfileAdapter borrowedBookAdapter;
    private ProfileAdapter requestedBookAdapter;
    private ProfileAdapter ownedBookAdapter;

    private static final String USERS = "users";
    private User currentUser;
    private ScrollUpdate scrollUpdate;
    private Fragment ownerFragment = this;
    private boolean firstTimeFragLoads = true;

    FirestoreRecyclerOptions<Book> readyForPickupBookOptions;
    FirestoreRecyclerOptions<Book> borrowedBookOptions;
    FirestoreRecyclerOptions<Book> requestedBookOptions;
    FirestoreRecyclerOptions<Book> ownedBookOptions;

    /**
     * Owner Profile fragment instance that bundles the user information to be accessible/displayed
     * @param user
     * @return
     */
    public static OwnerFragment newInstance(User user) {
        OwnerFragment ownerFragment = new OwnerFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        ownerFragment.setArguments(args);
        return ownerFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // query to retrieve accepted books
        readyForPickupBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "ACCEPTED").limit(LIMIT);

        // query to retrieve borrowed books
        borrowedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "BORROWED").limit(LIMIT);

        // query to retrieve requested books
        requestedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "REQUESTED").limit(LIMIT);

        // query to retrieve requested books
        ownedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner", currentUser.getEmail()).limit(LIMIT);

        readyForPickupBookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(readyForPickupBooksQuery, Book.class)
                .build();

        borrowedBookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(borrowedBooksQuery, Book.class)
                .build();

        requestedBookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedBooksQuery, Book.class)
                .build();

        ownedBookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(ownedBooksQuery, Book.class)
                .build();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("OWNER_LISTENER", "Listen failed.", e);
                return;
            }

            if ((snapshot != null) && snapshot.exists()) {
                currentUser = FirebaseIntegrity.getUserFromFirestore(snapshot);

                if (currentUser == null) {
                    return;
                }

                // TODO: Add isAdded to other listeners
                // if fragment can have a manager; tests crash without this line
                if ((!firstTimeFragLoads) && ownerFragment.isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(OwnerFragment.this)
                            .attach(OwnerFragment.this)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    firstTimeFragLoads = false;
                }
            }
            else if (ownerFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(OwnerFragment.this).commitAllowingStateLoss();
            }
        });

    }

    /**
     * Inflates the layout/container in the respectful fields and fills
     * the fields that require the owner information to be displayed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_owner, container, false);

        LinearLayout titleReadyForPickups = v.findViewById(R.id.TitleBarReadyForPickupOwner);
        LinearLayout titleRequested = v.findViewById(R.id.TitleBarRequestedOwner);
        LinearLayout titleBorrowed = v.findViewById(R.id.TitleBarBorrowedOwner);
        LinearLayout titleOwned = v.findViewById(R.id.TitleBarOwnedOwner);

        TextView viewAllReadyForPickups = v.findViewById(R.id.ViewAllReadyForPickupOwner);
        TextView viewAllRequestedBooks = v.findViewById(R.id.ViewAllRequestedOwner);
        TextView viewAllBorrowedBooks = v.findViewById(R.id.ViewAllBorrowedOwner);
        TextView viewAllOwnedBooks = v.findViewById(R.id.ViewAllOwnedOwner);

        mReadyForPickupBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerReadyForPickupBooks);
        LinearLayoutManager readyForPickuplayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mReadyForPickupBooksRecycler.setLayoutManager(readyForPickuplayoutManager);
        FirestoreRecyclerOptions<Book> readyForPickupOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(readyForPickupBooksQuery, Book.class)
                .build();
        readyForPickupBookAdapter = new ProfileAdapter(readyForPickupOptions, currentUser, getActivity(), titleReadyForPickups, mReadyForPickupBooksRecycler);
        mReadyForPickupBooksRecycler.setAdapter(readyForPickupBookAdapter);

        mBorrowedBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerBorrowedBooks);
        LinearLayoutManager borrowedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBorrowedBooksRecycler.setLayoutManager(borrowedlayoutManager);
        FirestoreRecyclerOptions<Book> borrowedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(borrowedBooksQuery, Book.class)
                .build();
        borrowedBookAdapter = new ProfileAdapter(borrowedOptions, currentUser, getActivity(), titleBorrowed, mBorrowedBooksRecycler);
        mBorrowedBooksRecycler.setAdapter(borrowedBookAdapter);

        mRequestedBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerRequestedBooks);
        LinearLayoutManager requestedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRequestedBooksRecycler.setLayoutManager(requestedlayoutManager);
        FirestoreRecyclerOptions<Book> requestedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedBooksQuery, Book.class)
                .build();
        requestedBookAdapter = new ProfileAdapter(requestedOptions, currentUser, getActivity(), titleRequested, mRequestedBooksRecycler);
        mRequestedBooksRecycler.setAdapter(requestedBookAdapter);

        mOwnedBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerOwnedBooks);
        LinearLayoutManager ownedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mOwnedBooksRecycler.setLayoutManager(ownedlayoutManager);
        FirestoreRecyclerOptions<Book> ownedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(ownedBooksQuery, Book.class)
                .build();
        ownedBookAdapter = new ProfileAdapter(ownedOptions, currentUser, getActivity(), titleOwned, mOwnedBooksRecycler);
        mOwnedBooksRecycler.setAdapter(ownedBookAdapter);

//        scrollUpdate = new ScrollUpdate(mQuery, mAdapter, mBooksRecycler);
//        scrollUpdate.load();

        Log.e("OWN", mOwnedBooksRecycler.getChildCount() + " " + ownedBookAdapter.getItemCount());

        // initially hide views if they have no content
//        if (readyForPickupOptions.getSnapshots().size() == 0) {
//            titleReadyForPickups.setVisibility(View.GONE);
//            mReadyForPickupBooksRecycler.setVisibility(View.GONE);
//        }
//        if (requestedOptions.getSnapshots().size() == 0) {
//            titleRequested.setVisibility(View.GONE);
//            mRequestedBooksRecycler.setVisibility(View.GONE);
//        }
//        if (borrowedOptions.getSnapshots().size() == 0) {
//            titleBorrowed.setVisibility(View.GONE);
//            mBorrowedBooksRecycler.setVisibility(View.GONE);
//        }
//        if (ownedOptions.getSnapshots().size() == 0) {
//            titleOwned.setVisibility(View.GONE);
//            mOwnedBooksRecycler.setVisibility(View.GONE);
//        }

        viewAllReadyForPickups.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Fragment someFragment = ReadyForPickupFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        viewAllRequestedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                RequestedBooksFragment requestedBooksfragment = RequestedBooksFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, requestedBooksfragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        viewAllBorrowedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Fragment someFragment = BorrowedBooksFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        viewAllOwnedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                OwnedBookFragment ownedBookfragment = OwnedBookFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, ownedBookfragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        readyForPickupBookAdapter.startListening();
        borrowedBookAdapter.startListening();
        requestedBookAdapter.startListening();
        ownedBookAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        readyForPickupBookAdapter.stopListening();
        borrowedBookAdapter.stopListening();
        requestedBookAdapter.stopListening();
        ownedBookAdapter.stopListening();
    }
}
