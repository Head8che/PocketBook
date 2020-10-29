package com.example.pocketbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.R;
import com.example.pocketbook.fragment.NewProfileFragment;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/** Login **/
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button signUp, login, forgotPass;
    private EditText userEmail, userPassword;
    private final String TAG = "MainActivity";
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
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
                    userEmail.setError("Email Required!");
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

                            ///
//                            User currentUserEmail = new User(user.getEmail());
//                            Bundle bundle = new Bundle();
//                            bundle.putString("userEmail", currentUserEmail);

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            String email = user.getEmail();
//                            bundle.putString("userEmail", user.getEmail());
                            //set MyFragment Arguments
//                            NewProfileFragment myObj = new NewProfileFragment();
//                            myObj.setArguments(bundle);

//                            intent.putExtra('username')
                            startActivity(intent);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Failed to Login.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void updateUI(FirebaseUser currentUser) {
        Intent profileIntent = new Intent(getApplicationContext(), HomeActivity.class);
        profileIntent.putExtra("email", currentUser.getEmail());
        Log.v("DATA", currentUser.getUid());
        startActivity(profileIntent);
    }


}