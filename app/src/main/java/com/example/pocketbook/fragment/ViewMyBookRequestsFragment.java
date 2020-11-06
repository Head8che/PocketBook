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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VMBF_BOOK = "VMBF_BOOK";
    private static final String VMBF_USER = "VMBF_USER";

    // TODO: Rename and change types of parameters
    private RecyclerView requestsRecycler;
    private Book book;
//    private User user;
    private User currentUser;
    private RequestAdapter requestAdapter;


    public ViewMyBookRequestsFragment() {
        // Required empty public constructor
    }

    public static ViewMyBookRequestsFragment newInstance(Book book) {
        ViewMyBookRequestsFragment fragment = new ViewMyBookRequestsFragment();
        Bundle args = new Bundle();
        args.putSerializable("VMBPA_BOOK", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("VMBPA_BOOK");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_my_book_requests, container, false);

        requestsRecycler = view.findViewById(R.id.viewMyBookRequestsRecyclerView);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        requestAdapter = new RequestAdapter(this.book, this.currentUser);
        requestsRecycler.setAdapter(requestAdapter);


        return view;
    }
}