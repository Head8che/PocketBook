package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.example.pocketbook.util.PhotoHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private Boolean validPhoneNumber;
    private Boolean validUserEmail;
    private Boolean validUserPassword;

    User currentUser;
    StorageReference defaultPhoto = FirebaseStorage.getInstance().getReference()
            .child("default_images").child("no_profileImg.png");

    TextInputEditText layoutUserFirstName;
    TextInputEditText layoutUserLastName;
    TextInputEditText layoutUserUsername;
    TextInputEditText layoutUserPhoneNumber;
    TextInputEditText layoutUserEmail;
    TextInputEditText layoutUserPassword;
    ImageView layoutProfilePicture;

    TextInputLayout layoutUserFirstNameContainer;
    TextInputLayout layoutUserLastNameContainer;
    TextInputLayout layoutUserUsernameContainer;
    TextInputLayout layoutUserPhoneNumberContainer;
    TextInputLayout layoutUserEmailContainer;
    TextInputLayout layoutUserPasswordContainer;

    TextView signUpButton;

    PhotoHandler photoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        photoHandler = new PhotoHandler();

        // initialize validation booleans to false
        validFirstName = false;
        validLastName = false;
        validUsername = false;
        validPhoneNumber = true;
        validUserEmail = false;
        validUserPassword = false;

        // Toolbar toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
        ImageView backButton = findViewById(R.id.signUpBackBtn);
        signUpButton = findViewById(R.id.signUpSignUpBtn);
        TextView changePhotoButton = findViewById(R.id.signUpChangePhotoBtn);

        // access the layout text fields
        layoutUserFirstName = findViewById(R.id.signUpFirstNameField);
        layoutUserLastName = findViewById(R.id.signUpLastNameField);
        layoutUserUsername = findViewById(R.id.signUpUsernameField);
        layoutUserPhoneNumber = findViewById(R.id.signUpPhoneNumberField);
        layoutUserEmail = findViewById(R.id.signUpEmailField);
        layoutUserPassword = findViewById(R.id.signUpPasswordField);
        layoutProfilePicture = findViewById(R.id.signUpProfilePictureField);

        // access the layout text containers
        layoutUserFirstNameContainer = findViewById(R.id.signUpFirstNameContainer);
        layoutUserLastNameContainer = findViewById(R.id.signUpLastNameContainer);
        layoutUserUsernameContainer = findViewById(R.id.signUpUsernameContainer);
        layoutUserPhoneNumberContainer = findViewById(R.id.signUpPhoneNumberContainer);
        layoutUserEmailContainer = findViewById(R.id.signUpEmailContainer);
        layoutUserPasswordContainer = findViewById(R.id.signUpPasswordContainer);

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
        changePhotoButton.setOnClickListener(v -> {
            changePhotoButton.setClickable(false);
            (photoHandler).showImageSelectorDialog(this,
                    defaultPhoto, layoutProfilePicture);
            changePhotoButton.setClickable(true);
        });

        // load default photo into ImageLayout
        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(defaultPhoto)
                .into(layoutProfilePicture);

        // go back when backButton is clicked
        backButton.setOnClickListener(v -> {
            backButton.setClickable(false);
            onBackPressed();
            backButton.setClickable(true);
        });

        // when signUpButton is clicked
        signUpButton.setOnClickListener(v -> {
            // user can't create account multiple times with multi-click
            signUpButton.setClickable(false);

            // if all fields are valid
            if (validFirstName && validLastName && validUsername
                    && validUserEmail && validPhoneNumber && validUserPassword) {

                if (!noChanges()) {  // if the user has entered some text or chosen a photo

                    // extract the layout text field values into variables
                    String newFirstName = Objects.requireNonNull(layoutUserFirstName.getText())
                            .toString();
                    String newLastName = Objects.requireNonNull(layoutUserLastName.getText())
                            .toString();
                    String newUsername = Objects.requireNonNull(layoutUserUsername.getText())
                            .toString();
                    String newUserPhoneNumber = Objects.requireNonNull(layoutUserPhoneNumber
                            .getText())
                            .toString();
                    String newUserEmail = Objects.requireNonNull(layoutUserEmail.getText())
                            .toString();
                    String newUserPassword = Objects.requireNonNull(layoutUserPassword.getText())
                            .toString();

                    // get a User variable from the valid inputted fields
                    currentUser = Parser.parseUser(newFirstName, newLastName, newUserEmail,
                            newUsername, newUserPassword, newUserPhoneNumber, "");

                    // attempt to sign the user up
                    signUpUser();

                } else {  // if the user has not made any changes
                    finish();
                    signUpButton.setClickable(true);
                }

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
                } else if (!validUsername) {
                    // set an error and focus the app on the erroneous field
                    layoutUserUsername.setError("Input required");
                    layoutUserUsernameContainer.setErrorEnabled(true);
                    layoutUserUsername.requestFocus();
                } else if (!validPhoneNumber) {
                    // set an error and focus the app on the erroneous field
                    layoutUserPhoneNumber.setError("Invalid Phone Number");
                    layoutUserPhoneNumberContainer.setErrorEnabled(true);
                    layoutUserPhoneNumber.requestFocus();
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
                signUpButton.setClickable(true);
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
        String phoneNumber = currentUser.getPhoneNumber();
        String photo = currentUser.getPhoto();

        // put the user's data into a HashMap object
        HashMap<String, Object> docData = new HashMap<>();
        docData.put("firstName", firstName);
        docData.put("lastName", lastName);
        docData.put("email", email);
        docData.put("username", username);
        docData.put("password", password);
        docData.put("phoneNumber", phoneNumber);
        docData.put("photo", photo);

        // if the user chose a photo from their gallery
        if ((photoHandler.getCurrentPhotoPath() != null)
                && photoHandler.getCurrentPhotoPath().equals("BITMAP")) {

            // if the user's fields are all valid
            if (Parser.isValidUserObject(currentUser)) {

                // attempt to create the user account
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successful sign up
                                Log.e("CREATE_USER", "createUserWithEmail:success");

                                // set the user's photo appropriately
                                if (photoHandler.getGalleryPhoto() != null) {
                                    FirebaseIntegrity.setUserProfilePictureBitmap(currentUser,
                                            photoHandler.getGalleryPhoto());
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

                                // go to OnBoardingActivity
                                Intent intent = new Intent(getApplicationContext(),
                                        OnBoardingActivity.class);
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
                            if ((photoHandler.getCurrentPhotoPath() != null)
                                    && (!photoHandler.getCurrentPhotoPath().equals(""))) {
                                FirebaseIntegrity.setUserProfilePicture(currentUser,
                                        photoHandler.getCurrentPhotoPath());
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

                            // go to OnBoardingActivity
                            Intent intent = new Intent(getApplicationContext(),
                                    OnBoardingActivity.class);
                            intent.putExtra("CURRENT_USER", currentUser);
                            startActivity(intent);
                            finish();  // finish the current activity
                            signUpButton.setClickable(true);

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
        return "".equals(Objects.requireNonNull(layoutUserFirstName.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserLastName.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserUsername.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserPhoneNumber.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserEmail.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserPassword.getText()).toString())
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

        (photoHandler).onActivityResult(this, R.id.signUpProfilePictureField,
                requestCode, resultCode, data);
    }
}
