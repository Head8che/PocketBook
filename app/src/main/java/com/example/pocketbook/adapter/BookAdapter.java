package com.example.pocketbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.activity.ViewMyBookActivity;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private BookList list;
    private User currentUser;
    private Activity activity;

    public BookAdapter(User currentUser, BookList list, Activity activity) {
        this.list = list;
        this.currentUser = currentUser;
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
        Book book = list.getBookAtPosition(position);
        holder.bind(book);

        /* DETERMINE WHICH PAGE SHOULD BE SELECTED, BASED ON IF USER IS OWNER OF BOOK */

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.getOwner().equals("jane@gmail.com")) {
                    Log.e("OWNERIN", book.getOwner());
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, ViewMyBookActivity.class);
                    intent.putExtra("BAI_BOOK", (Serializable) book);
                    intent.putExtra("BAI_USER", (Serializable) currentUser);
                    intent.putExtra("BAI_CATALOGUE", (Serializable) list);
                    context.startActivity(intent);
                } else {
                    ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser, book);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("BA_USER", currentUser);
                    bundle.putSerializable("BA_BOOK", book);
                    nextFrag.setArguments(bundle);
                    activity.getFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
                    //activity.getFragmentManager().beginTransaction().replace(R.id., nextFrag, "ViewBookFragment").addToBackStack(null).commit();
                }
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.getSize();
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


