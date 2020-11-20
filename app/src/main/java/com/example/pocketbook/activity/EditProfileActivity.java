package com.example.pocketbook.activity;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.example.pocketbook.util.PhotoHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Allows users to edit their Pocketbook profile details
 */
public class EditProfileActivity extends AppCompatActivity {
    private User currentUser = new User();

    private Boolean validFirstName;
    private Boolean validLastName;
    private Boolean validPhoneNumber;
    private Boolean validUsername;

    private String userFirstName;
    private String userLastName;
    private String userUsername;
    private String userPhoneNumber;
    private String userEmail;

    StorageReference defaultPhoto = FirebaseStorage.getInstance().getReference()
            .child("default_images").child("no_profileImg.png");

    TextInputEditText layoutUserFirstName;
    TextInputEditText layoutUserLastName;
    TextInputEditText layoutUserUsername;
    TextInputEditText layoutUserPhoneNumber;
    TextInputEditText layoutUserEmail;
    ImageView layoutProfilePicture;

    TextInputLayout layoutUserFirstNameContainer;
    TextInputLayout layoutUserLastNameContainer;
    TextInputLayout layoutUserUsernameContainer;
    TextInputLayout layoutUserPhoneNumberContainer;
    TextInputLayout layoutUserEmailContainer;

    PhotoHandler photoHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        photoHandler = new PhotoHandler();

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");

        // return if the intent passes in a null user
        if (currentUser == null) {
            return;
        }

        // access the user's profile picture
        StorageReference profilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);

        // extract user details into variables
        userFirstName = currentUser.getFirstName();
        userLastName = currentUser.getLastName();
        userUsername = currentUser.getUsername();
        userPhoneNumber = currentUser.getPhoneNumber();
        userEmail = currentUser.getEmail();

        photoHandler.setShowRemovePhoto((currentUser.getPhoto() != null)
                && (!currentUser.getPhoto().equals("")));

        // initialize validation booleans to false
        validFirstName = true;
        validLastName = true;
        validUsername = true;
        validPhoneNumber = true;

        // Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.editProfileCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.editProfileSaveBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.editProfileChangePhotoBtn);

        // access the layout text fields
        layoutUserFirstName = (TextInputEditText) findViewById(R.id.editProfileFirstNameField);
        layoutUserLastName = (TextInputEditText) findViewById(R.id.editProfileLastNameField);
        layoutUserUsername = (TextInputEditText) findViewById(R.id.editProfileUsernameField);
        layoutUserPhoneNumber = (TextInputEditText) findViewById(R.id.editProfilePhoneNumberField);
        layoutUserEmail = (TextInputEditText) findViewById(R.id.editProfileEmailField);
        layoutProfilePicture = (ImageView) findViewById(R.id.editProfileProfilePictureField);

        // set the layout text fields to the appropriate user variables
        layoutUserFirstName.setText(userFirstName);
        layoutUserLastName.setText(userLastName);
        layoutUserUsername.setText(userUsername);
        layoutUserPhoneNumber.setText(userPhoneNumber);
        layoutUserEmail.setText(userEmail);

        // access the layout text containers
        layoutUserFirstNameContainer = (TextInputLayout)
                findViewById(R.id.editProfileFirstNameContainer);
        layoutUserLastNameContainer = (TextInputLayout)
                findViewById(R.id.editProfileLastNameContainer);
        layoutUserUsernameContainer = (TextInputLayout)
                findViewById(R.id.editProfileUsernameContainer);
        layoutUserPhoneNumberContainer =
                (TextInputLayout) findViewById(R.id.editProfilePhoneNumberContainer);
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

        // add a text field listener that validates the inputted text
        layoutUserPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if ((s.toString().length() > 0) && !(Parser.isValidPhoneNumber(s.toString()))) {
                    layoutUserPhoneNumber.setError("Invalid Phone Number");
                    layoutUserPhoneNumberContainer.setErrorEnabled(true);
                    validPhoneNumber = false;
                } else {  // if the inputted text is valid
                    validPhoneNumber = true;
                    layoutUserPhoneNumber.setError(null);
                    layoutUserPhoneNumberContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // showImageSelectorDialog when changePhotoButton is clicked
        changePhotoButton.setOnClickListener(v -> (photoHandler)
                .showImageSelectorDialog(this, defaultPhoto, layoutProfilePicture));

        // load profile picture into ImageLayout
        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(profilePicture)
                .into(layoutProfilePicture);

        // go back when cancelButton is clicked
        cancelButton.setOnClickListener(v -> onBackPressed());

        // when saveButton is clicked
        saveButton.setOnClickListener(v -> {

            // if all fields are valid
            if (validFirstName && validLastName && validUsername && validPhoneNumber) {
                if (!noChanges()) {  // if the user has changed some text or changed their photo

                    // extract the layout text field values into variables
                    String newFirstName = Objects.requireNonNull(layoutUserFirstName.getText())
                            .toString();
                    String newLastName = Objects.requireNonNull(layoutUserLastName.getText())
                            .toString();
                    String newUsername = Objects.requireNonNull(layoutUserUsername.getText())
                            .toString();
                    String newPhoneNumber = Objects.requireNonNull(layoutUserPhoneNumber
                            .getText())
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
                    if (!(userPhoneNumber.equals(newPhoneNumber))) {
                        // handle the user changing their username
                        FirebaseIntegrity.setPhoneNumberFirebase(currentUser, newPhoneNumber);
                    }
                    if (photoHandler.getCurrentPhotoPath() != null) {
                        if (photoHandler.getCurrentPhotoPath().equals("BITMAP")) {
                            FirebaseIntegrity.setUserProfilePictureBitmap(currentUser,
                                    photoHandler.getGalleryPhoto());
                        } else {
                            FirebaseIntegrity.setUserProfilePicture(currentUser,
                                    photoHandler.getCurrentPhotoPath());
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
                } else if (!validPhoneNumber) {
                    // set an error and focus the app on the erroneous field
                    layoutUserPhoneNumber.setError("Invalid Phone Number");
                    layoutUserPhoneNumberContainer.setErrorEnabled(true);
                    layoutUserPhoneNumber.requestFocus();
                }else {
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
                && userPhoneNumber
                .equals(Objects.requireNonNull(layoutUserPhoneNumber.getText()).toString())
                && userEmail
                .equals(Objects.requireNonNull(layoutUserEmail.getText()).toString())
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

        (photoHandler).onActivityResult(this, R.id.editProfileProfilePictureField,
                requestCode, resultCode, data);
    }
}

