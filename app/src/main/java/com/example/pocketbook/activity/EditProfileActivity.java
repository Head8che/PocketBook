package com.example.pocketbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * User Edit Profile
 * Allows user to update (First name/ Last name)
 * User inputs are validated
 */
public class EditProfileActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private EditText userEmail1;

    String first_name;
    String last_name;
    String user_name;
    String email;
    String profileImg;

    private ImageView userProfileImg, backButton;
    private TextView changeProfilePicture, saveButton;
    private static final String USER = "users";
    private static final String TAG = "EditProfileActivity";
    private User currentUser = new User();
    Uri filePath;
    ProgressDialog pd;

    private Boolean validFirstName;
    private Boolean validLastName;
    private Boolean validUsername;
    private Boolean validUserEmail;

    private String userFirstName;
    private String userLastName;
    private String userUsername;
    private String userEmail;

    private int LAUNCH_CAMERA_CODE = 1408;
    private int LAUNCH_GALLERY_CODE = 1922;

    String currentPhotoPath;
    Bitmap currentPhoto;
    Boolean removePhoto;

    TextInputEditText layoutUserFirstName;
    TextInputEditText layoutUserLastName;
    TextInputEditText layoutUserUsername;
    TextInputEditText layoutUserEmail;
    ImageView layoutProfilePicture;

    TextInputLayout layoutUserFirstNameContainer;
    TextInputLayout layoutUserLastNameContainer;
    TextInputLayout layoutUserUsernameContainer;
    TextInputLayout layoutUserEmailContainer;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocketbook-t09.appspot.com/profile_pictures");
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");
        StorageReference profilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);

        Log.e("TAG_PROFILE", profilePicture.getName());

        userFirstName = currentUser.getFirstName();
        userLastName = currentUser.getLastName();
        userUsername = currentUser.getUsername();
        userEmail = currentUser.getEmail();

        removePhoto = (currentUser.getPhoto() != null) && (!currentUser.getPhoto().equals(""));
        validFirstName = true;
        validLastName = true;
        validUsername = true;
        validUserEmail = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.editProfileCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.editProfileSaveBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.editProfileChangePhotoBtn);

        layoutUserFirstName = (TextInputEditText) findViewById(R.id.editProfileFirstNameField);
        layoutUserLastName = (TextInputEditText) findViewById(R.id.editProfileLastNameField);
        layoutUserUsername = (TextInputEditText) findViewById(R.id.editProfileUsernameField);
        layoutUserEmail = (TextInputEditText) findViewById(R.id.editProfileEmailField);
        layoutProfilePicture = (ImageView) findViewById(R.id.editProfileProfilePictureField);

        layoutUserFirstName.setText(userFirstName);
        layoutUserLastName.setText(userLastName);
        layoutUserUsername.setText(userUsername);
        layoutUserEmail.setText(userEmail);

        layoutUserFirstNameContainer = (TextInputLayout) findViewById(R.id.editProfileFirstNameContainer);
        layoutUserLastNameContainer = (TextInputLayout) findViewById(R.id.editProfileLastNameContainer);
        layoutUserUsernameContainer = (TextInputLayout) findViewById(R.id.editProfileUsernameContainer);
        layoutUserEmailContainer = (TextInputLayout) findViewById(R.id.editProfileEmailContainer);

        layoutUserFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(Parser.isValidFirstName(s.toString()))) {
                    layoutUserFirstName.setError("Input required");
                    layoutUserFirstNameContainer.setErrorEnabled(true);
                    validFirstName = false;
                } else {
                    validFirstName = true;
                    layoutUserFirstNameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutUserLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!(Parser.isValidLastName(s.toString()))) {
                    layoutUserLastName.setError("Input required");
                    layoutUserLastNameContainer.setErrorEnabled(true);
                    validLastName = false;
                } else {
                    validLastName = true;
                    layoutUserLastNameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutUserUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!(Parser.isValidUsername(s.toString()))) {
                    layoutUserUsername.setError("Input required");
                    layoutUserUsernameContainer.setErrorEnabled(true);
                    validUsername = false;
                } else {
                    validUsername = true;
                    layoutUserUsernameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!(Parser.isValidUserEmail(s.toString()))) {
                    layoutUserEmail.setError("Input required");
                    layoutUserEmailContainer.setErrorEnabled(true);
                    validUserEmail = false;
                } else {
                    validUserEmail = true;
                    layoutUserEmailContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectorDialog();
            }
        });

        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(profilePicture)
                .into(layoutProfilePicture);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validFirstName && validLastName && validUsername && validUserEmail) {
                    if (!noChanges()) {
                        String newFirstName = Objects.requireNonNull(layoutUserFirstName.getText())
                                .toString();
                        String newLastName = Objects.requireNonNull(layoutUserLastName.getText())
                                .toString();
                        String newUsername = Objects.requireNonNull(layoutUserUsername.getText())
                                .toString();
                        String newUserEmail = Objects.requireNonNull(layoutUserEmail.getText())
                                .toString();

                        if (!(userFirstName.equals(newFirstName))) {
                            FirebaseIntegrity.setFirstNameFirebase(currentUser, newFirstName);
                        }
                        if (!(userLastName.equals(newLastName))) {
                            FirebaseIntegrity.setLastNameFirebase(currentUser, newLastName);
                        }
                        if (!(userUsername.equals(newUsername))) {
                            FirebaseIntegrity.setUsernameFirebase(currentUser, newUsername);
                        }
                        if (!(userEmail.equals(newUserEmail))) {
                            FirebaseIntegrity.setEmailFirebase(currentUser, newUserEmail);
                        }
                        if (currentPhotoPath != null) {
                            if (currentPhotoPath.equals("BITMAP")) {
                                FirebaseIntegrity.setUserProfilePictureBitmap(currentUser, currentPhoto);
                            } else {
                                FirebaseIntegrity.setUserProfilePicture(currentUser, currentPhotoPath);
                            }
                        }
                    }
                    finish();
                } else {
                    if (!validFirstName) {
                        layoutUserFirstName.setError("Input required");
                        layoutUserFirstNameContainer.setErrorEnabled(true);
                        layoutUserFirstName.requestFocus();
                    } else if (!validLastName) {
                        layoutUserLastName.setError("Input required");
                        layoutUserLastNameContainer.setErrorEnabled(true);
                        layoutUserLastName.requestFocus();
                    } else if (!validUsername) {
                        layoutUserUsername.setError("Input required");
                        layoutUserUsernameContainer.setErrorEnabled(true);
                        layoutUserUsername.requestFocus();
                    } else {
                        layoutUserEmail.setError("Input required");
                        layoutUserEmailContainer.setErrorEnabled(true);
                        layoutUserEmail.requestFocus();
                    }
                }
            }
        });
    }

    /**
     * Back button
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
        return userFirstName
                .equals(Objects.requireNonNull(layoutUserFirstName.getText()).toString())
                && userLastName
                .equals(Objects.requireNonNull(layoutUserLastName.getText()).toString())
                && userUsername
                .equals(Objects.requireNonNull(layoutUserUsername.getText()).toString())
                && userEmail
                .equals(Objects.requireNonNull(layoutUserEmail.getText()).toString())
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

        // TODO: Handle removing photo! Can't just overwrite current photo before save
        removePhotoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removedPhoto = currentUser.getPhoto();
//                currentUser.setPhoto("");
                alertDialog.dismiss();
                GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                        .load(FirebaseIntegrity.getUserProfilePicture(currentUser))
                        .into(layoutProfilePicture);
//                currentUser.setPhoto(removedPhoto);
//                FirebaseIntegrity.setBookPhotoFirebase(currentUser, "");
                currentPhotoPath = "REMOVE";
                removePhoto = false;
            }
        });
    }

    /**
     * Allows the camera to be initiated upon request from the user
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
                Log.e("EDIT_PROFILE_ACTIVITY", ex.toString());
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
            Log.e("EDIT_PROFILE_ACTIVITY", "Failed to resolve activity!");
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
     * Allows the users to set the bitmap image from the gallery to the image field.
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
                ImageView myImage = (ImageView) findViewById(R.id.editProfileProfilePictureField);
                myImage.setImageBitmap(myBitmap);
                removePhoto = true;
                currentPhoto = null;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("EDIT_PROFILE_ACTIVITY", "Camera failed!");
            }
        } else if (requestCode == LAUNCH_GALLERY_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getBaseContext()
                            .getContentResolver().openInputStream(data.getData());
                    currentPhoto = BitmapFactory.decodeStream(inputStream);
                    currentPhotoPath = "BITMAP";
                    ImageView myImage = (ImageView) findViewById(R.id.editProfileProfilePictureField);
                    myImage.setImageBitmap(currentPhoto);
                    removePhoto = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("EDIT_PROFILE_ACTIVITY", "Failed Gallery!");
            }
        }
    }


}

