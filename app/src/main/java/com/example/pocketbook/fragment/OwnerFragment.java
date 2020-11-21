package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.adapter.ProfileAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.ScrollUpdate;
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
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Owner Profile Page fragment that contains the user Profile (Books/Info)
 */
public class OwnerFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query requestedbooksQuery, ownedbooksQuery;
    private RecyclerView mReadyForPickupBooksRecycler, mRequestedBooksRecycler, mOwnedBooksRecycler;
    private ProfileAdapter requestedbookAdapter, ownedbookAdapter;
    private TextView profileName, userName;
    private TextView editProfile, readyForPickups, requestedBooks, borrowedBooks, ownedBooks;
    private static final String USERS = "users";
    private User currentUser;
    private ScrollUpdate scrollUpdate;
    private Fragment ownerFragment = this;
    private boolean firstTimeFragLoads = true;
    private String ownerrequestedBooks = "REQUESTED";

    FirestoreRecyclerOptions<Book> requestedbookOptions, ownedbookOptions;
    ListenerRegistration listenerRegistration;

    /**
     * Owner Profile fragment instance that bundles the user information to be accessible/displayed
     * @param user
     * @return
     */
    public static OwnerFragment newInstance(User user) {
        OwnerFragment ownerFragment = new OwnerFragment();
        ProfileFragment profileFragment = new ProfileFragment();
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
        // Query to retrieve
        requestedbooksQuery = mFirestore.collection("catalogue").whereEqualTo("owner",currentUser.getEmail()).whereEqualTo("status",ownerrequestedBooks).limit(LIMIT);
        ownedbooksQuery = mFirestore.collection("catalogue").whereEqualTo("owner",currentUser.getEmail()).limit(LIMIT);

        requestedbookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedbooksQuery, Book.class)
                .build();

        ownedbookOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(ownedbooksQuery, Book.class)
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

                                requestedbookAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("SCROLL_UPDATE", "Modified doc: " + document);

                                requestedbookAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("SCROLL_UPDATE", "Removed doc: " + document);

                                requestedbookAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        };

        listenerRegistration = requestedbooksQuery.addSnapshotListener(dataListener);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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
        View v = inflater.inflate(R.layout.fragment_profile_existing_user, container, false);

        mRequestedBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerRequestedBooks);
        LinearLayoutManager requestedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRequestedBooksRecycler.setLayoutManager(requestedlayoutManager);
        FirestoreRecyclerOptions<Book> requestedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedbooksQuery, Book.class)
                .build();
        requestedbookAdapter = new ProfileAdapter(requestedOptions, currentUser,getActivity());
        mRequestedBooksRecycler.setAdapter(requestedbookAdapter);

        mOwnedBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerOwnedBooks);
        LinearLayoutManager ownedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mOwnedBooksRecycler.setLayoutManager(ownedlayoutManager);
        FirestoreRecyclerOptions<Book> ownedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(ownedbooksQuery, Book.class)
                .build();
        ownedbookAdapter = new ProfileAdapter(ownedOptions, currentUser,getActivity());
        mOwnedBooksRecycler.setAdapter(ownedbookAdapter);




        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);

        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        // TODO: obtain user_photo from firebase
        String user_Pic = currentUser.getPhoto();
        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
        TextView ProfileName = (TextView) v.findViewById(R.id.profileName);
        TextView UserName = (TextView) v.findViewById(R.id.user_name);
        ProfileName.setText(first_Name + ' ' + last_Name);
        UserName.setText(user_Name);

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(userProfilePicture)
                .circleCrop()
                .into(profilePicture);

        editProfile = v.findViewById(R.id.edit_profile_button);
        readyForPickups = v.findViewById(R.id.ViewAllReadyForPickup);
        requestedBooks = v.findViewById(R.id.ViewAllRequested);
        borrowedBooks = v.findViewById(R.id.ViewAllBorrowed);
        ownedBooks = v.findViewById(R.id.ViewAllOwned);


//        scrollUpdate = new ScrollUpdate(mQuery, mAdapter, mBooksRecycler);
//        scrollUpdate.load();

        readyForPickups.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Fragment someFragment = new ReadyForPickupFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        requestedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                RequestedBooksFragment requestedbooksfragment = RequestedBooksFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, requestedbooksfragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        borrowedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Fragment someFragment = new BorrowedBooksFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        ownedBooks.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                OwnedBookFragment ownedbookfragment = OwnedBookFragment.newInstance(currentUser);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, ownedbookfragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        return v;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        listenerRegistration.remove();
//    }

    @Override
    public void onStart() {
        super.onStart();
        requestedbookAdapter.startListening();
        ownedbookAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestedbookAdapter.stopListening();
        ownedbookAdapter.stopListening();
    }
}
