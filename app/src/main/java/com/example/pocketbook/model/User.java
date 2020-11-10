package com.example.pocketbook.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    TODO: handle setPhoto() & uploading image to FirebaseStorage and overwriting old image;
          will likely be similar to how SignUpActivity sets images.
*/

/**
 * User model class that contains getters and setters of user information
 */
public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String photo;
//    private NotificationList notificationList;

    /**
     * Empty User Constructor for the firestore to automatically create new objects
     */
    public User() {} // used by firestore to automatically create new object

    /**
     *  User constructor that contains the firstname/lastname/email/username/password/photo values
     *  ownedbook/borrowedbook/acceptedboook/request books array list
     * @param firstName
     * @param lastName
     * @param email
     * @param username
     * @param password
     * @param photo
     */
    public User(String firstName, String lastName, String email, String username, String password, String photo) {
        this.firstName = (firstName == null) ? "" : firstName.trim();
        this.lastName = (lastName == null) ? "" : lastName.trim();
        this.email = (email == null) ? "" : email.trim();
        this.username = (username == null) ? "" : username.trim();
        this.password = (password == null) ? "" : password.trim();
        this.photo = (photo == null) ? "" : photo.trim();

//        this.notificationList = new NotificationList();
    }

    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getEmail() { return this.email; }

//    public NotificationList getNotificationList() {
//        return notificationList;
//    }

    /**
     * gets username
     * @return
     */
    public String getUsername() { return this.username; }
    /**
     * gets password
     * @return
     */
    public String getPassword() { return this.password; }
    /**
     * gets photo
     * @return
     */
    public String getPhoto() { return this.photo; }

    /**
     * returns default photo for no uploaded image for user
     * @return
     */
    public StorageReference getProfilePicture() {
        if (this.photo == null || this.photo.equals("") || !(this.photo.endsWith(".jpg"))) {
            return FirebaseStorage.getInstance().getReference()
                    .child("default_images").child("no_profileImg.png"); }
        return FirebaseStorage.getInstance().getReference().child("profile_pictures").child(this.photo);
    }

    /**
     * sets Firstname
     * @param firstName
     */
    public void setFirstName(String firstName) {
        setFirstNameLocal(firstName);
        setFirstNameFirebase(firstName);
    }
    /**
     * sets Lastname
     * @param lastName
     */
    public void setLastName(String lastName) {
        setLastNameLocal(lastName);
        setLastNameFirebase(lastName);
    }
    /**
     * sets Email
     */
    public void setEmail(String email) {
        setEmailLocal(email);
        setEmailFirebase(email);
    }
    /**
     * sets password
     * @param password
     */
    public void setPassword(String password) {
        setPasswordLocal(password);
        setPasswordFirebase(password);
    }
    /**
     * sets username
     * @param username
     */
    public void setUsername(String username) {
        setUsernameLocal(username);
        setUsernameFirebase(username);
    }

    /*
        TODO: upload new image to FirebaseStorage and overwrite old image
    */
    /**
     * sets photo
     * @param photo
     */
    public void setPhoto(String photo) {
        this.photo = ((photo == null) || (photo.trim().equals("")))
                ? null : photo.trim();
    }

    public void setFirstNameLocal(String firstName) { this.firstName = firstName; }
    public void setLastNameLocal(String lastName) { this.lastName = lastName; }
    public void setEmailLocal(String email) { this.email = email; }
    public void setUsernameLocal(String username) { this.username = username; }
    public void setPasswordLocal(String password) { this.password = password; }

    public void setEmailFirebase(String title) { setUserDataFirebase("email", email); }
    public void setFirstNameFirebase(String title) { setUserDataFirebase("firstName", firstName); }
    public void setLastNameFirebase(String title) { setUserDataFirebase("lastName", lastName); }
    public void setUsernameFirebase(String title) { setUserDataFirebase("username", username); }
    public void setPasswordFirebase(String title) { setUserDataFirebase("password", password); }


    public void setUserDataFirebase(String userFieldName, String userFieldValue) {
        FirebaseFirestore.getInstance().collection("users").document(this.email)
                .update(userFieldName, userFieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SET_USER", "User data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_USER", "Error writing user data!", e);
                    }
                });
    }

    /**
     * sets updated user information into the Firebase*
     */
    public void setNewUserFirebase() {
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(this.email);

        Map<String, Object> docData = new HashMap<>();
        docData.put("firstName", this.firstName);
        docData.put("lastName", this.lastName);
        docData.put("email", this.email);
        docData.put("username", this.username);
        docData.put("password", this.password);
        docData.put("photo", (photo == null) ? "" : this.photo);

        userDoc.set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW_USER", "User data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NEW_USER", "Error writing user data!", e);
                    }
                });
    }


}