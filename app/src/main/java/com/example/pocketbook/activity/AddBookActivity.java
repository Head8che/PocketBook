package com.example.pocketbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.example.pocketbook.util.PhotoHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Allows the user to add a new book to PocketBook
 */
public class AddBookActivity extends AppCompatActivity {

//    Book book;
    User currentUser;

    private Boolean validTitle;
    private Boolean validAuthor;
    private Boolean validISBN;

    private StorageReference defaultBookCover = FirebaseStorage.getInstance().getReference()
            .child("default_images").child("no_book_cover_light.png");

    private TextInputEditText layoutBookTitle;
    private TextInputEditText layoutBookAuthor;
    private TextInputEditText layoutBookISBN;
    private ImageView layoutBookCover;
    private TextInputEditText layoutBookCondition;
    private TextInputEditText layoutBookComment;

    private TextInputLayout layoutBookTitleContainer;
    private TextInputLayout layoutBookAuthorContainer;
    private TextInputLayout layoutBookISBNContainer;
    private TextInputLayout layoutBookConditionContainer;
    private TextInputLayout layoutBookCommentContainer;

    private PhotoHandler photoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        photoHandler = new PhotoHandler();

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("HA_USER");

        // return if the intent passes in a null user
        if (currentUser == null) {
            return;
        }

        // initialize validation booleans to false
        validTitle = false;
        validAuthor = false;
        validISBN = false;

        // Toolbar toolbar = (Toolbar) findViewById(R.id.addBookToolbar);
        ImageView cancelButton = findViewById(R.id.addBookCancelBtn);
        TextView saveButton = findViewById(R.id.addBookSaveBtn);
        TextView changePhotoButton = findViewById(R.id.addBookChangePhotoBtn);

        // access the layout text fields
        layoutBookTitle = findViewById(R.id.addBookTitleField);
        layoutBookAuthor = findViewById(R.id.addBookAuthorField);
        layoutBookISBN = findViewById(R.id.addBookISBNField);
        layoutBookCover = findViewById(R.id.addBookBookCoverField);
        layoutBookCondition = findViewById(R.id.addBookConditionField);
        layoutBookComment = findViewById(R.id.addBookCommentField);

        // set the initial book condition
        layoutBookCondition.setText(R.string.fairCondition);

        // access the layout text containers
        layoutBookTitleContainer = findViewById(R.id.addBookTitleContainer);
        layoutBookAuthorContainer = findViewById(R.id.addBookAuthorContainer);
        layoutBookISBNContainer = findViewById(R.id.addBookISBNContainer);
        layoutBookConditionContainer = findViewById(R.id.addBookConditionContainer);
        layoutBookCommentContainer = findViewById(R.id.addBookCommentContainer);

        // add a text field listener that validates the inputted text
        layoutBookTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidBookTitle(s.toString()))) {
                    layoutBookTitle.setError("Input required");
                    layoutBookTitleContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validTitle = true;
                    layoutBookTitle.setError(null);
                    layoutBookTitleContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // add a text field listener that validates the inputted text
        layoutBookAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidBookAuthor(s.toString()))) {
                    layoutBookAuthor.setError("Input required");
                    layoutBookAuthorContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validAuthor = true;
                    layoutBookAuthor.setError(null);
                    layoutBookAuthorContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // add a text field listener that validates the inputted text
        layoutBookISBN.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidBookIsbn(s.toString()))) {
                    // if the inputted text is empty
                    if (s.toString().equals("")) {
                        layoutBookISBN.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutBookISBN.setError("Invalid ISBN");
                    }
                    layoutBookISBNContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validISBN = true;
                    layoutBookISBN.setError(null);
                    layoutBookISBNContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // showSpinnerDialog when layoutBookCondition is clicked
        layoutBookCondition.setOnClickListener(v -> {
            layoutBookCondition.setClickable(false);
            showSpinnerDialog();
            layoutBookCondition.setClickable(true);
        });

        // showImageSelectorDialog when changePhotoButton is clicked
        changePhotoButton.setOnClickListener(v -> {
            changePhotoButton.setClickable(false);
            (photoHandler).showImageSelectorDialog(this, defaultBookCover, layoutBookCover);
            changePhotoButton.setClickable(true);
        });

        // load default book cover into ImageLayout
        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(defaultBookCover)
                .into(layoutBookCover);

        // go back when cancelButton is clicked
        cancelButton.setOnClickListener(v -> {
            cancelButton.setClickable(false);
            onBackPressed();
            cancelButton.setClickable(true);
        });

        // when saveButton is clicked
        saveButton.setOnClickListener(v -> {
            saveButton.setClickable(false);
            // if all fields are valid
            if (validTitle && validAuthor && validISBN) {
                if (!noChanges()) {  // if the user has entered some text or chosen a photo

                    // extract the layout text field values into variables
                    String title = Objects.requireNonNull(layoutBookTitle.getText())
                            .toString();
                    String author = Objects.requireNonNull(layoutBookAuthor.getText())
                            .toString();
                    String isbn = Objects.requireNonNull(layoutBookISBN.getText())
                            .toString();
                    String condition = Objects.requireNonNull(layoutBookCondition.getText())
                            .toString();
                    String comment = Objects.requireNonNull(layoutBookComment.getText())
                            .toString();

                    // generate a valid id for the new book
                    String bookId = Parser.generateValidId();

                    // if all booleans are good, pushNewBook
                    Book book = Parser.parseBook(bookId, title, author, isbn,
                            currentUser.getEmail(), "AVAILABLE", true, comment,
                            condition, "", new ArrayList<>());

                    if (photoHandler.getCurrentPhotoPath() != null) {  // if the user changed their book cover
                        // set the book cover appropriately
                        if (photoHandler.getCurrentPhotoPath().equals("BITMAP")) {
                            // if the user chose a photo from the gallery
                            FirebaseIntegrity.pushNewBookToFirebaseWithBitmap(book,
                                    photoHandler.getGalleryPhoto());
                        } else {  // if the user took a photo
                            FirebaseIntegrity.pushNewBookToFirebaseWithURL(book,
                                    photoHandler.getCurrentPhotoPath());
                        }
                    } else {  // if the user does not have a photo
                        FirebaseIntegrity.pushNewBookToFirebaseWithURL(book, null);
                    }

                    // return from AddBookActivity with a positive result code
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("ABA_BOOK", book);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }
            } else {  // if not all fields are valid
                if (!validTitle) {
                    // set an error and focus the app on the erroneous field
                    layoutBookTitle.setError("Input required");
                    layoutBookTitleContainer.setErrorEnabled(true);
                    layoutBookTitle.requestFocus();
                } else if (!validAuthor) {
                    // set an error and focus the app on the erroneous field
                    layoutBookAuthor.setError("Input required");
                    layoutBookAuthorContainer.setErrorEnabled(true);
                    layoutBookAuthor.requestFocus();
                } else {
                    String isbn = Objects.requireNonNull(layoutBookISBN
                            .getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (isbn.equals("")) {
                        layoutBookISBN.setError("Input required");
                    } else {
                        layoutBookISBN.setError("Invalid ISBN");
                    }
                    layoutBookISBNContainer.setErrorEnabled(true);
                    layoutBookISBN.requestFocus();
                }
            }
            saveButton.setClickable(true);
        });
    }

    /**
     * Back button
     */
    @Override
    public void onBackPressed() {
        if (noChanges()) {  // if the user changed nothing
            finish();
        } else {  // if the user has entered some text or chosen a photo
            showCancelDialog();
        }
    }

    /**
     * Spinner Dialog that allows the user to choose the book's condition
     */
    private void showSpinnerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_condition_spinner, null);

        // access the spinner text fields
        TextView greatOption = view.findViewById(R.id.spinnerDialogGreatField);
        TextView goodOption = view.findViewById(R.id.spinnerDialogGoodField);
        TextView fairOption = view.findViewById(R.id.spinnerDialogFairField);
        TextView acceptableOption = view.findViewById(R.id.spinnerDialogAcceptableField);
        TextView selectedOption;

        // if the condition layout is not null and has text
        if ((layoutBookCondition != null) && (layoutBookCondition.getText() != null)) {
            // set selectedOption based on the condition layout text
            switch (layoutBookCondition.getText().toString()) {
                case "GREAT":
                    selectedOption = greatOption;
                    break;
                case "GOOD":
                    selectedOption = goodOption;
                    break;
                case "FAIR":
                    selectedOption = fairOption;
                    break;
                default:
                    selectedOption = acceptableOption;
                    break;
            }

            // set the background color of the selected option to red
            selectedOption.setBackgroundColor(ContextCompat
                    .getColor(getBaseContext(), R.color.colorAccent));

            // set the text color of the selected option to white
            selectedOption.setTextColor(ContextCompat
                    .getColor(getBaseContext(), R.color.textWhite));
        }

        // create the condition dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // set the condition layout text to great when greatOption is clicked
        greatOption.setOnClickListener(v -> {
            greatOption.setClickable(false);
            alertDialog.dismiss();
            layoutBookCondition.setText(R.string.greatCondition);
            greatOption.setClickable(true);
        });

        // set the condition layout text to good when goodOption is clicked
        goodOption.setOnClickListener(v -> {
            goodOption.setClickable(false);
            alertDialog.dismiss();
            layoutBookCondition.setText(R.string.goodCondition);
            goodOption.setClickable(true);
        });

        // set the condition layout text to fair when fairOption is clicked
        fairOption.setOnClickListener(v -> {
            fairOption.setClickable(false);
            alertDialog.dismiss();
            layoutBookCondition.setText(R.string.fairCondition);
            fairOption.setClickable(true);
        });

        // set the condition layout text to acceptable when acceptableOption is clicked
        acceptableOption.setOnClickListener(v -> {
            acceptableOption.setClickable(false);
            alertDialog.dismiss();
            layoutBookCondition.setText(R.string.acceptableCondition);
            acceptableOption.setClickable(true);
        });
    }

    /**
     * Cancel Dialog that prompts the user to keep editing or to discard their changes
     */
    private void showCancelDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_discard_changes, null);

        // access the views for the buttons
        Button keepEditingBtn = view.findViewById(R.id.keepEditingBtn);
        TextView discardBtn = view.findViewById(R.id.discardBtn);

        // create the cancel dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // stay in this activity if the user opts to keep editing
        keepEditingBtn.setOnClickListener(v -> {
            keepEditingBtn.setClickable(false);
            alertDialog.dismiss();
            keepEditingBtn.setClickable(true);
        });

        // finish this activity if the user opts to discard their changes
        discardBtn.setOnClickListener(v -> {
            discardBtn.setClickable(false);
            alertDialog.dismiss();
            SystemClock.sleep(300);
            finish();
            discardBtn.setClickable(true);
        });
    }

    /**
     * Checks if the user has not entered any text or chosen a photo
     * @return true if the user has not changed anything, false otherwise
     */
    private boolean noChanges() {
        return String.valueOf(layoutBookTitle.getText()).equals("")
                && String.valueOf(layoutBookAuthor.getText()).equals("")
                && String.valueOf(layoutBookISBN.getText()).equals("")
                && String.valueOf(layoutBookCondition.getText()).equals("FAIR")
                && String.valueOf(layoutBookComment.getText()).equals("")
                && (photoHandler.getCurrentPhotoPath() == null)
                ;
    }

    /**
     * Sets the user's photo to the image from either the camera or the gallery.
     * @param requestCode code that the image activity was launched with
     * @param resultCode code that the image activity returns
     * @param data data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        (photoHandler).onActivityResult(this, R.id.addBookBookCoverField,
                requestCode, resultCode, data);
    }
}
