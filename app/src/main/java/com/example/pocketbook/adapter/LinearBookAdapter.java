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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.fragment.ViewProfileFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LinearBookAdapter extends FirestoreRecyclerAdapter<Book, LinearBookAdapter.LinearBookHolder> {
    private User currentUser;
    private User bookOwner;
    private FragmentActivity activity;
    private FirebaseAuth mAuth;

    public LinearBookAdapter(@NonNull FirestoreRecyclerOptions<Book> options, User currentUser,
                       FragmentActivity activity) {
        super(options);
        this.currentUser = currentUser;
        this.activity = activity;
    }

    static class LinearBookHolder extends RecyclerView.ViewHolder{
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookIsbn;
        TextView bookOwner;
        ImageView bookStatusImageView;
        ImageView bookCoverImageView;

        public LinearBookHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.itemBookLinearTitle);
            bookAuthor = itemView.findViewById(R.id.itemBookLinearAuthor);
            bookIsbn = itemView.findViewById(R.id.itemBookLinearIsbn);
            bookOwner = itemView.findViewById(R.id.itemBookLinearOwner);
            bookStatusImageView = itemView.findViewById(R.id.itemBookLinearStatus);
            bookCoverImageView = itemView.findViewById(R.id.itemBookLinearCardCover);
        }
    }

    @NonNull
    @Override
    public LinearBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LinearBookHolder(inflater.inflate(R.layout.item_book_linear, parent,
                false));
    }

    @Override
    protected void onBindViewHolder(@NonNull LinearBookHolder bookHolder,
                                    int position, @NonNull Book book) {

        bookHolder.bookTitle.setText(book.getTitle());
        bookHolder.bookAuthor.setText(book.getAuthor());
        bookHolder.bookIsbn.setText(activity.getResources()
                .getString(R.string.isbn_text, book.getISBN()));
        bookHolder.bookOwner.setText(activity.getResources()
                .getString(R.string.added_by_text, book.getOwner()));

        switch (book.getStatus().toUpperCase()) {

            // if the book is borrowed or accepted, it is not available for requesting
            case "BORROWED":
                bookHolder.bookStatusImageView.setImageResource(R.drawable.ic_borrowed);
                bookHolder.bookStatusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "ACCEPTED":
                bookHolder.bookStatusImageView.setImageResource(R.drawable.ic_accepted);
                bookHolder.bookStatusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "REQUESTED":
                // if the book has any requesters and is requested by the current user
                if (((book.getRequesters().size() > 0)
                        && (book.getRequesters().contains(currentUser.getEmail())))
                        || (book.getOwner().equals(currentUser.getEmail()))) {

                    // if the user has already requested the book, it is not available
                    bookHolder.bookStatusImageView.setImageResource(R.drawable.ic_requested);
                    bookHolder.bookStatusImageView.setColorFilter(ContextCompat
                                    .getColor(activity.getBaseContext(), R.color.colorRequested),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                break;

            // default case is that the book is available
            default:
                bookHolder.bookStatusImageView.setImageResource(R.drawable.ic_available);
                bookHolder.bookStatusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

        GlideApp.with(Objects.requireNonNull(bookHolder.itemView.getContext()))
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
}
