package com.example.pocketbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class EditBookActivity extends AppCompatActivity {

    Book book;

    private String bookTitle;
    private String bookAuthor;
    private String bookISBN;
    private String bookCondition;
    private String bookComment;
    private int LAUNCH_CAMERA_CODE = 1408;

    String currentPhotoPath;

    TextInputEditText layoutBookTitle;
    TextInputEditText layoutBookAuthor;
    TextInputEditText layoutBookISBN;
    ImageView layoutBookCover;
    TextInputEditText layoutBookCondition;
    TextInputEditText layoutBookComment;

    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("VMBBF_BOOK");
        StorageReference bookCover = book.getBookCover();
        Log.e("EDIT_BOOK_ACTIVITY", "got book cover ");

        bookTitle = book.getTitle();
        bookAuthor = book.getAuthor();
        bookISBN = book.getISBN();
        bookCondition = book.getCondition();
        bookComment = book.getComment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.editBookToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.editBookCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.editBookSaveBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.editBookChangePhotoBtn);

        layoutBookTitle = (TextInputEditText) findViewById(R.id.editBookTitleField);
        layoutBookAuthor = (TextInputEditText) findViewById(R.id.editBookAuthorField);
        layoutBookISBN = (TextInputEditText) findViewById(R.id.editBookISBNField);
        layoutBookCover = (ImageView) findViewById(R.id.editBookBookCoverField);
        layoutBookCondition = (TextInputEditText) findViewById(R.id.editBookConditionField);
        layoutBookComment = (TextInputEditText) findViewById(R.id.editBookCommentField);

        layoutBookTitle.setText(bookTitle);
        layoutBookAuthor.setText(bookAuthor);
        layoutBookISBN.setText(bookISBN);
        layoutBookCondition.setText(bookCondition);
        layoutBookComment.setText(bookComment);

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
                .load(bookCover)
                .signature(new ObjectKey(String.valueOf(book.getPhotoCacheValue())))
                .into(layoutBookCover);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noChanges()) {
                    String newTitle = layoutBookTitle.getText().toString();
                    String newAuthor = layoutBookAuthor.getText().toString();
                    String newISBN = layoutBookISBN.getText().toString();
                    String newCondition = layoutBookCondition.getText().toString();
                    String newComment = layoutBookComment.getText().toString();

                    if (!(bookTitle.equals(newTitle))) {
                        book.setTitle(newTitle);
                    }
                    if (!(bookAuthor.equals(newAuthor))) {
                        book.setAuthor(newAuthor);
                    }
                    if (!(bookISBN.equals(newISBN))) {
                        book.setIsbn(newISBN);
                    }
                    if (!(bookCondition.equals(newCondition))) {
                        book.setCondition(newCondition);
                    }
                    if (!(bookComment.equals(newComment))) {
                        book.setComment(newComment);
                    }
                    if (currentPhotoPath != null) {
                        book.setBookCover(currentPhotoPath);
                    }

                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (noChanges()) {
            finish();
        } else {
            showCancelDialog();
        }
    }

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
        return bookTitle.equals(layoutBookTitle.getText().toString())
                && bookAuthor.equals(layoutBookAuthor.getText().toString())
                && bookISBN.equals(layoutBookISBN.getText().toString())
                && bookCondition.equals(layoutBookCondition.getText().toString())
                && bookComment.equals(layoutBookComment.getText().toString())
                && (currentPhotoPath == null)
                ;
    }

    private void showImageSelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_book_photo, null);

        TextView takePhotoOption = view.findViewById(R.id.takePhotoField);
        TextView choosePhotoOption = view.findViewById(R.id.choosePhotoField);
        TextView removePhotoOption = view.findViewById(R.id.removePhotoField);

        String bookPhoto = book.getPhoto();

        if ((bookPhoto == null) || (bookPhoto.equals(""))) {
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
    }

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
                Log.e("EDIT_BOOK_ACTIVITY", ex.toString());
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
            Log.e("EDIT_BOOK_ACTIVITY", "Failed to resolve activity!");
        }

    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CAMERA_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView myImage = (ImageView) findViewById(R.id.editBookBookCoverField);
                myImage.setImageBitmap(myBitmap);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("EDIT_BOOK_ACTIVITY", "Camera failed!");
            }
        }
    }
}