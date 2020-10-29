package com.example.pocketbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/** Login **/
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button signUp, login, forgotPass;
    private EditText userEmail, userPassword;
    private final String TAG = "MainActivity";
    private User current_user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUp = findViewById(R.id.RegisterBtn);
        login = findViewById(R.id.LoginBtn);
        forgotPass = findViewById(R.id.ForgotPass);
        userEmail = findViewById(R.id.UserReg);
        userPassword = findViewById(R.id.PasswordReg);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                if(email.isEmpty()){
                    userEmail.setError("Need an email!");
                    userEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    userPassword.setError("Password Required");
                    userPassword.requestFocus();
                    return;
                }
                Register(email,password);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    public void Register(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Welcome to Pocketbook.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                    .collection("users").document(Objects.requireNonNull(user.getEmail()));

                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                                            current_user = document.toObject(User.class);
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                            Log.e(TAG, "DocumentSnapshot data: " + current_user.getUsername());

                            System.out.println(current_user.getUsername());

                            new ProfileFragment(current_user);

//                            Toast.makeText(LoginActivity.this, current_user.getUsername(),
//                                    Toast.LENGTH_SHORT).show();

//                            User current_user = User();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Failed to Login.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}