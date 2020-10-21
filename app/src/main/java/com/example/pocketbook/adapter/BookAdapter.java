// responsible for the actual display of books in the UI

package com.example.pocketbook.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class BookAdapter extends FirestoreAdapter<BookAdapter.ViewHolder>{

    private static final String TAG = "Book Adapter";

    public BookAdapter(Query query){
        super(query);
    }

    // Creates new views (invoked by the layout manager)
    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_book, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Log.d(TAG, "docSnapshot: ", (Throwable) getSnapshot(position).getData());
        holder.bind(getSnapshot(position));
    }


    // Provide a reference to the views for each book item
    // this class represents 1 book
    // you provide access to all the views for a book item here

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView bookTitle;
        TextView bookAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.textView);
            bookAuthor = itemView.findViewById(R.id.textView2);
        }

        public void bind(final DocumentSnapshot snapshot){
            Book book = snapshot.toObject(Book.class);

            bookTitle.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            // click listener
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

