package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.ProfileAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * Owner Profile Page fragment that contains the user Profile (Books/Info)
 */
public class OwnerFragment extends Fragment {
    private static final int LIMIT = 8;
    private FirebaseFirestore mFirestore;

    private ProfileAdapter readyForPickupBookAdapter;
    private ProfileAdapter borrowedBookAdapter;
    private ProfileAdapter requestedBookAdapter;
    private ProfileAdapter ownedBookAdapter;

    private User currentUser;
    private Fragment ownerFragment = this;
    private boolean firstTimeFragLoads = true;
    private boolean isOwnerTab = true;

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
        View rootView = inflater.inflate(R.layout.fragment_owner, container, false);

        LinearLayout titleReadyForPickups = rootView.findViewById(R.id.TitleBarReadyForPickupOwner);
        LinearLayout titleRequested = rootView.findViewById(R.id.TitleBarRequestedOwner);
        LinearLayout titleBorrowed = rootView.findViewById(R.id.TitleBarBorrowedOwner);
        LinearLayout titleOwned = rootView.findViewById(R.id.TitleBarOwnedOwner);

        TextView viewAllReadyForPickups = rootView.findViewById(R.id.ViewAllReadyForPickupOwner);
        TextView viewAllRequestedBooks = rootView.findViewById(R.id.ViewAllRequestedOwner);
        TextView viewAllBorrowedBooks = rootView.findViewById(R.id.ViewAllBorrowedOwner);
        TextView viewAllOwnedBooks = rootView.findViewById(R.id.ViewAllOwnedOwner);

        RecyclerView mReadyForPickupBooksRecycler = rootView.findViewById(R.id.profileOwnerRecyclerReadyForPickupBooks);
        LinearLayoutManager readyForPickuplayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve accepted books
        Query readyForPickupBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "ACCEPTED").limit(LIMIT);

        mReadyForPickupBooksRecycler.setLayoutManager(readyForPickuplayoutManager);
        FirestoreRecyclerOptions<Book> readyForPickupOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(readyForPickupBooksQuery, Book.class)
                .build();
        readyForPickupBookAdapter = new ProfileAdapter(readyForPickupOptions, currentUser, getActivity(), titleReadyForPickups, mReadyForPickupBooksRecycler);
        mReadyForPickupBooksRecycler.setAdapter(readyForPickupBookAdapter);

        RecyclerView mBorrowedBooksRecycler = rootView.findViewById(R.id.profileOwnerRecyclerBorrowedBooks);
        LinearLayoutManager borrowedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve borrowed books
        Query borrowedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "BORROWED").limit(LIMIT);

        mBorrowedBooksRecycler.setLayoutManager(borrowedlayoutManager);
        FirestoreRecyclerOptions<Book> borrowedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(borrowedBooksQuery, Book.class)
                .build();
        borrowedBookAdapter = new ProfileAdapter(borrowedOptions, currentUser, getActivity(), titleBorrowed, mBorrowedBooksRecycler);
        mBorrowedBooksRecycler.setAdapter(borrowedBookAdapter);

        RecyclerView mRequestedBooksRecycler = rootView.findViewById(R.id.profileOwnerRecyclerRequestedBooks);
        LinearLayoutManager requestedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve requested books
        Query requestedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner",currentUser.getEmail())
                .whereEqualTo("status", "REQUESTED").limit(LIMIT);

        mRequestedBooksRecycler.setLayoutManager(requestedlayoutManager);
        FirestoreRecyclerOptions<Book> requestedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedBooksQuery, Book.class)
                .build();
        requestedBookAdapter = new ProfileAdapter(requestedOptions, currentUser, getActivity(), titleRequested, mRequestedBooksRecycler);
        mRequestedBooksRecycler.setAdapter(requestedBookAdapter);

        RecyclerView mOwnedBooksRecycler = rootView.findViewById(R.id.profileOwnerRecyclerOwnedBooks);
        LinearLayoutManager ownedlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve owned books
        Query ownedBooksQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner", currentUser.getEmail()).limit(LIMIT);

        mOwnedBooksRecycler.setLayoutManager(ownedlayoutManager);
        FirestoreRecyclerOptions<Book> ownedOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(ownedBooksQuery, Book.class)
                .build();
        ownedBookAdapter = new ProfileAdapter(ownedOptions, currentUser, getActivity(), titleOwned, mOwnedBooksRecycler);
        mOwnedBooksRecycler.setAdapter(ownedBookAdapter);

        Log.e("OWN", mOwnedBooksRecycler.getChildCount() + " " + ownedBookAdapter.getItemCount());

        // initially hide views if they have no content
        if (readyForPickupOptions.getSnapshots().size() == 0) {
            titleReadyForPickups.setVisibility(View.GONE);
            mReadyForPickupBooksRecycler.setVisibility(View.GONE);
        }
        if (requestedOptions.getSnapshots().size() == 0) {
            titleRequested.setVisibility(View.GONE);
            mRequestedBooksRecycler.setVisibility(View.GONE);
        }
        if (borrowedOptions.getSnapshots().size() == 0) {
            titleBorrowed.setVisibility(View.GONE);
            mBorrowedBooksRecycler.setVisibility(View.GONE);
        }
        if (ownedOptions.getSnapshots().size() == 0) {
            titleOwned.setVisibility(View.GONE);
            mOwnedBooksRecycler.setVisibility(View.GONE);
        }

        viewAllReadyForPickups.setOnClickListener(v1 -> {

            ReadyForPickupFragment readyForPickupFragment
                    = ReadyForPickupFragment.newInstance(currentUser, isOwnerTab);
            Bundle bundle = new Bundle();
            bundle.putSerializable("PF_USER", currentUser);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            readyForPickupFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction()
                    .replace(getActivity().findViewById(R.id.container).getId(),
                            readyForPickupFragment)
                    .addToBackStack(null).commit();

        });

        viewAllRequestedBooks.setOnClickListener(v1 -> {

            RequestedBooksFragment requestedBooksFragment
                    = RequestedBooksFragment.newInstance(currentUser, isOwnerTab);
            Bundle bundle = new Bundle();
            bundle.putSerializable("PF_USER", currentUser);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            requestedBooksFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction()
                    .replace(getActivity().findViewById(R.id.container).getId(),
                            requestedBooksFragment)
                    .addToBackStack(null).commit();

        });

        viewAllBorrowedBooks.setOnClickListener(v1 -> {

            BorrowedBooksFragment borrowedBooksFragment
                    = BorrowedBooksFragment.newInstance(currentUser, isOwnerTab);
            Bundle bundle = new Bundle();
            bundle.putSerializable("PF_USER", currentUser);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            borrowedBooksFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction()
                    .replace(getActivity().findViewById(R.id.container).getId(),
                            borrowedBooksFragment)
                    .addToBackStack(null).commit();

        });

        viewAllOwnedBooks.setOnClickListener(v1 -> {

            OwnedBookFragment ownedBookFragment
                    = OwnedBookFragment.newInstance(currentUser);
            Bundle bundle = new Bundle();
            bundle.putSerializable("PF_USER", currentUser);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            ownedBookFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction()
                    .replace(getActivity().findViewById(R.id.container).getId(),
                            ownedBookFragment)
                    .addToBackStack(null).commit();

        });


        return rootView;
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
