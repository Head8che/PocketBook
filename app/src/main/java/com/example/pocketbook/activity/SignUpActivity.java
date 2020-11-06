package com.example.pocketbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A Sign up screen that allows users to sign up with there credentials(Email/Password/First name/Last name and username)
 */
public class SignUpActivity extends AppCompatActivity {

    EditText FirstName, LastName, Email, Username, Password;
    String firstName, lastName, email, username, password, profileImageUrl;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private static final String USER = "users";
    private static final String TAG = "SignUpActivity";
    private User user;
    private Button registerConfirm, addPicture;

    ImageView imgView, cameraView;
    Uri filePath;
    ProgressDialog pd;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocketbook-t09.appspot.com/profile_pictures");



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        FirstName = findViewById(R.id.firstName);
        LastName = findViewById(R.id.lastName);
        Email = findViewById(R.id.EmailReg);
        Username = findViewById(R.id.userName);
        Password = findViewById(R.id.PasswordReg);
        registerConfirm = findViewById(R.id.RegisterConfirm);
        imgView = findViewById(R.id.userImage);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        addPicture = findViewById(R.id.AddProfilePicture);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");


        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(SignUpActivity.this);
            }
        });


        registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = FirstName.getText().toString().trim();
                lastName = LastName.getText().toString().trim();
                email = Email.getText().toString().trim().toLowerCase();
                username = Username.getText().toString().trim();
                password = Password.getText().toString().trim();

                if(firstName.isEmpty()){
                    FirstName.setError("First Name Required!");
                    FirstName.requestFocus();
                    return;
                }

                if(lastName.isEmpty()){
                    LastName.setError("Last Name Required!");
                    LastName.requestFocus();
                    return;
                }

                if(username.isEmpty()){
                    Username.setError("User Name Required!");
                    Username.requestFocus();
                    return;
                }

                if(email.isEmpty()){
                    Email.setError("Need an email!");
                    Email.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Email.setError("Enter a valid email!.");
                    Email.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    Password.setError("Password Required");
                    Password.requestFocus();
                    return;
                }

                if(password.length() < 6){
                    Password.setError("Password needs to be of length greater than 6.");
                    Password.requestFocus();
                    return;
                }

                user = new User(firstName,lastName,email,username,password, null);
                Register(email,password,firstName, lastName, username);
            }
        });

    }
    /**
     * registers the user with the provided credentials
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     * @param username
     */
    public void Register(String email, String password, String firstName, String lastName, String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            boolean successfulUpload = uploadImage();
                            if(successfulUpload) {
                                Toast.makeText(SignUpActivity.this, "Account Created Successfully!",
                                        Toast.LENGTH_SHORT).show();
                                profileImageUrl = username+".jpg";
                                user = new User(firstName,lastName,email,username,password,profileImageUrl);
                                user.setNewUserFirebase();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else{
                                Toast.makeText(SignUpActivity.this, "Account Created Successfully!",
                                        Toast.LENGTH_SHORT).show();
                                user = new User(firstName,lastName,email,username,password, null);
                                user.setNewUserFirebase();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "User Already Exists with the email",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }
    /**
     * Updates the user interface once they have successfully signed up.
     * @param currentUser
     */
    public void updateUI(FirebaseUser currentUser){
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
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

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * uploads the image of the user to firebase with a unique id
     * @return
     */
    public boolean uploadImage(){
        if(filePath != null) {
            pd.show();

            StorageReference childRef = storageRef.child(username+".jpg");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(SignUpActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(SignUpActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Allows the camera to be initiated upon request from the user
     */
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        filePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        startActivityForResult(cameraIntent,0);

    }

    /**
     * Allows the user to select method type of image (Take Image/ Choose from Gallery)
     * @param context
     */
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}