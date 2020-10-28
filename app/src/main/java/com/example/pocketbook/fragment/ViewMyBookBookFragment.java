package com.example.pocketbook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.ViewMyBookActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class ViewMyBookBookFragment extends Fragment {

    private Book book;
    private BookList catalogue;

    public ViewMyBookBookFragment() {
        // Required empty public constructor
    }

    public ViewMyBookBookFragment(Book book, BookList catalogue) {
        this.book = book;
        this.catalogue = catalogue;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_my_book_book, container, false);

        /* UNCOMMENT AFTER THE OTHER STATUS ICONS ARE SET UP*/
        String bookTitle = book.getTitle();
        String bookAuthor = book.getAuthor();
        String bookISBN = book.getISBN();
        StorageReference bookCover = book.getBookCover();
        String bookStatus = book.getStatus();
        String bookCondition = book.getCondition();
        String bookComment = book.getComment();

        TextView layoutBookTitle = (TextView) rootView.findViewById(R.id.viewMyBookBookTitleTextView);
        TextView layoutBookAuthor = (TextView) rootView.findViewById(R.id.viewMyBookAuthorTextView);
        TextView layoutBookISBN = (TextView) rootView.findViewById(R.id.viewMyBookISBNTextView);
        ImageView layoutBookCover = (ImageView) rootView.findViewById(R.id.viewMyBookBookCoverImageView);
        ImageView layoutBookStatus = (ImageView) rootView.findViewById(R.id.viewMyBookStatusImageView);
        TextView layoutBookCondition = (TextView) rootView.findViewById(R.id.viewMyBookConditionTextView);
        TextView layoutBookComment = (TextView) rootView.findViewById(R.id.viewMyBookCommentTextView);

        layoutBookTitle.setText(bookTitle);
        layoutBookAuthor.setText(bookAuthor);
        layoutBookISBN.setText(getResources().getString(R.string.isbn_text, bookISBN));

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookCover)
                .into(layoutBookCover);

        switch(bookStatus) {
            case "accepted":
                layoutBookStatus.setImageResource(R.drawable.ic_accepted);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "borrowed":
                layoutBookStatus.setImageResource(R.drawable.ic_borrowed);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                layoutBookStatus.setImageResource(R.drawable.ic_available);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (bookCondition != null) {
            layoutBookCondition.setText(getResources().getString(R.string.condition_text,
                    bookCondition));
        } else {
            layoutBookCondition.setVisibility(View.GONE);
        }

        if (bookComment != null) {
            layoutBookComment.setText(getResources().getString(R.string.comment_text,
                    bookComment));
        } else {
            layoutBookComment.setVisibility(View.GONE);
        }

        /* TODO: handle Edit button and edit book info in catalogue */

        return rootView;
    }
}