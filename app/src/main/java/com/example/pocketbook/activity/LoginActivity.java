package com.example.pocketbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

/**
 * A login screen that allows users to login using (email/password)
 */
public class LoginActivity extends AppCompatActivity {
    private User currentUser = new User();

    private Boolean validUserEmail;
    private Boolean validUserPassword;

    TextInputEditText layoutUserEmail;
    TextInputEditText layoutUserPassword;

    TextInputLayout layoutUserEmailContainer;
    TextInputLayout layoutUserPasswordContainer;

    Button loginButton;
    Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize validation booleans to false
        validUserEmail = false;
        validUserPassword = false;

        // access the buttons
        loginButton = (Button) findViewById(R.id.loginLoginBtn);
        signUpButton = (Button) findViewById(R.id.loginSignUpBtn);

        // access the layout text fields
        layoutUserEmail = (TextInputEditText) findViewById(R.id.loginEmailField);
        layoutUserPassword = (TextInputEditText) findViewById(R.id.loginPasswordField);

        // access the layout text containers
        layoutUserEmailContainer = (TextInputLayout) findViewById(R.id.loginEmailContainer);
        layoutUserPasswordContainer = (TextInputLayout) findViewById(R.id.loginPasswordContainer);

        // add a text field listener that validates the inputted text
        layoutUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidUserEmail(s.toString()))) {
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

        // go to SignUpActivity when signUpButton is clicked
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        // when loginButton is clicked
        loginButton.setOnClickListener(v -> {

            // if all fields are valid
            if (validUserEmail && validUserPassword) {

                if (!noChanges()) {  // if the user has entered some text or chosen a photo

                    // extract the layout text field values into variables
                    String userEmail = Objects.requireNonNull(layoutUserEmail.getText())
                            .toString();
                    String userPassword = Objects.requireNonNull(layoutUserPassword.getText())
                            .toString();

                    // user can't try to login multiple times with multi-click
                    loginButton.setClickable(false);
                    signUpButton.setClickable(false);

                    // attempt to sign the user up
                    Register(userEmail, userPassword);
                }
            } else {  // if not all fields are valid
                if (!validUserEmail) {
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
     * Checks if the user has not entered any text or chosen a photo
     * @return true if the user has not changed anything, false otherwise
     */
    private boolean noChanges() {
        return "".equals(Objects.requireNonNull(layoutUserEmail.getText()).toString())
                && "".equals(Objects.requireNonNull(layoutUserPassword.getText()).toString());
    }

    /**
     * Allows the user to sign in using their username and password
     * @param userEmail user email
     * @param userPassword user password
     */
    public void Register(String userEmail, String userPassword){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d("LOGIN_ACTIVITY", "signInWithEmail:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        DocumentReference docRef = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(Objects.requireNonNull(user.getEmail()));

                        docRef.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task1.getResult();

                                // if the user exists in the database
                                if ((document != null) && (document.exists())) {

                                    // create a User object from the user data
                                    currentUser = FirebaseIntegrity
                                            .getUserFromFirestore(document);
                                    Log.e("LOGIN_ACTIVITY",
                                            "Login user data: " + document.getData());

                                    // welcome the user back to Pocketbook
                                    Toast.makeText(LoginActivity.this,
                                            String.format(Locale.CANADA,
                                            "Welcome back, %s.",
                                                    currentUser.getFirstName()),
                                            Toast.LENGTH_SHORT).show();

                                    // go to HomeActivity
                                    Intent intent = new Intent(getApplicationContext(),
                                            HomeActivity.class);
                                    intent.putExtra("CURRENT_USER", currentUser);
                                    startActivity(intent);
                                    finish();  // finish the current activity

                                } else {  // if the user does not exist in the database
                                    Log.d("LOGIN_ACTIVITY", "No such document");
                                }
                            } else {  // if getting the user document failed
                                Log.d("LOGIN_ACTIVITY",
                                        "get failed with ", task1.getException());
                            }
                        });

                    } else {
                        // if sign in fails, display a message to the user
                        Log.w("LOGIN_ACTIVITY",
                                "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Failed to Login.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
