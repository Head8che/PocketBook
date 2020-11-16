package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.Toast;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * Allows users to create a Pocketbook account with valid credentials
 */
public class SignUpActivity extends AppCompatActivity {

    private Boolean validFirstName;
    private Boolean validLastName;
    private Boolean validUsername;
    private Boolean validUserEmail;
    private Boolean validUserPassword;

    private int LAUNCH_CAMERA_CODE = 1408;
    private int LAUNCH_GALLERY_CODE = 1922;

    String currentPhotoPath;
    Bitmap currentPhoto;
    Boolean showRemovePhoto;

    User currentUser;
    StorageReference defaultPhoto = FirebaseStorage.getInstance().getReference()
            .child("default_images").child("no_profileImg.png");

    TextInputEditText layoutUserFirstName;
    TextInputEditText layoutUserLastName;
    TextInputEditText layoutUserUsername;
    TextInputEditText layoutUserEmail;
    TextInputEditText layoutUserPassword;
    ImageView layoutProfilePicture;

    TextInputLayout layoutUserFirstNameContainer;
    TextInputLayout layoutUserLastNameContainer;
    TextInputLayout layoutUserUsernameContainer;
    TextInputLayout layoutUserEmailContainer;
    TextInputLayout layoutUserPasswordContainer;

    TextView signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        showRemovePhoto = false;  // user without a photo can't remove photo

        // initialize validation booleans to false
        validFirstName = false;
        validLastName = false;
        validUsername = false;
        validUserEmail = false;
        validUserPassword = false;

//        Toolbar toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
        ImageView backButton = (ImageView) findViewById(R.id.signUpBackBtn);
        signUpButton = (TextView) findViewById(R.id.signUpSignUpBtn);
        TextView changePhotoButton = (TextView) findViewById(R.id.signUpChangePhotoBtn);

        // access the layout text fields
        layoutUserFirstName = (TextInputEditText) findViewById(R.id.signUpFirstNameField);
        layoutUserLastName = (TextInputEditText) findViewById(R.id.signUpLastNameField);
        layoutUserUsername = (TextInputEditText) findViewById(R.id.signUpUsernameField);
        layoutUserEmail = (TextInputEditText) findViewById(R.id.signUpEmailField);
        layoutUserPassword = (TextInputEditText) findViewById(R.id.signUpPasswordField);
        layoutProfilePicture = (ImageView) findViewById(R.id.signUpProfilePictureField);

        // access the layout text containers
        layoutUserFirstNameContainer = (TextInputLayout)
                findViewById(R.id.signUpFirstNameContainer);
        layoutUserLastNameContainer = (TextInputLayout) findViewById(R.id.signUpLastNameContainer);
        layoutUserUsernameContainer = (TextInputLayout) findViewById(R.id.signUpUsernameContainer);
        layoutUserEmailContainer = (TextInputLayout) findViewById(R.id.signUpEmailContainer);
        layoutUserPasswordContainer = (TextInputLayout) findViewById(R.id.signUpPasswordContainer);

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
        layoutUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidUserEmail(s.toString()))) {
                    // if the inputted text is not all in lowercase
                    if (!(s.toString().toLowerCase().equals(s.toString()))) {
                        layoutUserEmail.setError("Email must be lowercase!");
                    } else if (s.toString().equals("")) {  // if the inputted text is empty
                        layoutUserEmail.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutUserEmail.setError("Invalid Email");
                    }
                    layoutUserEmailContainer.setErrorEnabled(true);
                    validUserEmail = false;
                } else {  // if the inputted text is valid
                    validUserEmail = true;
                    layoutUserEmail.setError(null);
                    layoutUserEmailContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // add a text field listener that validates the inputted text
        layoutUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidPassword(s.toString()))) {
                    // if the inputted text is empty
                    if (s.toString().equals("")) {
                        layoutUserPassword.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutUserPassword.setError("Must be 6 characters or longer!");
                    }
                    layoutUserPasswordContainer.setErrorEnabled(true);
                    validUserPassword = false;
                } else {  // if the inputted text is valid
                    validUserPassword = true;
                    layoutUserPassword.setError(null);
                    layoutUserPasswordContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // showImageSelectorDialog when changePhotoButton is clicked
        changePhotoButton.setOnClickListener(v -> showImageSelectorDialog());

        // load default photo into ImageLayout
        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(defaultPhoto)
                .into(layoutProfilePicture);

        // go back when backButton is clicked
        backButton.setOnClickListener(v -> onBackPressed());

        // when signUpButton is clicked
        signUpButton.setOnClickListener(v -> {

            // if all fields are valid
            if (validFirstName && validLastName && validUsername
                    && validUserEmail && validUserPassword) {

                if (!noChanges()) {  // if the user has entered some text or chosen a photo

                    // extract the layout text field values into variables
                    String newFirstName = Objects.requireNonNull(layoutUserFirstName.getText())
                            .toString();
                    String newLastName = Objects.requireNonNull(layoutUserLastName.getText())
                            .toString();
                    String newUsername = Objects.requireNonNull(layoutUserUsername.getText())
                            .toString();
                    String newUserEmail = Objects.requireNonNull(layoutUserEmail.getText())
                            .toString();
                    String newUserPassword = Objects.requireNonNull(layoutUserPassword.getText())
                            .toString();

                    // get a User variable from the valid inputted fields
                    currentUser = Parser.parseUser(newFirstName, newLastName, newUserEmail,
                            newUsername, newUserPassword, "");

                    // user can't create account multiple times with multi-click
                    signUpButton.setClickable(false);

                    // attempt to sign the user up
                    signUpUser();

                } else {
                    finish();
                }

            } else {
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
                } else if (!validUsername) {
                    // set an error and focus the app on the erroneous field
                    layoutUserUsername.setError("Input required");
                    layoutUserUsernameContainer.setErrorEnabled(true);
                    layoutUserUsername.requestFocus();
                } else if (!validUserEmail) {
                    String email = Objects.requireNonNull(layoutUserEmail.getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (!(email.toLowerCase().equals(email))) {
                        layoutUserEmail.setError("Email must be lowercase!");
                    } else if (email.equals("")) {
                        layoutUserEmail.setError("Input required");
                    } else {
                        layoutUserEmail.setError("Invalid Email");
                    }
                    layoutUserEmailContainer.setErrorEnabled(true);
                    layoutUserEmail.requestFocus();
                } else {
                    String password = Objects.requireNonNull(layoutUserPassword
                            .getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (password.equals("")) {
                        layoutUserPassword.setError("Input required");
                    } else {
                        layoutUserPassword.setError("Must be 6 characters or longer!");
                    }
                    layoutUserPasswordContainer.setErrorEnabled(true);
                    layoutUserPassword.requestFocus();
                }
            }
        });

    }

    /**
     * Validates that the user's username and email are unique
     * and proceeds to createUserAccount if they are
     */
    public void signUpUser() {

        if (currentUser == null) {
            return;
        }

        // get all documents in the database where the inputted email exists
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if the email already exists in the database
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // show an error message and allow the user to sign up again
                            signUpButton.setClickable(true);
                            Toast.makeText(SignUpActivity.this,
                                    "That email already exists!",
                                    Toast.LENGTH_SHORT).show();

                        } else {  // email does not already exist
                            // get all documents in the database where the inputted username exists
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .whereEqualTo("username", currentUser.getUsername())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            // if the username already exists in the database
                                            if (!Objects.requireNonNull(task1.getResult())
                                                    .isEmpty()) {

                                                // show an error message and allow the user
                                                // to sign up again
                                                signUpButton.setClickable(true);
                                                Toast.makeText(SignUpActivity.this,
                                                        "That username already exists!",
                                                        Toast.LENGTH_SHORT).show();

                                            } else {  // username does not already exist
                                                createUserAccount();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Attempts to create a user account with the user's input
     */
    public void createUserAccount() {

        if (currentUser == null) {
            return;
        }

        // extract the user's data into variables
        String firstName = currentUser.getFirstName();
        String lastName = currentUser.getLastName();
        String email = currentUser.getEmail();
        String username = currentUser.getUsername();
        String password = currentUser.getPassword();
        String photo = currentUser.getPhoto();

        // put the user's data into a HashMap object
        HashMap<String, Object> docData = new HashMap<>();
        docData.put("firstName", firstName);
        docData.put("lastName", lastName);
        docData.put("email", email);
        docData.put("username", username);
        docData.put("password", password);
        docData.put("photo", photo);

        // if the user chose a photo from their gallery
        if ((currentPhotoPath != null) && currentPhotoPath.equals("BITMAP")) {

            // if the user's fields are all valid
            if (Parser.isValidUserObject(currentUser)) {

                // attempt to create the user account
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successful sign up
                                Log.e("CREATE_USER", "createUserWithEmail:success");

                                // set the user's photo appropriately
                                if (currentPhoto != null) {
                                    FirebaseIntegrity.setUserProfilePictureBitmap(currentUser,
                                            currentPhoto);
                                } else {
                                    docData.put("photo", photo);
                                }

                                // create a Firestore document for the user
                                FirebaseIntegrity.setDocumentFromObject("users",
                                        email, docData);

                                // welcome the user to Pocketbook
                                Toast.makeText(SignUpActivity.this,
                                        String.format(Locale.CANADA,
                                                "Welcome to Pocketbook, %s.",
                                                currentUser.getFirstName()),
                                        Toast.LENGTH_SHORT).show();

                                // go to HomeActivity
                                Intent intent = new Intent(getApplicationContext(),
                                        HomeActivity.class);
                                intent.putExtra("CURRENT_USER", currentUser);
                                startActivity(intent);
                                finish();  // finish the current activity

                            } else {  // if the sign up attempt failed

                                // show an error message and allow the user to sign up again
                                signUpButton.setClickable(true);
                                Toast.makeText(SignUpActivity.this,
                                        "Unsuccessful Sign Up! Try using a " +
                                                "different email address.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        } else if (Parser.isValidUserObject(currentUser)) {  // if the user didn't choose a photo

            // attempt to create the user account
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // Successful sign up
                            Log.e("CREATE_USER", "createUserWithEmail:success");

                            // set the user's photo appropriately
                            if ((currentPhotoPath != null) && (!currentPhotoPath.equals(""))) {
                                FirebaseIntegrity.setUserProfilePicture(currentUser,
                                        currentPhotoPath);
                            } else {
                                docData.put("photo", photo);
                            }

                            // create a Firestore document for the user
                            FirebaseIntegrity.setDocumentFromObject("users",
                                    email, docData);

                            // welcome the user to Pocketbook
                            Toast.makeText(SignUpActivity.this,
                                    String.format(Locale.CANADA,
                                            "Welcome to Pocketbook, %s.",
                                            currentUser.getFirstName()),
                                    Toast.LENGTH_SHORT).show();

                            // go to HomeActivity
                            Intent intent = new Intent(getApplicationContext(),
                                    HomeActivity.class);
                            intent.putExtra("CURRENT_USER", currentUser);
                            startActivity(intent);
                            finish();  // finish the current activity

                        } else {  // if the sign up attempt failed

                            // show an error message and allow the user to sign up again
                            signUpButton.setClickable(true);
                            Toast.makeText(SignUpActivity.this,
                                    "Unsuccessful Sign Up! Try using a " +
                                            "different email address.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {  // if the sign up attempt was invalid

            // show an error message and allow the user to sign up again
            signUpButton.setClickable(true);
            Toast.makeText(SignUpActivity.this,
                    "Unsuccessful Sign Up! Try using a different email address.",
                    Toast.LENGTH_SHORT).show();
        }

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
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
     * Checks if the user has not entered any text or chosen a photo
     * @return true if the user has not changed anything, false otherwise
     */
    private boolean noChanges() {
        return "".equals(Objects.requireNonNull(layoutUserFirstName.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserLastName.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserUsername.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserEmail.getText()).toString())
                && (currentPhotoPath == null)
                ;
    }

    /**
     * Image Option dialog that allows the user
     */
    private void showImageSelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_book_photo, null);

        TextView takePhotoOption = view.findViewById(R.id.takePhotoField);
        TextView choosePhotoOption = view.findViewById(R.id.choosePhotoField);
        TextView showRemovePhotoOption = view.findViewById(R.id.removePhotoField);

        if (showRemovePhoto) {
            showRemovePhotoOption.setVisibility(View.VISIBLE);
        } else {
            showRemovePhotoOption.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        takePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            openCamera();
        });

        choosePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                    LAUNCH_GALLERY_CODE);
        });

        showRemovePhotoOption.setOnClickListener(v -> {
            alertDialog.dismiss();
            GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                    .load(defaultPhoto)
                    .into(layoutProfilePicture);
            currentPhotoPath = "REMOVE";
            showRemovePhoto = false;
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
                Log.e("SIGN_UP_ACTIVITY", ex.toString());
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
            Log.e("SIGN_UP_ACTIVITY", "Failed to resolve activity!");
        }

    }

    /**
     * Create an image file for the images to be stored
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
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
                ImageView myImage = (ImageView) findViewById(R.id.signUpProfilePictureField);
                myImage.setImageBitmap(myBitmap);
                showRemovePhoto = true;
                currentPhoto = null;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("SIGN_UP_ACTIVITY", "Camera failed!");
            }
        } else if (requestCode == LAUNCH_GALLERY_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getBaseContext()
                            .getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    currentPhoto = BitmapFactory.decodeStream(inputStream);
                    currentPhotoPath = "BITMAP";
                    ImageView myImage = (ImageView) findViewById(R.id.signUpProfilePictureField);
                    myImage.setImageBitmap(currentPhoto);
                    showRemovePhoto = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("SIGN_UP_ACTIVITY", "Failed Gallery!");
            }
        }
    }
}