package com.example.pocketbook.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Borrower Profile Page fragment that contains the user Profile (Books/Info)
 */
public class BorrowerFragment extends Fragment {

    private User currentUser;
    private FirebaseFirestore mFirestore;

    private static final int LIMIT = 20;

    private ProfileAdapter readyForPickupBookAdapter;
    private ProfileAdapter borrowedBookAdapter;
    private ProfileAdapter requestedBookAdapter;

    private Fragment ownerFragment = this;
    private boolean firstTimeFragLoads = true;
    private boolean isOwnerTab = false;


    /**
     * @param user User object
     * @return new instance of BorrowerFragment
     */
    public static BorrowerFragment newInstance(User user) {
        BorrowerFragment ownerFragment = new BorrowerFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        ownerFragment.setArguments(args);
        return ownerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the book argument passed to the newInstance() method
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
                            .detach(BorrowerFragment.this)
                            .attach(BorrowerFragment.this)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    firstTimeFragLoads = false;
                }
            }
            else if (ownerFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(BorrowerFragment.this).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_borrower, container, false);

        LinearLayout titleReadyForPickups
                = rootView.findViewById(R.id.TitleBarReadyForPickupBorrower);
        LinearLayout titleRequested = rootView.findViewById(R.id.TitleBarRequestedBorrower);
        LinearLayout titleBorrowed = rootView.findViewById(R.id.TitleBarBorrowedBorrower);

        TextView viewAllReadyForPickups
                = rootView.findViewById(R.id.ViewAllReadyForPickupBorrower);
        TextView viewAllRequestedBooks = rootView.findViewById(R.id.ViewAllRequestedBorrower);
        TextView viewAllBorrowedBooks = rootView.findViewById(R.id.ViewAllBorrowedBorrower);

        RecyclerView mReadyForPickupBooksRecycler
                = rootView.findViewById(R.id.profileBorrowerRecyclerReadyForPickupBooks);
        LinearLayoutManager readyForPickuplayoutManager
                = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve accepted books
        Query readyForPickupBooksQuery = mFirestore.collection("catalogue")
                .whereArrayContains("requesters", currentUser.getEmail())
                .whereEqualTo("status", "ACCEPTED").limit(LIMIT);

        mReadyForPickupBooksRecycler.setLayoutManager(readyForPickuplayoutManager);
        FirestoreRecyclerOptions<Book> readyForPickupOptions
                = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(readyForPickupBooksQuery, Book.class)
                .build();
        readyForPickupBookAdapter = new ProfileAdapter(readyForPickupOptions,
                currentUser, getActivity(), titleReadyForPickups, mReadyForPickupBooksRecycler);
        mReadyForPickupBooksRecycler.setAdapter(readyForPickupBookAdapter);

        RecyclerView mBorrowedBooksRecycler
                = rootView.findViewById(R.id.profileBorrowerRecyclerBorrowedBooks);
        LinearLayoutManager borrowedlayoutManager
                = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve borrowed books
        Query borrowedBooksQuery = mFirestore.collection("catalogue")
                .whereArrayContains("requesters", currentUser.getEmail())
                .whereEqualTo("status", "BORROWED").limit(LIMIT);

        mBorrowedBooksRecycler.setLayoutManager(borrowedlayoutManager);
        FirestoreRecyclerOptions<Book> borrowedOptions
                = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(borrowedBooksQuery, Book.class)
                .build();
        borrowedBookAdapter = new ProfileAdapter(borrowedOptions,
                currentUser, getActivity(), titleBorrowed, mBorrowedBooksRecycler);
        mBorrowedBooksRecycler.setAdapter(borrowedBookAdapter);

        RecyclerView mRequestedBooksRecycler
                = rootView.findViewById(R.id.profileBorrowerRecyclerRequestedBooks);
        LinearLayoutManager requestedlayoutManager
                = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        // query to retrieve requested books
        Query requestedBooksQuery = mFirestore.collection("catalogue")
                .whereArrayContains("requesters", currentUser.getEmail())
                .whereEqualTo("status", "REQUESTED").limit(LIMIT);

        mRequestedBooksRecycler.setLayoutManager(requestedlayoutManager);
        FirestoreRecyclerOptions<Book> requestedOptions
                = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requestedBooksQuery, Book.class)
                .build();
        requestedBookAdapter
                = new ProfileAdapter(requestedOptions, currentUser,
                getActivity(), titleRequested, mRequestedBooksRecycler);
        mRequestedBooksRecycler.setAdapter(requestedBookAdapter);

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

        viewAllReadyForPickups.setOnClickListener(v1 -> {
            viewAllReadyForPickups.setClickable(false);
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
            viewAllReadyForPickups.setClickable(true);
        });

        viewAllRequestedBooks.setOnClickListener(v1 -> {
            viewAllRequestedBooks.setClickable(false);
            RequestedBooksFragment requestedBooksFragment
                    = RequestedBooksFragment.newInstance(currentUser, isOwnerTab);
            Bundle bundle = new Bundle();
            bundle.putSerializable("PF_USER", currentUser);
            bundle.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
            requestedBooksFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction()
                    .replace(getActivity().findViewById(R.id.container).getId(),
                            requestedBooksFragment)
                    .addToBackStack(null).commit();
            viewAllRequestedBooks.setClickable(true);
        });

        viewAllBorrowedBooks.setOnClickListener(v1 -> {
            viewAllBorrowedBooks.setClickable(false);
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
            viewAllBorrowedBooks.setClickable(true);
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        readyForPickupBookAdapter.startListening();
        borrowedBookAdapter.startListening();
        requestedBookAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        readyForPickupBookAdapter.stopListening();
        borrowedBookAdapter.stopListening();
        requestedBookAdapter.stopListening();
    }
}
