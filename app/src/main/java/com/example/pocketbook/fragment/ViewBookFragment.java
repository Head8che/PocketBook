package com.example.pocketbook.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class ViewBookFragment extends androidx.fragment.app.Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Book book;
    private User currentUser;

    private ImageView userProfile;
    private ImageView bookCoverImageView;
    private ImageView bookStatusImage;
    private TextView bookTitleField;
    private TextView bookAuthorField;
    private TextView commentField;
    private TextView isbnField;
    private TextView conditionField;
    private Button requestButton;

    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookComment;
    private String bookIsbn;
    private String bookCondition;
    private String bookOwner;
    private String bookStatus;
    private StorageReference bookCover;

    public static ViewBookFragment newInstance(User user, Book book) {
        ViewBookFragment viewBookFragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("BA_USER", user);
        args.putSerializable("BA_BOOK", book);
        viewBookFragment.setArguments(args);
        return viewBookFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("BA_USER");
            this.book = (Book) getArguments().getSerializable("BA_BOOK");
        }

        bookId = book.getId();
        bookTitle = book.getTitle();
        bookAuthor = book.getAuthor();
        bookComment = book.getComment();
        bookIsbn = book.getISBN();
        bookCondition = book.getCondition();
        bookOwner = book.getOwner();
        bookStatus =  book.getStatus();
        bookCover = book.getBookCover();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_view_book, container, true);

        String bookTitle = book.getTitle();
        String bookAuthor = book.getAuthor();
        String bookISBN = book.getISBN();
        StorageReference bookCover = book.getBookCover();
        String bookStatus = book.getStatus();
        String bookCondition = book.getCondition();
        String bookComment = book.getComment();

        TextView bookTitleField = view.findViewById(R.id.viewBookTitle);
        TextView bookAuthorField = view.findViewById(R.id.viewBookAuthor);
        TextView isbnField = view.findViewById(R.id.isbnField);
        TextView conditionField = view.findViewById(R.id.conditionField);
        TextView commentField = view.findViewById(R.id.commentField);

        Button requestButton = view.findViewById(R.id.viewBookRequestBtn);
        ImageView userProfilePicture = view.findViewById(R.id.viewBookUserProfile);
        ImageView bookCoverImageView = view.findViewById(R.id.bookCover);
        ImageView bookStatusImage = (ImageView) view.findViewById(R.id.viewBookBookStatusImageView);

        bookTitleField.setText(bookTitle);
        bookAuthorField.setText(bookAuthor);
        isbnField.setText(bookISBN);

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookCover)
                .into(bookCoverImageView);

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(currentUser.getProfilePicture())
                .circleCrop()
                .into(userProfilePicture);

        boolean available;
        //boolean alreadyRequested = false;
        switch(bookStatus) {
            case "borrowed":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),android.graphics.PorterDuff.Mode.SRC_IN);
                available = false;
                break;
            case "accepted":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccepted),android.graphics.PorterDuff.Mode.SRC_IN);
                available = false;
                break;
            default:
                bookStatusImage.setImageResource(R.drawable.ic_available);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable), android.graphics.PorterDuff.Mode.SRC_IN);
                available = true;
        }

        if (bookComment != null) {
            commentField.setText(getResources().getString(R.string.comment_text,
                    bookComment));
        } else {
            commentField.setVisibility(View.GONE);
        }

        if (bookCondition != null) {
            conditionField.setText(getResources().getString(R.string.condition_text,
                    bookCondition));
        } else {
            conditionField.setVisibility(View.GONE);
        }

        if (available) {
            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    book.addRequest(new Request(currentUser.getEmail(), book.getOwner(), book));

                    Toast.makeText(getActivity(), String.format(Locale.CANADA,
                            "You have requested %s!",book.getTitle()),
                            Toast.LENGTH_SHORT).show();

                    getActivity().onBackPressed();
                }
                //TODO: set status to requested in book object

            });
        }
        // return view
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    
}
