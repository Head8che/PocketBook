package com.example.pocketbook.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class LinearBookAdapter extends FirestoreAdapter<LinearBookAdapter.ViewHolder>{

    public interface OnBookSelectedListener {
        void onBookSelected(DocumentSnapshot snapshot);
    }

    private OnBookSelectedListener mListener;

    public LinearBookAdapter(Query query, OnBookSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_book_linear, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView bookTitle;
        TextView description;
        TextView username;
        TextView status;
        ImageView bookCoverImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.item_book_title);
            description = itemView.findViewById(R.id.item_book_description);;
            username = itemView.findViewById(R.id.item_book_owner);;
            status = itemView.findViewById(R.id.item_book_status);;
            bookCoverImageView = itemView.findViewById(R.id.item_book_cover);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnBookSelectedListener listener){
            Book book = snapshot.toObject(Book.class);

//            Log.d("LinearBookAdapter", book.getBookTitle());
            bookTitle.setText(book.getTitle());
            description.setText(book.getComment());
            username.setText(book.getOwner());
            status.setText(book.getStatus());

            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
                    .load(book.getBookCover())
                    .into(bookCoverImageView);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onBookSelected(snapshot);
                    }
                }
            });
        }
    }
}
