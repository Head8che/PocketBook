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
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMyBookRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMyBookRequestsFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VMBF_BOOK = "VMBF_BOOK";


    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;


    public ViewMyBookRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of ViewMyBookRequestsFragment
     * @param book: the book being viewed
     * @return
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_my_book_requests, container, false);

        //get the id of the recycler for this layout and set its layout manager
        requestsRecycler = view.findViewById(R.id.viewMyBookRequestsRecyclerView);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //set a new requestAdapter as an adapter for the recycler
        requestAdapter = new RequestAdapter(this.book);
        requestsRecycler.setAdapter(requestAdapter);

        return view;
    }
}