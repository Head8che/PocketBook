package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.ScrollUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private User currentUser;

    private ImageView userProfile;
    private TextView profileName, userName, userEmail;
    private TextView edidProfilePicture;
    private static final String USER = "users";

    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER", user);
        editProfileFragment.setArguments(args);
        return editProfileFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("BA_USER");
        }
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, true);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        String email = currentUser.getEmail();
        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
        TextView profileName = (TextView) v.findViewById(R.id.userProfileName);
        TextView profileUsername = (TextView) v.findViewById(R.id.userProfileUserName);
        TextView profileEmail = (TextView) v.findViewById(R.id.UserProfileEmail);

        TextView editProfilebtn = (TextView) v.findViewById(R.id.editProfilePicture);

        profileName.setText(first_Name + ' ' + last_Name);
        profileUsername.setText(user_Name);
        profileEmail.setText(email);

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(currentUser.getProfilePicture())
                .circleCrop()
                .into(profilePicture);





        String name = currentUser.getFirstName();


        // return view
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}



//    private FirebaseDatabase database;
//    private DatabaseReference mDatabase;
//    private FirebaseUser user;
//    private FirebaseAuth mAuth;
//    ImageView imgView, cameraView;
//    Uri filePath;
//    ProgressDialog pd;
//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocketbook-t09.appspot.com/profile_pictures");

//    public static EditProfileFragment newInstance(User user) {
//        EditProfileFragment editProfileFragment = new EditProfileFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("PF_USER", user);
//        editProfileFragment.setArguments(args);
//        return editProfileFragment;
//    }
//
//    public EditProfileFragment(){
//        // Empty Constructor
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            this.currentUser = (User) getArguments().getSerializable("PF_USER");
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        String userEmail = mAuth.getCurrentUser().getEmail();
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//        if (container != null) {
//            container.removeAllViews();
//        }
//        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
//        StorageReference userProfilePicture = currentUser.getProfilePicture();
//        String first_Name = currentUser.getFirstName();
//        String last_Name = currentUser.getLastName();
//        String user_Name = currentUser.getUsername();
//        String User_pic = currentUser.getPhoto();
//
//        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
//        TextView ProfileName = (TextView) v.findViewById(R.id.userProfileName);
//        TextView UserName = (TextView) v.findViewById(R.id.userProfileUserName);
//        edidProfilePicture = v.findViewById(R.id.editProfilePicture);
//
//        ProfileName.setText(first_Name + ' ' + last_Name);
//        UserName.setText(user_Name);
////
////        GlideApp.with(Objects.requireNonNull(getContext()))
////                .load(userProfilePicture)
////                .circleCrop()
////                .into(profilePicture);
//
//        edidProfilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditProfileFragment nextFrag = new EditProfileFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container,nextFrag).commit();
//            }
//        });
//        return v;
//    }
//
//}
//
////
////
////        FirstName = v.findViewById(R.id.profileName);
////        Email =  v.findViewById(R.id.UserProfileEmail);
////        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
////
////        addPicture = (TextView) v.findViewById(R.id.AddProfilePicture);
////        Username =  v.findViewById(R.id.userProfileUserName);
//////        cancelBtn = v.findViewById(R.id.cancelBtn);
//////        doneBtn =  v.findViewById(R.id.doneBtn);
////
////        database = FirebaseDatabase.getInstance();
////        mDatabase = database.getReference(USER);
////        mAuth = FirebaseAuth.getInstance();
////
////        pd = new ProgressDialog(getActivity());
////        pd.setMessage("Uploading....");
////
////        addPicture.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                selectImage(getActivity());
////            }
////        });
////
////        GlideApp.with(Objects.requireNonNull(getContext()))
////                .load(userProfilePicture)
////                .circleCrop()
////                .into(profilePicture);
////
////        doneBtn.setOnClickListener(new View.OnClickListener(){
////            @Override
////            public void onClick(View v){
////
////                FirstName.setText(first_Name);
////                LastName.setText(last_Name);
////                Email.setText(email);
////                Username.setText(username);
////
////
////                if(first_Name.isEmpty()){
////                    FirstName.setError("First Name Required!");
////                    FirstName.requestFocus();
////                    return;
////                }
////
////                if(last_Name.isEmpty()){
////                    LastName.setError("Last Name Required!");
////                    LastName.requestFocus();
////                    return;
////                }
////
////                if(username.isEmpty()){
////                    Username.setError("User Name Required!");
////                    Username.requestFocus();
////                    return;
////                }
////
////                if(email.isEmpty()){
////                    Email.setError("Need an email!");
////                    Email.requestFocus();
////                    return;
////                }
////
////                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
////                    Email.setError("Enter a valid email!.");
////                    Email.requestFocus();
////                    return;
////
////                }
////                currentUser = new User(first_Name,last_Name, username,email,password, null);
////                UpdateInfo(email,password);
////                mFirestore = FirebaseFirestore.getInstance();
////                mFirestore = FirebaseFirestore.getInstance();
////                mFirestore.collection("users").document(email).update(first_Name,"David");
////
////            }
////
////        });
////
////        return v;
////    }
////
////    /**
////     * EditProfileFragment UpdateInfo
////     * @param email
////     * @param password
////     */
////    private void UpdateInfo(String email, String password) {
////        AuthCredential credential = EmailAuthProvider.getCredential(email,password);
////        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>(){
////            @Override
////            public void onComplete(@NonNull Task<Void> task) {
////                if (task.isSuccessful()){
////                    String updatedEmail = Email.getText().toString();
////                    Log.d(currentUser.getEmail(),"EDIT Activity");
////                    Log.d(updatedEmail,"EDIT Activity");
//////                    user.getEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
//////                        @Override
//////                        public void onComplete(@NonNull Task<Void> task) {
//////
//////                        }
//////                    });
////
////                }
////            }
////
////        });
////    }
////
////    /**
////     * EditProfileFragment boolean uploadImage Method
////     * @return
////     */
////    public boolean uploadImage(){
////        if(filePath != null) {
////            pd.show();
////            StorageReference childRef = storageRef.child(username+".jpg");
////            //uploading the image
////            UploadTask uploadTask = childRef.putFile(filePath);
////            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                @Override
////                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                    pd.dismiss();
////                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
////                }
////            }).addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception e) {
////                    pd.dismiss();
////                    Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
////                }
////            });
////            return true;
////        }
////        else {
////            return false;
////        }
////
////    }
////
////    /**
////     * EditProfileFragment openCamera
////     */
////    private void openCamera() {
////        ContentValues values = new ContentValues();
////        values.put(MediaStore.Images.Media.TITLE, "New Profile Picture");
////        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
////        filePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
////
////        //Camera intent
////        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, (Parcelable) filePath);
////        startActivityForResult(cameraIntent,0);
////
////    }
////
////    /**
////     * EditProfileFragment SelectImage method
////     * @param context
////     */
////    private void selectImage(Context context) {
////        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
////
////        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////        builder.setTitle("Choose your profile picture");
////
////        builder.setItems(options, new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialog, int item) {
////
////                if (options[item].equals("Take Photo")) {
////                    openCamera();
////                } else if (options[item].equals("Choose from Gallery")) {
////                    Intent intent = new Intent();
////                    intent.setType("image/*");
////                    intent.setAction(Intent.ACTION_PICK);
////                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
////
////                } else if (options[item].equals("Cancel")) {
////                    dialog.dismiss();
////                }
////            }
////        });
////        builder.show();
////    }
////}
////
