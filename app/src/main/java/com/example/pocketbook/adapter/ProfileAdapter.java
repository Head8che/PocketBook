package com.example.pocketbook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class ProfileAdapter extends FirestoreRecyclerAdapter<Book, ProfileAdapter.BookHolder> {
    private User currentUser;
    private User bookOwner;
    private FragmentActivity activity;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerOptions<Book> options;
    private LinearLayout rowTitle;
    private RecyclerView rowRecycler;

    public ProfileAdapter(@NonNull FirestoreRecyclerOptions<Book> options, User currentUser,
                          FragmentActivity activity, LinearLayout rowTitle, RecyclerView rowRecycler) {
        super(options);
        this.options = options;
        this.currentUser = currentUser;
        this.activity = activity;
        this.rowTitle = rowTitle;
        this.rowRecycler = rowRecycler;
    }

    static class BookHolder extends RecyclerView.ViewHolder {
//        TextView bookTitle;
//        TextView bookAuthor;
        ImageView bookCoverImageView;
//        ImageView statusImageView;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
//            bookTitle = itemView.findViewById(R.id.itemBookTitle);
//            bookAuthor = itemView.findViewById(R.id.itemBookAuthor);
            bookCoverImageView = itemView.findViewById(R.id.item_book_cover);
//            statusImageView = itemView.findViewById(R.id.itemBookStatus);

        }
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BookHolder(inflater.inflate(R.layout.profile_adapter, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder bookHolder,
                                    int position, @NonNull Book book) {

//        bookHolder.bookTitle.setText(book.getTitle());
//        bookHolder.bookAuthor.setText(book.getAuthor());

//        Log.e("BIND", options.getSnapshots().size() + "");

        GlideApp.with(Objects.requireNonNull(activity.getBaseContext()))
                .load(FirebaseIntegrity.getBookCover(book))
                .into(bookHolder.bookCoverImageView);

        bookHolder.itemView.setOnClickListener(v -> {
            if (book.getOwner().equals(currentUser.getEmail())) {
                Log.e("OWNERIN", book.getOwner());
                ViewMyBookFragment nextFrag = ViewMyBookFragment
                        .newInstance(currentUser, book);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VMBF_USER", currentUser);
                bundle.putSerializable("VMBF_BOOK", book);
                nextFrag.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(activity.findViewById(R.id.container).getId(), nextFrag)
                        .addToBackStack(null).commit();
            } else {
                FirebaseFirestore.getInstance().collection("users")
                        .document(book.getOwner())
                        .get().addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if ((document != null) && (document.exists())) {
                        bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
                        ViewBookFragment nextFrag = ViewBookFragment
                                .newInstance(currentUser, bookOwner, book);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BA_CURRENTUSER", currentUser);
                        bundle.putSerializable("BA_BOOK", book);
                        bundle.putSerializable("BA_BOOKOWNER", bookOwner);
                        nextFrag.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(activity.findViewById(R.id.container).getId(), nextFrag)
                                .addToBackStack(null).commit();
                    }
                });
            }
        });

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        int dataSize = options.getSnapshots().size();
        Log.e("CHANGE_PROFILE_ADAPTER", dataSize + "");
        if (dataSize > 0) {
            rowTitle.setVisibility(View.VISIBLE);
            rowRecycler.setVisibility(View.VISIBLE);
        } else {
            rowTitle.setVisibility(View.GONE);
            rowRecycler.setVisibility(View.GONE);
        }
    }
}