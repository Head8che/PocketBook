package com.example.pocketbook.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ViewBookFragment extends Fragment {
    private ImageView bookCover;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView commentField;
    private TextView isbnField;
    private TextView conditionField;
    private ImageView userProfile;
    private Button requestButton;
    private String bookId;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookId = getArguments().getString("ID");
        db = FirebaseFirestore.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_view_book, container, true);
        bookCover = view.findViewById(R.id.bookCover);
        bookTitle = view.findViewById(R.id.viewBookTitle);
        bookAuthor = view.findViewById(R.id.viewBookAuthor);
        commentField = view.findViewById(R.id.commentField);
        isbnField = view.findViewById(R.id.isbnField);
        conditionField = view.findViewById(R.id.conditionField);

        DocumentReference docRef = db.collection("books").document(bookId); // ps: change this to catalogue later
        // https://firebase.google.com/docs/firestore/query-data/get-data#java
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("sample", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("sample", "No such document");
                    }
                } else {
                    Log.d("sample", "get failed with ", task.getException());
                }
            }
        });
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Book book = documentSnapshot.toObject(Book.class);

                bookTitle.setText(book.getTitle());
                bookAuthor.setText(book.getAuthor());
                commentField.setText(book.getComment());
                isbnField.setText(book.getISBN());
                conditionField.setText(book.getCondition());
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    
}
