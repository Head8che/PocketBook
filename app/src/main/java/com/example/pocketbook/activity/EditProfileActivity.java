package com.example.pocketbook.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * User Edit Profile
 * Allows user to update (First name/ Last name)
 * User inputs are validated
 */
public class EditProfileActivity extends AppCompatActivity {
    private EditText firstName, lastName, userName, userEmail;
    String first_name, last_name, user_name, email, profileImg;
    private ImageView userProfileImg, backButton;
    private TextView changeProfilePicture, saveButton;
    private static final String USER = "users";
    private static final String TAG = "EditProfileActivity";
    private User current_user = new User();
    Uri filePath;
    private User user;
    ProgressDialog pd;


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
        firstName = (EditText) findViewById(R.id.userProfileFirstName);
        lastName =  (EditText) findViewById(R.id.userProfileLastName);
        userName = (EditText)findViewById(R.id.userProfileUserName);
        userEmail = (EditText) findViewById(R.id.userProfileEmail);
        userProfileImg = findViewById(R.id.profile_image);
        changeProfilePicture = findViewById(R.id.editProfilePicture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        backButton = (ImageView) findViewById(R.id.editProfileBackBtn);
        saveButton = (TextView) findViewById(R.id.editProfileSaveBtn);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        current_user = (User) intent.getSerializableExtra("currentUser");

        Log.w("Passed",current_user.getFirstName());
        String first_Name = current_user.getFirstName();
        String last_Name = current_user.getLastName();
        firstName.setText(first_Name);
        lastName.setText(last_Name);
        userName.setText(current_user.getUsername());
        userEmail.setText(current_user.getEmail());

        StorageReference userProfilePicture = FirebaseIntegrity.getProfilePicture(current_user);

        GlideApp.with(this)
                .load(userProfilePicture)
                .circleCrop()
                .into(userProfileImg);

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(EditProfileActivity.this);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                first_name = firstName.getText().toString().trim();
                last_name = lastName.getText().toString().trim();
                user_name = userName.getText().toString().trim();
                email =  userEmail.getText().toString().trim();

                if(first_name.isEmpty()){
                    firstName.setError("First Name Required!");
                    firstName.requestFocus();
                    return;
                }

                if(last_name.isEmpty()){
                    lastName.setError("Last Name Required!");
                    lastName.requestFocus();
                    return;
                }

                if(user_name.isEmpty()){
                    userName.setError("User Name Required!");
                    userName.requestFocus();
                    return;
                }

                user = new User(first_name, last_name, user_name, null, null, null);
                current_user.setFirstName(first_name);
                // TODO: add Firebase
                current_user.setLastName(last_name);
                // TODO: add Firebase
                current_user.setUsername(user_name);
                // TODO: add Firebase
                Log.d("Current User", current_user.getFirstName());

                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("users").document(Objects.requireNonNull(current_user.getEmail()));

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                current_user = FirebaseIntegrity.getUserFromFirestore(document);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        }
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("users").document(Objects.requireNonNull(current_user.getEmail()));

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                current_user = FirebaseIntegrity.getUserFromFirestore(document);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        }
                    }
                });
            }
        });
    }

    private void updateUI(User current_user) {
        String KeyID = mDatabase.push().getKey();
        mDatabase.child(KeyID).setValue(current_user);
    }

    /**
     * uploads new user image
     * @return
     */
    public boolean uploadImage(){
        if(filePath != null) {
            pd.show();
            StorageReference childRef = storageRef.child(userName+".jpg");
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
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
    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
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
        final CharSequence[] options = {"Take Photo","Choose Photo","Remove Current Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    openCamera();
                } else if (options[item].equals("Choose Photo")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
                } else if (options[item].equals("Remove Current Photo")){
                    //need to be completed
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
//                userProfile.setImageBitmap(bitmap);
                GlideApp.with(this)
                        .load(bitmap)
                        .circleCrop()
                        .into(userProfileImg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

