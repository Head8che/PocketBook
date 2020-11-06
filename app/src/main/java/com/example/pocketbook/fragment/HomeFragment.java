package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.ScrollUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Home Page fragment that contains a wide range of books on the platform
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HOME_ACTIVITY";
    private static final int NUM_COLUMNS = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;

    private User currentUser;
    private BookList catalogue;

    private ScrollUpdate scrollUpdate;

    /**
     * Home Page fragment instance that bundles the user/catalogue to be displayed
     * @param user
     * @param catalogue
     * @return
     */
    public static HomeFragment newInstance(User user, BookList catalogue) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("HF_USER", user);
        args.putSerializable("HF_CATALOGUE", catalogue);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("HF_USER");
            this.catalogue = (BookList) getArguments().getSerializable("HF_CATALOGUE");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue")
                .whereNotEqualTo("owner",currentUser.getEmail()).limit(LIMIT);
    }

    /**
     * Inflates the layout/container with the following (Layout and Books)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mBooksRecycler = v.findViewById(R.id.recycler_books);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), NUM_COLUMNS));
        mAdapter = new BookAdapter(currentUser, catalogue, getActivity());
        mBooksRecycler.setAdapter(mAdapter);

        scrollUpdate = new ScrollUpdate(catalogue, mQuery, mAdapter, mBooksRecycler);
        scrollUpdate.load();

        return v;
    }

}