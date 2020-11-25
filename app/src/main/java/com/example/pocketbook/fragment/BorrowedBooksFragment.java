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
import com.example.pocketbook.adapter.ViewAllBookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BorrowedBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BorrowedBooksFragment extends Fragment {

    private static final int numColumns = 2;
    private ViewAllBookAdapter mAdapter;
    private User currentUser;
    private boolean isOwnerTab;

    FirestorePagingOptions<Book> options;

    public static BorrowedBooksFragment newInstance(User user, boolean isOwnerTab) {
        BorrowedBooksFragment borrowedBooksfragment = new BorrowedBooksFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        args.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
        borrowedBooksfragment.setArguments(args);
        return borrowedBooksfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
            this.isOwnerTab = (boolean) getArguments().getSerializable("PF_IS_OWNER_TAB");
        }

        Query mQuery;
        if (isOwnerTab) {
            mQuery = FirebaseFirestore.getInstance().collection("catalogue")
                    .whereEqualTo("owner",currentUser.getEmail())
                    .whereEqualTo("status", "BORROWED");
        } else {
            mQuery = FirebaseFirestore.getInstance().collection("catalogue")
                    .whereArrayContains("requesters", currentUser.getEmail())
                    .whereEqualTo("status", "BORROWED");
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(4)
                .setPageSize(4).build();

        options = new FirestorePagingOptions.Builder<Book>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Book.class).build();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View rootView = inflater.inflate(R.layout.fragment_borrowed_books,
                container, false);
        RecyclerView mBooksRecycler = rootView.findViewById(R.id.borrowedBooksRecyclerBooks);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), numColumns));
        mAdapter = new ViewAllBookAdapter(options, currentUser, getActivity(), isOwnerTab);
        mBooksRecycler.setAdapter(mAdapter);

        ImageView backButton = rootView.findViewById(R.id.borrowedBooksFragBackBtn);

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