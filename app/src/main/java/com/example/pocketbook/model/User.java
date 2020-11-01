package com.example.pocketbook.model;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String photo;
    public User() {} // used by firestore to automatically create new object



    public User(String firstName, String lastName, String email, String username, String password, String photo) {
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        this.username = username.trim();
        this.password = password.trim();
        this.photo = photo;
    }

    public User(String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        this.username = username.trim();
        this.password = password.trim();
    }

    public User(String firstName, String lastName, String email, String username) {
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        this.username = username.trim();
        this.password = password.trim();
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public StorageReference getProfilePicture() {
        if (this.photo == null || this.photo.equals("") || !this.photo.endsWith(".jpg")) {
            return FirebaseStorage.getInstance().getReference()
                    .child("default_images").child("no_profileImg.png"); }
        return FirebaseStorage.getInstance().getReference().child("profile_pictures").child(this.photo);
    }
}