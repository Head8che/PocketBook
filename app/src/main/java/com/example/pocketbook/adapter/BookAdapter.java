package com.example.pocketbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.ViewMyBookActivity;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;

import java.io.Serializable;
import java.util.Objects;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private BookList list;
    private Activity activity;

    public BookAdapter(BookList list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = list.get(position);
        holder.bind(book);

        /* DETERMINE WHICH PAGE SHOULD BE SELECTED, BASED ON IF USER IS OWNER OF BOOK */

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBookFragment nextFrag = new ViewBookFragment();
                Bundle args = new Bundle();
                args.putString("ID",book.getId());
                args.putString("Title",book.getTitle());
                args.putString("Author",book.getAuthor());
                args.putString("ISBN",book.getISBN());
                args.putString("Owner",book.getOwner());
                args.putString("Status",book.getStatus());
                args.putString("Comment",book.getComment());
                args.putString("Condition",book.getCondition());
                args.putString("Photo",book.getPhoto());
                nextFrag.setArguments(args);
                activity.getFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag, "findThisFragment").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        //        TextView bookTitle;
//        TextView bookAuthor;
        ImageView bookCoverImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            bookTitle = itemView.findViewById(R.id.view_title);
//            bookAuthor = itemView.findViewById(R.id.view_author);
            bookCoverImageView = itemView.findViewById(R.id.itemBookBookCoverImageView);
        }

        public void bind(final Book book){

//            bookTitle.setText(book.getTitle());
//            bookAuthor.setText(book.getAuthor());

            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
                    .load(book.getBookCover())
                    .into(bookCoverImageView);
        }
    }
}


