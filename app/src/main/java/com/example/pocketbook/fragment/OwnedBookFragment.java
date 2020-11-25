package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.adapter.ViewAllBookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnedBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnedBookFragment extends Fragment {

    private static final int numColumns = 2;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private ViewAllBookAdapter mAdapter;
    private User currentUser;

    FirestoreRecyclerOptions<Book> options;

    public static OwnedBookFragment newInstance(User user) {
        OwnedBookFragment ownedbookfragment = new OwnedBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        ownedbookfragment.setArguments(args);
        return ownedbookfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // retrieving owned books
        mQuery = mFirestore.collection("catalogue")
                .whereEqualTo("owner", currentUser.getEmail());

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(4)
                .setPageSize(4).build();

        options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View rootView = inflater.inflate(R.layout.fragment_owned_book,
                container, false);
        mBooksRecycler = rootView.findViewById(R.id.ownedBooksRecyclerBooks);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), numColumns));
        mAdapter = new ViewAllBookAdapter(options, currentUser, getActivity(), true);
        mBooksRecycler.setAdapter(mAdapter);

        ImageView backButton = rootView.findViewById(R.id.ownedBooksFragBackBtn);

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