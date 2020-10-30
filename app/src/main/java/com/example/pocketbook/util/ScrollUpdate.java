package com.example.pocketbook.util;

import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ScrollUpdate {
    private final int  LIMIT = 10;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookList catalogue ;
    private BookAdapter mAdapter;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    public ScrollUpdate(BookList catalogue, Query query,
                        BookAdapter adapter, RecyclerView recycler){
        this.catalogue = catalogue;
        this.mQuery = query;
        this.mAdapter = adapter;
        this.isScrolling = false;
        this.isLastItemReached = false;
        this.mBooksRecycler = recycler;
    }

    public void load(){
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        catalogue.addBook(book);
                    }
                    mAdapter.notifyDataSetChanged();

                    if(task.getResult().size() > LIMIT ) {
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                GridLayoutManager gridLayoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                                assert gridLayoutManager != null;
                                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = gridLayoutManager.getChildCount();
                                int totalItemCount = gridLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;

                                    if ((task.getResult().size() - 1) < (totalItemCount - 1)) {

                                        Query nextQuery = mFirestore.collection("books").startAfter(lastVisible).limit(LIMIT);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        Book book = d.toObject(Book.class);
                                                        catalogue.addBook(book);
                                                    }
                                                    mAdapter.notifyDataSetChanged();
                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                                    if (t.getResult().size() < LIMIT) {
                                                        isLastItemReached = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        };
                        mBooksRecycler.addOnScrollListener(onScrollListener);
                    }
                }
            }
        });

    }

}
