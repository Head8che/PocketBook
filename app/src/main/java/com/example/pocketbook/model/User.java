package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.Serializable;

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
    }

    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getEmail() { return this.email; }

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
    public boolean setFirstName(String firstName) {
        firstName = firstName.trim();
        if (Parser.isValidFirstName(firstName)) {
            this.firstName = firstName;
            return true;
        }
        return false;
    }
    /**
     * sets Lastname
     * @param lastName
     */
    public boolean setLastName(String lastName) {
        lastName = lastName.trim();
        if (Parser.isValidLastName(lastName)) {
            this.lastName = lastName;
            return true;
        }
        return false;
    }
    /**
     * sets Email
     */
    public boolean setEmail(String email) {
        email = email.trim();
        if (Parser.isValidUserEmail(email)) {
            this.email = email;
            return true;
        }
        return false;
    }
    /**
     * sets password
     * @param password
     */
    public boolean setPassword(String password) {
        password = password.trim();
        if (Parser.isValidPassword(password)) {
            this.password = password;
            return true;
        }
        return false;
    }
    /**
     * sets username
     * @param username
     */
    public boolean setUsername(String username) {
        username = username.trim();
        if (Parser.isValidUsername(username)) {
            this.username = username;
            return true;
        }
        return false;
    }


    /*
        TODO: upload new image to FirebaseStorage and overwrite old image
    */
    /**
     * sets photo
     * @param photo
     */
    public boolean setPhoto(String photo) {
        photo = photo.trim();
        if (Parser.isValidUserPhoto(photo)) {
            this.photo = photo;
            return true;
        }
        return false;
    }


}