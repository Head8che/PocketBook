package com.example.pocketbook.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.ImageAdder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFragment extends Fragment implements ImageAdder {

    private EditText authorEditText;
    private EditText titleEditText;
    private EditText isbnEditText;
    private EditText commentEditText;
    private Button addButton;
    private FloatingActionButton addImageButton;
    private ImageView imageView;

    private String condition;
    private String photo;
    private String author;
    private String  isbn;
    private String comment;
    private String status;
    private String title;
    private String owner;

    private User currentUser;
    private BookList catalogue;
    private Uri filePath;
    private ProgressDialog progressBar; //TODO : create a progress bar

    private FirebaseStorage storage;
    private StorageReference storageRef;


    public static AddFragment newInstance(User user, BookList catalogue) {
        AddFragment addFragment = new AddFragment();
        Bundle args = new Bundle();
        args.putSerializable("AF_USER", user);
        args.putSerializable("AF_CATALOGUE", catalogue);
        addFragment.setArguments(args);

        // initialize Firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // initialize storage reference
        StorageReference storageRef = storage.getReferenceFromUrl("gs://pocketbook-t09.appspot.com/profile_pictures");

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

        addImageButton = view.findViewById(R.id.button_addImage);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage(getActivity());
                uploadImageToFirebase();

                //find image and set it
            }
        });


        /* Add Book Handler */
        addButton = view.findViewById(R.id.button_addBook);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addBook(); }
        });

    }

    /**
     * Handler for addButton to add a Book
     */
    private void addBook() {
        // create Book object and add to Firestore
        String author = authorEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String isbn = isbnEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        // all new books to be added are available
        final String status = "AVAILABLE";
        // get the user credentials
        final String owner = currentUser.getEmail();

        Book book = new Book(null, title, author, isbn, owner, status,
                comment, condition, null);
        book.pushNewBookToFirebase();

        //TODO : go to the view owned book activity
        ViewMyBookBookFragment viewMyBookBookFragment = new ViewMyBookBookFragment(book);
        Toast.makeText(getActivity(), "You have added a new book.", Toast.LENGTH_SHORT).show();

    }




    @Override
    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        filePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, (Parcelable) filePath);
        startActivityForResult(cameraIntent,0);
    }

    @Override
    public void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean uploadImageToFirebase() {
        if(filePath != null) {
            progressBar.show();
            StorageReference childRef = storageRef.child(isbn+".jpg");
            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else {
            return false;
        }
    }

}
