package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFragment extends Fragment {

    private EditText authorText;
    private EditText titleText;
    private EditText isbnText;
    private EditText commentText;
    private Button addButton;
    private ImageButton imageButton;

    public FirebaseUser owner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        owner = FirebaseAuth.getInstance().getCurrentUser();
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get all the EditText fields
        authorText = (EditText) view.findViewById(R.id.editText_author);
        titleText = (EditText) view.findViewById(R.id.editText_title);
        isbnText = (EditText) view.findViewById(R.id.editText_isbn);
        commentText = (EditText) view.findViewById(R.id.editText_comment);

        /**
         * Add Book Handler
         */
        // get the button and create the listener
        addButton = view.findViewById(R.id.button_addBook);
        addButton.setOnClickListener(new View.OnClickListener() {

            final String author = authorText.getText().toString();
            final String title = titleText.getText().toString();
            final String isbn = isbnText.getText().toString();
            final String comment = commentText.getText().toString();
            // all new books to be added are available
            final String status = "available";

            // get the user credentials
            final FirebaseUser owner = FirebaseAuth.getInstance().getCurrentUser();
            final String ownerId = owner.getEmail(); //TODO : change to email

            @Override
            public void onClick(View view) {
                // create Book object and add to Firestore
                Book book = new Book(title, author, isbn, ownerId, status);
                book.setBook(title, author, isbn, ownerId, status,
                        null, null, null);

                //go to the view owned book activity
                ViewMyBookBookFragment viewMyBookBookFragment = new ViewMyBookBookFragment(book);

            }

        });

        /**
         * Add Image Handler
         *
         * Resource :
         * https://stackoverflow.com/questions/9107900/how-to-upload-image-from-gallery-in-android
         * https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
         */
        imageButton = view.findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///////////// TODO:
                // create a dialogue fragment that allows
                //  - take photo
                //  - choose from gallery
                //  - cancel
                // retrieve the image
                // add to Firestore
                // set image as image uploaded as imageView
            }
        });

    }
}
