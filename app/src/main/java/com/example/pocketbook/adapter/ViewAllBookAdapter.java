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

public class ViewAllBookAdapter extends
        FirestoreRecyclerAdapter<Book, ViewAllBookAdapter.BookHolder> {

    private User currentUser;
    private User bookOwner;
    private FragmentActivity activity;
    private FirebaseAuth mAuth;

    public ViewAllBookAdapter(@NonNull FirestoreRecyclerOptions<Book> options, User currentUser,
                       FragmentActivity activity) {
        super(options);
        this.currentUser = currentUser;
        this.activity = activity;
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView bookAuthor;
        ImageView bookCoverImageView;
        ImageView statusImageView;
        TextView otherUser;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.itemBookV2Title);
            bookAuthor = itemView.findViewById(R.id.itemBookV2Author);
            bookCoverImageView = itemView.findViewById(R.id.itemBookV2BookCoverImageView);
            statusImageView = itemView.findViewById(R.id.itemBookV2Status);
            otherUser = itemView.findViewById(R.id.itemBookV2User);

        }
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BookHolder(inflater.inflate(R.layout.item_book_v2, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder bookHolder,
                                    int position, @NonNull Book book) {

        bookHolder.bookTitle.setText(book.getTitle());
        bookHolder.bookAuthor.setText(book.getAuthor());

        GlideApp.with(Objects.requireNonNull(activity.getBaseContext()))
                .load(FirebaseIntegrity.getBookCover(book))
                .into(bookHolder.bookCoverImageView);

        switch (book.getStatus().toUpperCase()) {

            // if the book is borrowed or accepted, it is not available for requesting
            case "BORROWED":

                // OWNER TAB
                if (currentUser.getEmail().equals(book.getOwner())) {
                    // TODO: Set the Other User text to the book borrower
                    bookHolder.otherUser.setVisibility(View.GONE);

                } else  { // BORROWER TAB

                        // Set the Other User text to the book owner
                        bookHolder.otherUser.setText(activity.getResources()
                                .getString(R.string.owned_book_text, book.getOwner()));

                }

                bookHolder.statusImageView.setImageResource(R.drawable.ic_borrowed);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);

                break;

            case "ACCEPTED":

                // OWNER TAB
                if (currentUser.getEmail().equals(book.getOwner())) {
                    // TODO: Set the Other User text to the book requester
                    bookHolder.otherUser.setVisibility(View.GONE);

                } else  { // BORROWER TAB

                    // Set the Other User text to the book owner
                    bookHolder.otherUser.setText(activity.getResources()
                            .getString(R.string.owned_book_text, book.getOwner()));

                }

                bookHolder.statusImageView.setImageResource(R.drawable.ic_accepted);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "REQUESTED":

                // OWNER TAB
                if (currentUser.getEmail().equals(book.getOwner())) {
                    // TODO: Set the Other User text to the book requester
                    bookHolder.otherUser.setVisibility(View.GONE);

                } else  { // BORROWER TAB

                    // Set the Other User text to the book owner
                    bookHolder.otherUser.setText(activity.getResources()
                            .getString(R.string.owned_book_text, book.getOwner()));

                }

                // if the user has already requested the book, it is not available
                bookHolder.statusImageView.setImageResource(R.drawable.ic_requested);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorRequested),
                        android.graphics.PorterDuff.Mode.SRC_IN);



                break;

            // default case is that the book is available
            default:
                bookHolder.statusImageView.setImageResource(R.drawable.ic_available);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

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
