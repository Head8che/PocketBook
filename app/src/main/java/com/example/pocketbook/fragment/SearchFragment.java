package com.example.pocketbook.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.adapter.LinearBookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchFragment extends Fragment implements LinearBookAdapter.OnBookSelectedListener {
    private static final String TAG = "SearchFragment";
    private static final int LIMIT = 20;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private LinearBookAdapter mAdapter;

    private BookList catalogue = new BookList();

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue").limit(LIMIT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

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
                Log.i("onQueryTextChange", newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);


                return true;
            }
        });

//        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        Book book = document.toObject(Book.class);
//                        catalogue.add(book);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
//
////
////                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
////                        @Override
////                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
////                            super.onScrollStateChanged(recyclerView, newState);
////                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
////                                isScrolling = true;
////                            }
////                        }
////
////                        @Override
////                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
////                            super.onScrolled(recyclerView, dx, dy);
////
////                            LinearLayoutManager linearLayout = ((LinearLayoutManager) recyclerView.getLayoutManager());
////                            assert linearLayout != null;
////                            int firstVisibleItemPosition = linearLayout.findFirstVisibleItemPosition();
////                            int visibleItemCount = linearLayout.getChildCount();
////                            int totalItemCount = linearLayout.getItemCount();
////
////                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
////                                isScrolling = false;
////
////                                if ((task.getResult().size() - 1) < (totalItemCount - 1)) {
////
////                                    Query nextQuery = mFirestore.collection("books").startAfter(lastVisible).limit(LIMIT);
////                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                        @Override
////                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
////                                            if (t.isSuccessful()) {
////                                                for (DocumentSnapshot d : t.getResult()) {
////                                                    Book book = d.toObject(Book.class);
////                                                    catalogue.add(book);
////                                                }
////                                                mAdapter.notifyDataSetChanged();
////                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
////
////                                                if (t.getResult().size() < LIMIT) {
////                                                    isLastItemReached = true;
////                                                }
////                                            }
////                                        }
////                                    });
////                                }
////                            }
////                        }
////                    };
////                    mBooksRecycler.addOnScrollListener(onScrollListener);
//                }
//            }
//        });
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
        // Go to ViewMyBookActivity
        ViewBookFragment nextFrag = new ViewBookFragment();
//        Bundle args = new Bundle();
//        args.putString("ID",Book.class snapshot.getId());
//        nextFrag.setArguments(args);
//        activity.getFragmentManager().beginTransaction().replace(R.id.container, nextFrag, "findThisFragment").addToBackStack(null).commit();

    }
}