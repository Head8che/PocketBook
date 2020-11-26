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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.ViewAllBookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestedBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestedBooksFragment extends Fragment {

    private static final int numColumns = 2;
    private ViewAllBookAdapter mAdapter;
    private User currentUser;
    private boolean isOwnerTab;

    FirestoreRecyclerOptions<Book> options;

    public static RequestedBooksFragment newInstance(User user, boolean isOwnerTab) {
        RequestedBooksFragment requestedbooksfragment = new RequestedBooksFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        args.putSerializable("PF_IS_OWNER_TAB", isOwnerTab);
        requestedbooksfragment.setArguments(args);
        return requestedbooksfragment;
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
                    .whereEqualTo("status", "REQUESTED");
        } else {
            mQuery = FirebaseFirestore.getInstance().collection("catalogue")
                    .whereArrayContains("requesters", currentUser.getEmail())
                    .whereEqualTo("status", "REQUESTED");
        }

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

        View rootView = inflater.inflate(R.layout.fragment_requested_books,
                container, false);
        RecyclerView mBooksRecycler = rootView.findViewById(R.id.requestedBooksRecyclerBooks);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), numColumns));
        mAdapter = new ViewAllBookAdapter(options, currentUser, getActivity(), isOwnerTab);
        mBooksRecycler.setAdapter(mAdapter);

        ImageView backButton = rootView.findViewById(R.id.requestedBooksFragBackBtn);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                backButton.setClickable(false);
                getActivity().onBackPressed();
                backButton.setClickable(true);
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