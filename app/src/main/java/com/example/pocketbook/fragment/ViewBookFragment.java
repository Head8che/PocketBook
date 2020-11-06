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
import com.example.pocketbook.model.RequestList;
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewBookFragment extends androidx.fragment.app.Fragment {

    private Book book;
    private User currentUser;
    private User bookOwner;

    private CircleImageView userProfilePicture;
    private ImageView bookCoverImageView;
    private ImageView bookStatusImage;
    private TextView bookTitleField;
    private TextView bookAuthorField;
    private TextView commentField;
    private TextView isbnField;
    private TextView conditionField;
    private Button requestButton;

    /**
     * create a new instance of the ViewBookFragment
     * @param currentUser: the user currently signed in the app
     * @param bookOwner: the owner of the book being viewed
     * @param book: the book being viewed
     * @return
     */

    public static ViewBookFragment newInstance(User currentUser, User bookOwner, Book book) {
        ViewBookFragment viewBookFragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("BA_CURRENTUSER", currentUser);
        args.putSerializable("BA_BOOK", book);
        args.putSerializable("BA_BOOKOWNER", bookOwner);
        viewBookFragment.setArguments(args);
        return viewBookFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the arguments passed to the fragment as a bundle
        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("BA_CURRENTUSER");
            this.book = (Book) getArguments().getSerializable("BA_BOOK");
            this.bookOwner = (User) getArguments().getSerializable("BA_BOOKOWNER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //clear the container before starting the fragment
        if (container != null) {
            container.removeAllViews();
        }
        //inflate fragment in container
        View view = inflater.inflate(R.layout.fragment_view_book, container, true);

        //get the views' ids
        bookTitleField = view.findViewById(R.id.viewBookTitle);
        bookAuthorField = view.findViewById(R.id.viewBookAuthor);
        isbnField = view.findViewById(R.id.viewBookISBN);
        conditionField = view.findViewById(R.id.viewBookCondition);
        commentField = view.findViewById(R.id.viewBookComment);
        requestButton = view.findViewById(R.id.viewBookRequestBtn);
        userProfilePicture = view.findViewById(R.id.viewBookUserProfile);
        bookCoverImageView = view.findViewById(R.id.bookCover);
        bookStatusImage = (ImageView) view.findViewById(R.id.viewBookBookStatusImageView);

        //set the views' values to the values of the book being viewed
        bookTitleField.setText(book.getTitle());
        bookAuthorField.setText(book.getAuthor());
        isbnField.setText(getResources().getString(R.string.isbn_text, book.getISBN()));
        //display a comment if the owner optionally added one to the book
        if (book.getComment() != null) {
            commentField.setText(getResources().getString(R.string.comment_text,
                    book.getComment()));
        } else {
            commentField.setVisibility(View.GONE);
        }
        //display the book's condition if the owner optionally added one to the book
        if (book.getCondition() != null) {
            conditionField.setText(getResources().getString(R.string.condition_text,
                    book.getCondition()));
        } else {
            conditionField.setVisibility(View.GONE);
        }
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(book.getBookCover())
                .into(bookCoverImageView);

        //get the profile picture of the book's owner and set it in its field
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookOwner.getProfilePicture())
                .circleCrop()
                .into(userProfilePicture);

        //boolean to track if a book is available for being requested
        boolean available = false;

        switch(book.getStatus()) {
            //if the book is borrowed or accepted by another user, it is not available for requesting
            case "BORROWED":
            case "ACCEPTED":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),android.graphics.PorterDuff.Mode.SRC_IN);
                available = false;
                break;

            case "REQUESTED":
                //if the book has been requested by this user before, it is not available for requesting again
                if (book.getRequestList().containsRequest(currentUser.getEmail())){
                    requestButton.setText("Already Requested!");
                    requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                    bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                    bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),android.graphics.PorterDuff.Mode.SRC_IN);
                    available = false;
                }
                //if the book has no requests, it is available for requesting
                else if (book.getRequestList().getSize() == 0){
                    book.setStatus("AVAILABLE");
                    available =  true;
                }
                break;

            //default case when the book is available
            default:
                bookStatusImage.setImageResource(R.drawable.ic_available);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable), android.graphics.PorterDuff.Mode.SRC_IN);
                available = true;
        }

        // if the book is available for requesting, the user can tap the request button to request the book
        if (available) {
            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //add a pending request to the book's requestList
                    book.addRequest(new Request(currentUser.getEmail(), bookOwner.getEmail(), book));

                    //display a message confirming that the book has been requested
                    Toast.makeText(getActivity(), String.format(Locale.CANADA,
                            "You have requested %s!",book.getTitle()),
                            Toast.LENGTH_SHORT).show();

                    //go back to the previous fragment
                    getActivity().onBackPressed();
                }
            });
        }

        //return the view
        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
