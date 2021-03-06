package com.example.pocketbook.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.RequestAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
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

/**
 /**
 * Allows users to view requests for a book they own
 * A {@link Fragment} subclass.
 * Use the {@link #newInstance(Book) newInstance} method to create an instance of this fragment.
 */
public class ViewMyBookRequestsFragment extends Fragment {

    private RequestAdapter requestAdapter;
    private Book book;

    private FirestoreRecyclerOptions<Request> options;
    private ListenerRegistration listenerRegistration;
    private ListenerRegistration bookListenerRegistration;


    public ViewMyBookRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of ViewMyBookRequestsFragment
     * @param book : the book being viewed
     * @return ViewMyBookRequestsFragment
     */
    public static ViewMyBookRequestsFragment newInstance(Book book) {
        ViewMyBookRequestsFragment viewMyBookRequestsFragment = new ViewMyBookRequestsFragment();
        Bundle args = new Bundle();
        args.putSerializable("VMBPA_BOOK", book);
        viewMyBookRequestsFragment.setArguments(args);
        return viewMyBookRequestsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the book argument passed to the newInstance() method
        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("VMBPA_BOOK");
        }

        // Initialize Firestore
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all book requests
        Query mQuery = mFirestore.collection("catalogue").document(book.getId())
                .collection("requests");

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(mQuery, Request.class)
                .build();

        EventListener<QuerySnapshot> dataListener = (snapshots, error) -> {
            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (error != null) {
                        Log.e("REQUEST_SCROLL_UPDATE_ERROR", "Listen failed.", error);
                        return;
                    }

                    DocumentSnapshot document = dc.getDocument();

                    if (book != null) {

                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("REQUEST_SCROLL_UPDATE", "New doc: "
                                        + document);

                                requestAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("REQUEST_SCROLL_UPDATE", "Modified doc: "
                                        + document);

                                requestAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("REQUEST_SCROLL_UPDATE", "Removed doc: "
                                        + document);

                                requestAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);

        bookListenerRegistration = FirebaseFirestore
                .getInstance().collection("catalogue")
                .document(book.getId()).addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("VMBBF_LISTENER", "Listen failed.", e);
                        return;
                    }

                    if ((snapshot != null) && snapshot.exists()) {
                        book = FirebaseIntegrity.getBookFromFirestore(snapshot);

                        getParentFragmentManager()
                                .beginTransaction()
                                .detach(ViewMyBookRequestsFragment.this)
                                .attach(ViewMyBookRequestsFragment.this)
                                .commitAllowingStateLoss();
                    } else {
                        if ( getActivity() == null) {
                            getParentFragmentManager().beginTransaction()
                                    .detach(ViewMyBookRequestsFragment.this)
                                    .commitAllowingStateLoss();
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }


                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_my_book_requests,
                container, false);

        //get the id of the recycler for this layout and set its layout manager
        RecyclerView requestsRecycler
                = view.findViewById(R.id.viewMyBookRequestsRecyclerView);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //set a new requestAdapter as an adapter for the recycler
        requestAdapter = new RequestAdapter(options, this.book, getActivity());
        requestsRecycler.setAdapter(requestAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
        bookListenerRegistration.remove();
    }

    @Override
    public void onStart() {
        super.onStart();
        requestAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestAdapter.stopListening();
    }
}