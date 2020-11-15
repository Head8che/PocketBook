package com.example.pocketbook.activity;

import android.app.Activity;
import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Allows users to edit their Pocketbook profile details
 */
public class EditProfileActivity extends AppCompatActivity {
    private User currentUser = new User();

    private Boolean validFirstName;
    private Boolean validLastName;
    private Boolean validUsername;

    private String userFirstName;
    private String userLastName;
    private String userUsername;
    private String userEmail;

    private int LAUNCH_CAMERA_CODE = 1408;
    private int LAUNCH_GALLERY_CODE = 1922;

    String currentPhotoPath;
    Bitmap galleryPhoto;
    Boolean showRemovePhoto;

    StorageReference defaultPhoto = FirebaseStorage.getInstance().getReference()
            .child("default_images").child("no_profileImg.png");

    TextInputEditText layoutUserFirstName;
    TextInputEditText layoutUserLastName;
    TextInputEditText layoutUserUsername;
    TextInputEditText layoutUserEmail;
    ImageView layoutProfilePicture;

    TextInputLayout layoutUserFirstNameContainer;
    TextInputLayout layoutUserLastNameContainer;
    TextInputLayout layoutUserUsernameContainer;
    TextInputLayout layoutUserEmailContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");

        // return if the intent passes in a null user
        if (currentUser == null) {
            return;
        }

        // access the user's profile picture
        StorageReference profilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);

        // initialize validation booleans to false
        userFirstName = currentUser.getFirstName();
        userLastName = currentUser.getLastName();
        userUsername = currentUser.getUsername();
        userEmail = currentUser.getEmail();

        showRemovePhoto = (currentUser.getPhoto() != null) && (!currentUser.getPhoto().equals(""));
        validFirstName = true;
        validLastName = true;
        validUsername = true;

        // Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.editProfileCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.editProfileSaveBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.editProfileChangePhotoBtn);

        // access the layout text fields
        layoutUserFirstName = (TextInputEditText) findViewById(R.id.editProfileFirstNameField);
        layoutUserLastName = (TextInputEditText) findViewById(R.id.editProfileLastNameField);
        layoutUserUsername = (TextInputEditText) findViewById(R.id.editProfileUsernameField);
        layoutUserEmail = (TextInputEditText) findViewById(R.id.editProfileEmailField);
        layoutProfilePicture = (ImageView) findViewById(R.id.editProfileProfilePictureField);

        // set the layout text fields to the appropriate user variables
        layoutUserFirstName.setText(userFirstName);
        layoutUserLastName.setText(userLastName);
        layoutUserUsername.setText(userUsername);
        layoutUserEmail.setText(userEmail);

        // access the layout text containers
        layoutUserFirstNameContainer = (TextInputLayout)
                findViewById(R.id.editProfileFirstNameContainer);
        layoutUserLastNameContainer = (TextInputLayout)
                findViewById(R.id.editProfileLastNameContainer);
        layoutUserUsernameContainer = (TextInputLayout)
                findViewById(R.id.editProfileUsernameContainer);
        layoutUserEmailContainer = (TextInputLayout)
                findViewById(R.id.editProfileEmailContainer);

        // add a text field listener that validates the inputted text
        layoutUserFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidFirstName(s.toString()))) {
                    layoutUserFirstName.setError("Input required");
                    layoutUserFirstNameContainer.setErrorEnabled(true);
                    validFirstName = false;
                } else {  // if the inputted text is valid
                    validFirstName = true;
                    layoutUserFirstName.setError(null);
                    layoutUserFirstNameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // add a text field listener that validates the inputted text
        layoutUserLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidLastName(s.toString()))) {
                    layoutUserLastName.setError("Input required");
                    layoutUserLastNameContainer.setErrorEnabled(true);
                    validLastName = false;
                } else {  // if the inputted text is valid
                    validLastName = true;
                    layoutUserLastName.setError(null);
                    layoutUserLastNameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // add a text field listener that validates the inputted text
        layoutUserUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidUsername(s.toString()))) {
                    layoutUserUsername.setError("Input required");
                    layoutUserUsernameContainer.setErrorEnabled(true);
                    validUsername = false;
                } else {  // if the inputted text is valid
                    validUsername = true;
                    layoutUserUsername.setError(null);
                    layoutUserUsernameContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // showImageSelectorDialog when changePhotoButton is clicked
        changePhotoButton.setOnClickListener(v -> showImageSelectorDialog());

        // load profile picture into ImageLayout
        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(profilePicture)
                .into(layoutProfilePicture);

        // go back when cancelButton is clicked
        cancelButton.setOnClickListener(v -> onBackPressed());

        // when saveButton is clicked
        saveButton.setOnClickListener(v -> {

            // if all fields are valid
            if (validFirstName && validLastName && validUsername) {
                if (!noChanges()) {  // if the user has changed some text or changed their photo

                    // extract the layout text field values into variables
                    String newFirstName = Objects.requireNonNull(layoutUserFirstName.getText())
                            .toString();
                    String newLastName = Objects.requireNonNull(layoutUserLastName.getText())
                            .toString();
                    String newUsername = Objects.requireNonNull(layoutUserUsername.getText())
                            .toString();

                    if (!(userFirstName.equals(newFirstName))) {
                        // handle the user changing their first name
                        FirebaseIntegrity.setFirstNameFirebase(currentUser, newFirstName);
                    }
                    if (!(userLastName.equals(newLastName))) {
                        // handle the user changing their last name
                        FirebaseIntegrity.setLastNameFirebase(currentUser, newLastName);
                    }
                    if (!(userUsername.equals(newUsername))) {
                        // handle the user changing their username
                        FirebaseIntegrity.setUsernameFirebase(currentUser, newUsername);
                    }
                    if (currentPhotoPath != null) {
                        if (currentPhotoPath.equals("BITMAP")) {
                            FirebaseIntegrity.setUserProfilePictureBitmap(currentUser,
                                    galleryPhoto);
                        } else {
                            FirebaseIntegrity.setUserProfilePicture(currentUser, currentPhotoPath);
                        }
                    }
                }
                finish();
            } else {  // if not all fields are valid
                if (!validFirstName) {
                    // set an error and focus the app on the erroneous field
                    layoutUserFirstName.setError("Input required");
                    layoutUserFirstNameContainer.setErrorEnabled(true);
                    layoutUserFirstName.requestFocus();
                } else if (!validLastName) {
                    // set an error and focus the app on the erroneous field
                    layoutUserLastName.setError("Input required");
                    layoutUserLastNameContainer.setErrorEnabled(true);
                    layoutUserLastName.requestFocus();
                } else {
                    // set an error and focus the app on the erroneous field
                    layoutUserUsername.setError("Input required");
                    layoutUserUsernameContainer.setErrorEnabled(true);
                    layoutUserUsername.requestFocus();
                }
            }
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
        keepEditingBtn.setOnClickListener(v -> alertDialog.dismiss());

        // finish this activity if the user opts to discard their changes
        discardBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            SystemClock.sleep(300);
            finish();
        });
    }

    /**
     * Checks if the user has not changed any text or changed their photo
     * @return true if the user has not changed anything, false otherwise
     */
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
     * Image Option dialog that allows the user to take, choose, or remove a photo
     */
    private void showImageSelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_book_photo, null);

        // access the photo option text fields
        TextView takePhotoOption = view.findViewById(R.id.takePhotoField);
        TextView choosePhotoOption = view.findViewById(R.id.choosePhotoField);
        TextView showRemovePhotoOption = view.findViewById(R.id.removePhotoField);

        if (showRemovePhoto) {  // only show the Remove Photo option if the user has a photo
            showRemovePhotoOption.setVisibility(View.VISIBLE);
        } else {
            showRemovePhotoOption.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // if the user opts to take a photo, open the camera
        takePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            openCamera();
        });

        // if the user opts to choose a photo, open their gallery
        choosePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                    LAUNCH_GALLERY_CODE);
        });

        // if the user opts to remove their photo, replace their image with the default image
        showRemovePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                    .load(defaultPhoto)
                    .into(layoutProfilePicture);
            currentPhotoPath = "REMOVE";
            showRemovePhoto = false;  // don't show Remove Photo option since user has no photo
        });
    }

    /**
     * Allows the camera to be initiated upon request from the user
     */
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // catch errors that occur while creating the file
                Log.e("EDIT_PROFILE_ACTIVITY", ex.toString());
            }
            // continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                // open the camera
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, LAUNCH_CAMERA_CODE);
            }
        } else {  // if there's no camera activity to handle the intent
            Log.e("EDIT_PROFILE_ACTIVITY", "Failed to resolve activity!");
        }

    }

    /**
     * Create an image file for the images to be stored
     * @return the created image
     * @throws IOException exception if creating the image file fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.CANADA).format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
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
     * Sets the user's photo to the image from either the camera or the gallery.
     * @param requestCode code that the image activity was launched with
     * @param resultCode code that the image activity returns
     * @param data data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the user launched the camera
        if (requestCode == LAUNCH_CAMERA_CODE) {
            if(resultCode == Activity.RESULT_OK) {  // if a photo was successfully chosen
                // set the profile picture ImageView to the chosen image
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView myImage = (ImageView)
                        findViewById(R.id.editProfileProfilePictureField);
                myImage.setImageBitmap(myBitmap);
                showRemovePhoto = true;  // show Remove Photo option since user now has a photo
                galleryPhoto = null;  // nullify the gallery photo variable
            } else if (resultCode == Activity.RESULT_CANCELED) {  // if the activity was cancelled
                Log.e("EDIT_PROFILE_ACTIVITY", "Camera failed!");
            }
        } else if (requestCode == LAUNCH_GALLERY_CODE) {  // if the user launched the gallery
            if(resultCode == Activity.RESULT_OK) {  // if a photo was successfully selected
                try {  // try to get a Bitmap of the selected image
                    InputStream inputStream = getBaseContext()
                            .getContentResolver()
                            .openInputStream(Objects.requireNonNull(data.getData()));
                    // store the selected image in currentPhoto
                    galleryPhoto = BitmapFactory.decodeStream(inputStream);
                    currentPhotoPath = "BITMAP";
                    ImageView myImage = (ImageView)
                            findViewById(R.id.editProfileProfilePictureField);
                    myImage.setImageBitmap(galleryPhoto);
                    showRemovePhoto = true;  // show Remove Photo option since user now has a photo

                } catch (FileNotFoundException e) {  // handle when the selected image is not found
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {  // if the activity was cancelled
                Log.e("EDIT_PROFILE_ACTIVITY", "Failed Gallery!");
            }
        }
    }
}

