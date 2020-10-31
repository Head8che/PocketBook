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
import com.example.pocketbook.model.Request;
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
import java.util.Objects;


public class ViewBookFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Book book= (Book) bundle.getSerializable("book");
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
        bookCoverImageView = view.findViewById(R.id.bookCover);
        bookTitleField = view.findViewById(R.id.viewBookTitle);
        bookAuthorField = view.findViewById(R.id.viewBookAuthor);
        commentField = view.findViewById(R.id.commentField);
        isbnField = view.findViewById(R.id.isbnField);
        conditionField = view.findViewById(R.id.conditionField);
        requestButton = view.findViewById(R.id.viewBookRequestBtn);
        bookStatusImage = (ImageView) view.findViewById(R.id.viewBookBookStatusImageView);
        bookTitleField.setText(bookTitle);
        bookAuthorField.setText(bookAuthor);
        commentField.setText(bookComment);
        isbnField.setText(bookIsbn);
        conditionField.setText(bookCondition);
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookCover)
                .into(bookCoverImageView);
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();

        boolean available = true;
        //boolean alreadyRequested = false;
        switch(bookStatus) {
            case "borrowed":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),android.graphics.PorterDuff.Mode.SRC_IN);
                available = false;

            case "accepted":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(getResources().getColor(R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccepted),android.graphics.PorterDuff.Mode.SRC_IN);
                available = false;
            default:
                bookStatusImage.setImageResource(R.drawable.ic_available);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable), android.graphics.PorterDuff.Mode.SRC_IN);
                available = true;
        }

        if (available) {
            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    Date date = new Date();
                    //Request request = new Request(email, bookOwner,bookId, dateFormat.format(date) );
                    HashMap<String, String> data = new HashMap<>();
                    data.put("requester", email);
                    data.put("requestee", bookOwner);
                    data.put("requestedBook", bookId);
                    data.put("date", dateFormat.format(date));
                    db.collection("requests")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("test", "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("test", "Error adding document", e);
                                }
                            });
                    db.collection("catalogue").document(bookId)
                            .update("status", "requested")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("test", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("test", "Error updating document", e);
                                }
                            });

                    Toast.makeText(getActivity(), "You have requested "+ bookTitle +"!",
                            Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                //TODO: set status to requested in book object

            });
        }
        return super.onCreateView(inflater, container, savedInstanceState);
 //       return view;
    }


    
}




//    DocumentReference docRef = db.collection("books").document(bookId); // ps: change this to catalogue later
//// https://firebase.google.com/docs/firestore/query-data/get-data#java
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//@Override
//public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//        if (task.isSuccessful()) {
//        DocumentSnapshot document = task.getResult();
//        if (document.exists()) {
//        Log.d("sample", "DocumentSnapshot data: " + document.getData());
//        } else {
//        Log.d("sample", "No such document");
//        }
//        } else {
//        Log.d("sample", "get failed with ", task.getException());
//        }
//        }
//        });
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//@Override
//public void onSuccess(DocumentSnapshot documentSnapshot) {
//        Book book = documentSnapshot.toObject(Book.class);
//
//        bookTitle.setText(book.getTitle());
//        bookAuthor.setText(book.getAuthor());
//        commentField.setText(book.getComment());
//        isbnField.setText(book.getISBN());
//        conditionField.setText(book.getCondition());
//        }
//        });

//        else if (bookStatus.equals("requested")){
//            // check if the user has requested this book already
//            db.collection("requests")
//                    .whereEqualTo("requestedBook", bookId)
//                    .whereEqualTo("requester", email)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                boolean alreadyRequested = true;
//                                requestButton.setClickable(false);
//                                requestButton.setText("Already Requested!");
//                            } else {
//                                boolean alreadyRequested = false;
//                            }
//                        }
//                    });
//
//        }