package com.example.pocketbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Allows the user to add a new book to be placed/available on PocketBook
 */
public class AddBookActivity extends AppCompatActivity {

//    Book book;
    User currentUser;

    private Boolean validTitle;
    private Boolean validAuthor;
    private Boolean validISBN;
    private int LAUNCH_CAMERA_CODE = 1408;
    private int LAUNCH_GALLERY_CODE = 1922;

    String currentPhotoPath;
    Bitmap currentPhoto;
    Boolean removePhoto;
    StorageReference defaultBookCover;

    TextInputEditText layoutBookTitle;
    TextInputEditText layoutBookAuthor;
    TextInputEditText layoutBookISBN;
    ImageView layoutBookCover;
    TextInputEditText layoutBookCondition;
    TextInputEditText layoutBookComment;

    TextInputLayout layoutBookTitleContainer;
    TextInputLayout layoutBookAuthorContainer;
    TextInputLayout layoutBookISBNContainer;
    TextInputLayout layoutBookConditionContainer;
    TextInputLayout layoutBookCommentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("HA_USER");
        defaultBookCover = FirebaseStorage.getInstance().getReference()
                .child("default_images").child("no_book_cover_light.png");

        removePhoto = false;
        validTitle = false;
        validAuthor = false;
        validISBN = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.addBookToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.addBookCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.addBookSaveBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.addBookChangePhotoBtn);

        layoutBookTitle = (TextInputEditText) findViewById(R.id.addBookTitleField);
        layoutBookAuthor = (TextInputEditText) findViewById(R.id.addBookAuthorField);
        layoutBookISBN = (TextInputEditText) findViewById(R.id.addBookISBNField);
        layoutBookCover = (ImageView) findViewById(R.id.addBookBookCoverField);
        layoutBookCondition = (TextInputEditText) findViewById(R.id.addBookConditionField);
        layoutBookComment = (TextInputEditText) findViewById(R.id.addBookCommentField);

        layoutBookCondition.setText(R.string.fairCondition);

        layoutBookTitleContainer = (TextInputLayout) findViewById(R.id.addBookTitleContainer);
        layoutBookAuthorContainer = (TextInputLayout) findViewById(R.id.addBookAuthorContainer);
        layoutBookISBNContainer = (TextInputLayout) findViewById(R.id.addBookISBNContainer);
        layoutBookConditionContainer = (TextInputLayout) findViewById(R.id.addBookConditionContainer);
        layoutBookCommentContainer = (TextInputLayout) findViewById(R.id.addBookCommentContainer);

        layoutBookTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(Parser.isValidBookTitle(s.toString()))) {
                    layoutBookTitle.setError("Input required");
                    layoutBookTitleContainer.setErrorEnabled(true);
                } else {
                    validTitle = true;
                    layoutBookTitleContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutBookAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!(Parser.isValidBookAuthor(s.toString()))) {
                    layoutBookAuthor.setError("Input required");
                    layoutBookAuthorContainer.setErrorEnabled(true);
                } else {
                    validAuthor = true;
                    layoutBookAuthorContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutBookISBN.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!(Parser.isValidBookIsbn(s.toString()))) {
                    layoutBookISBN.setError("Invalid ISBN");
                    layoutBookISBNContainer.setErrorEnabled(true);
                } else {
                    validISBN = true;
                    layoutBookISBNContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutBookCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinnerDialog();
            }
        });

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectorDialog();
            }
        });

        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(defaultBookCover)
                .into(layoutBookCover);

        // TODO: Handle remove photo (like in EditBookActivity)

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validTitle && validAuthor && validISBN) {
                    if (!noChanges()) {
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

                        String bookId = Parser.generateValidId();

                        // if all booleans are good, pushNewBook
                        Book book = Parser.parseBook(bookId, title, author, isbn,
                                currentUser.getEmail(), "AVAILABLE", comment,
                                condition, "", new ArrayList<>());

                        if (currentPhotoPath != null) {
                            if (currentPhotoPath.equals("BITMAP")) {
                                FirebaseIntegrity.pushNewBookToFirebaseWithBitmap(book,
                                        currentPhoto);
                            } else {
                                FirebaseIntegrity.pushNewBookToFirebaseWithURL(book,
                                        currentPhotoPath);
                            }
                        } else {
                            FirebaseIntegrity.pushNewBookToFirebaseWithURL(book, null);
                        }

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("ABA_BOOK", book);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();

                    }
                } else {
                    if (!validTitle) {
                        layoutBookTitle.setError("Input required");
                        layoutBookTitleContainer.setErrorEnabled(true);
                        layoutBookTitle.requestFocus();
                    } else if (!validAuthor) {
                        layoutBookAuthor.setError("Input required");
                        layoutBookAuthorContainer.setErrorEnabled(true);
                        layoutBookAuthor.requestFocus();
                    } else {
                        layoutBookISBN.setError("Input required");
                        layoutBookISBNContainer.setErrorEnabled(true);
                        layoutBookISBN.requestFocus();
                    }
                }
            }
        });
    }

    /**
     * Back button actions
     */
    @Override
    public void onBackPressed() {
        if (noChanges()) {
            finish();
        } else {
            showCancelDialog();
        }
    }

    /**
     * Spinning Dialog
     */

    private void showSpinnerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_condition_spinner, null);

        TextView greatOption = view.findViewById(R.id.spinnerDialogGreatField);
        TextView goodOption = view.findViewById(R.id.spinnerDialogGoodField);
        TextView fairOption = view.findViewById(R.id.spinnerDialogFairField);
        TextView acceptableOption = view.findViewById(R.id.spinnerDialogAcceptableField);
        TextView selectedOption;

        if (layoutBookCondition != null) {
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
            selectedOption.setBackgroundColor(ContextCompat
                    .getColor(getBaseContext(), R.color.colorAccent));
            selectedOption.setTextColor(ContextCompat
                    .getColor(getBaseContext(), R.color.textWhite));
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        greatOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                layoutBookCondition.setText(R.string.greatCondition);
            }
        });

        goodOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                layoutBookCondition.setText(R.string.goodCondition);
            }
        });

        fairOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                layoutBookCondition.setText(R.string.fairCondition);
            }
        });

        acceptableOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                layoutBookCondition.setText(R.string.acceptableCondition);
            }
        });
    }

    /**
     * Cancel Dialog
     */

    private void showCancelDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_discard_changes, null);

        Button keepEditingBtn = view.findViewById(R.id.keepEditingBtn);
        TextView discardBtn = view.findViewById(R.id.discardBtn);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        keepEditingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SystemClock.sleep(300);
                finish();
            }
        });
    }

    private boolean noChanges() {
        return String.valueOf(layoutBookTitle.getText()).equals("")
                && String.valueOf(layoutBookAuthor.getText()).equals("")
                && String.valueOf(layoutBookISBN.getText()).equals("")
                && String.valueOf(layoutBookCondition.getText()).equals("FAIR")
                && String.valueOf(layoutBookComment.getText()).equals("")
                && (currentPhotoPath == null)
                ;
    }


    /**
     * Image Option dialog
     */

    private void showImageSelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_book_photo, null);

        TextView takePhotoOption = view.findViewById(R.id.takePhotoField);
        TextView choosePhotoOption = view.findViewById(R.id.choosePhotoField);
        TextView removePhotoOption = view.findViewById(R.id.removePhotoField);

//        String bookPhoto = book.getPhoto();

        if (removePhoto) {
            removePhotoOption.setVisibility(View.VISIBLE);
        } else {
            removePhotoOption.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        takePhotoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openCamera();
            }
        });

        choosePhotoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), LAUNCH_GALLERY_CODE);
            }
        });

        removePhotoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String removedPhoto = book.getPhoto();
//                book.setPhoto("");
                alertDialog.dismiss();
                GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                        .load(defaultBookCover)
                        .into(layoutBookCover);
//                book.setPhoto(removedPhoto);
                currentPhotoPath = "REMOVE";
                removePhoto = false;
            }
        });
    }

    /**
     * Initiates the camera
     */

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // display error state to the user
                Log.e("ADD_BOOK_ACTIVITY", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, LAUNCH_CAMERA_CODE);
            }
        } else {
            Log.e("ADD_BOOK_ACTIVITY", "Failed to resolve activity!");
        }

    }
    /**
     * Create an image file for the images to be stored
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * User Activity on the type of image and if it's should be stored or not
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CAMERA_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView myImage = (ImageView) findViewById(R.id.addBookBookCoverField);
                myImage.setImageBitmap(myBitmap);
                removePhoto = true;
                currentPhoto = null;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("ADD_BOOK_ACTIVITY", "Camera failed!");
            }
        } else if (requestCode == LAUNCH_GALLERY_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getBaseContext()
                            .getContentResolver().openInputStream(data.getData());
                    currentPhoto = BitmapFactory.decodeStream(inputStream);
                    currentPhotoPath = "BITMAP";
                    ImageView myImage = (ImageView) findViewById(R.id.addBookBookCoverField);
                    myImage.setImageBitmap(currentPhoto);
                    removePhoto = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("ADD_BOOK_ACTIVITY", "Failed Gallery!");
            }
        }
    }
}
