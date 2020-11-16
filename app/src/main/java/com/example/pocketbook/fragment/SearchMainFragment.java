package com.example.pocketbook.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.adapter.RequestAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchMainFragment extends Fragment implements LinearBookAdapter.OnBookSelectedListener {

    private static final String TAG = "SearchMainFragment";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private User currentUser;
    private BookList catalogue;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private SearchView searchView;


    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;
    private int pos;


//    public SearchMainFragment() {
//        // Required empty public constructor
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the book argument passed to the newInstance() method
        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("VMBPA_BOOK");
            this.pos = getArguments().getInt("pos");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_main, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        // TODO: see if possibility to switch
        mBooksRecycler = v.findViewById(R.id.search_recycler_books);
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));

//        Bundle args = getArguments();
//        ((TextView) view.findViewById(android.R.id.text1))
//                .setText(Integer.toString(args.getInt(ARG_OBJECT)));

        mAdapter = new LinearBookAdapter(mQuery, this);
        mBooksRecycler.setAdapter(mAdapter);
    }

    public void updateQuery(String newText){
        // TODO: add batch loading
        // TODO: descriptive message when no books found

        newText = newText.toLowerCase();

        Bundle args = getArguments();
        int t = args.getInt("type");
        Log.w("onQueryTextSubmit", String.valueOf(t));
        if(t == 0) // searching all books
            mQuery = mFirestore.collection("catalogue").whereArrayContains("keywords", newText);
        else // searching in owned books only
            mQuery = mFirestore.collection("catalogue").whereEqualTo("owner", "currentUser") // change this
                                                                    .whereArrayContains("keywords", newText);
        mAdapter.setQuery(mQuery);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onBookSelected(DocumentSnapshot snapshot) {
        Book book = FirebaseIntegrity.getBookFromFirestore(snapshot);
//        ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser, book);
//        Bundle args = new Bundle();
//        args.putString("ID",Book.class snapshot.getId());
//        nextFrag.setArguments(args);
//        activity.getFragmentManager().beginTransaction().replace(R.id.container, nextFrag, "findThisFragment").addToBackStack(null).commit();

    }
}



