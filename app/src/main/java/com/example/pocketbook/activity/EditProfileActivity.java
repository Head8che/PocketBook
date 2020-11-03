package com.example.pocketbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView userProfile;
    private TextView profileName, userName, userEmail;
    private TextView editProfilePicture;
    private static final String USER = "users";
    private User current_user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileName = findViewById(R.id.userProfileName);
        userName = findViewById(R.id.userProfileUserName);
        userEmail = findViewById(R.id.UserProfileEmail);
        userProfile = findViewById(R.id.profile_image);

        Intent intent = getIntent();
        current_user = (User) intent.getSerializableExtra("currentUser");


        String first_Name = current_user.getFirstName();
        String last_Name = current_user.getLastName();
        profileName.setText(first_Name + ' ' + last_Name);
        userName.setText(current_user.getUsername());
        userEmail.setText(current_user.getEmail());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://pocketbook-t09.appspot.com/profile_pictures");
        StorageReference userProfilePicture = current_user.getProfilePicture();

        GlideApp.with(this)
                .load(userProfilePicture)
                .circleCrop()
                .into(userProfile);
    }



}
//    private FirebaseFirestore mFirestore;
//    private FirebaseAuth mAuth;
//    private User currentUser;
//
//    private ImageView userProfile;
//    private TextView profileName, userName, userEmail;
//    private TextView edidProfilePicture;
//    private static final String USER = "users";
//
//    public static EditProfileFragment newInstance(User user) {
//        EditProfileFragment editProfileFragment = new EditProfileFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("USER", user);
//        editProfileFragment.setArguments(args);
//        return editProfileFragment;
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            this.currentUser = (User) getArguments().getSerializable("BA_USER");
//        }
//        mFirestore = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (container != null) {
//            container.removeAllViews();
//        }
//        View v = inflater.inflate(R.layout.fragment_edit_profile, container, true);
//        mFirestore = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        String first_Name = currentUser.getFirstName();
//        String last_Name = currentUser.getLastName();
//        String user_Name = currentUser.getUsername();
//        String email = currentUser.getEmail();
//        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
//        TextView profileName = (TextView) v.findViewById(R.id.userProfileName);
//        TextView profileUsername = (TextView) v.findViewById(R.id.userProfileUserName);
//        TextView profileEmail = (TextView) v.findViewById(R.id.UserProfileEmail);
//
//        TextView editProfilebtn = (TextView) v.findViewById(R.id.editProfilePicture);
//
//        profileName.setText(first_Name + ' ' + last_Name);
//        profileUsername.setText(user_Name);
//        profileEmail.setText(email);
//
//        GlideApp.with(Objects.requireNonNull(getContext()))
//                .load(currentUser.getProfilePicture())
//                .circleCrop()
//                .into(profilePicture);
//
//
//
//
//
//        String name = currentUser.getFirstName();
//
//
//        // return view
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }
//
//
//}
