package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;

public class AddFragment extends Fragment {

    private EditText authorEditText;
    private EditText titleEditText;
    private EditText isbnEditText;
    private EditText commentEditText;
    private Button addButton;
    private ImageView imageView;

    private String condition;
//    private String photo;
//    private String author;
//    private String  isbn;
//    private String comment;
//    private String status;
//    private String title;
//    private String owner;

    private User currentUser;
    private BookList catalogue;

    public static AddFragment newInstance(User user, BookList catalogue) {
        AddFragment addFragment = new AddFragment();
        Bundle args = new Bundle();
        args.putSerializable("AF_USER", user);
        args.putSerializable("AF_CATALOGUE", catalogue);
        addFragment.setArguments(args);
        return addFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("AF_USER");
            this.catalogue = (BookList) getArguments().getSerializable("AF_CATALOGUE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get all the EditText fields
        authorEditText = (EditText) view.findViewById(R.id.editText_author);
        titleEditText = (EditText) view.findViewById(R.id.editText_title);
        isbnEditText = (EditText) view.findViewById(R.id.editText_isbn);
        commentEditText = (EditText) view.findViewById(R.id.editText_comment);

        /* Drop-down menu for Conditions */
        Spinner conditionsSpinner = (Spinner) view.findViewById(R.id.spinner_conditions);
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Resource : https://stackoverflow.com/questions/12275678/how-to-set-arrayadapter-for-spinner-in-fragment
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.bookConditions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        conditionsSpinner.setAdapter(spinnerAdapter);

        conditionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                condition = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * TODO : Add Image Handler
         *
         * Resource :
         * https://stackoverflow.com/questions/9107900/how-to-upload-image-from-gallery-in-android
         * https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
         */
        imageView = view.findViewById(R.id.image_view);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ///////////// TODO:
//                // create a dialogue fragment that allows
//                //  - take photo
//                //  - choose from gallery
//                //  - cancel
//                // retrieve the image
//                // add to Firestore
//                // set image as image uploaded as imageView
//
//                pri
//            }
//        });

//        author = authorText.getText().toString();
//        title = titleText.getText().toString();
//        isbn = isbnText.getText().toString();
//        comment = commentText.getText().toString();
//        // all new books to be added are available
//        status = "AVAILABLE";
//
//        // get the user credentials
//        owner = currentUser.getEmail();

        /* Add Book Handler */
        addButton = view.findViewById(R.id.button_addBook);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // create Book object and add to Firestore
                final String author = authorEditText.getText().toString();
                final String title = titleEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String comment = commentEditText.getText().toString();
                // all new books to be added are available
                final String status = "AVAILABLE";

                // get the user credentials
                final String owner = currentUser.getEmail();

                Book book = new Book(null, title, author, isbn, owner, status,
                        comment, condition, null);
                book.pushNewBookToFirebase();


                //TODO : go to the view owned book activity
                ViewMyBookBookFragment viewMyBookBookFragment = new ViewMyBookBookFragment(book);
                String finishAddMsg = "You have added a book.";

            }

        });

    }

}
