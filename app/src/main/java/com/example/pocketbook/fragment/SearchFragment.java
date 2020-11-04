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
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchFragment extends Fragment implements LinearBookAdapter.OnBookSelectedListener {
    private static final String TAG = "SearchFragment";
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

    public static SearchFragment newInstance(User user, BookList catalogue) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable("SF_USER", user);
        args.putSerializable("SF_CATALOGUE", catalogue);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("SF_USER");
            this.catalogue = (BookList) getArguments().getSerializable("SF_CATALOGUE");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        // TODO: see if possible to switch to BookList and BookAdapter
        mBooksRecycler = v.findViewById(R.id.search_recycler_books);
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));

        mAdapter = new LinearBookAdapter(mQuery, this);
        mBooksRecycler.setAdapter(mAdapter);


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = v.findViewById(R.id.searchView);

        // Assumes current activity is the searchable activity
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();

                // TODO: add batch loading
                // TODO: update query not to include books that currentUser owns
                mQuery = mFirestore.collection("catalogue").whereArrayContains("keywords", newText);
                mAdapter.setQuery(mQuery);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                // TODO: implement this
                // TODO: no reloading but conclusion
                return true;
            }
        });

        return v;
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
        Book book = FirebaseIntegrity.getBookFromFirestore(snapshot);;
//        ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser, book);
//        Bundle args = new Bundle();
//        args.putString("ID",Book.class snapshot.getId());
//        nextFrag.setArguments(args);
//        activity.getFragmentManager().beginTransaction().replace(R.id.container, nextFrag, "findThisFragment").addToBackStack(null).commit();

    }
}