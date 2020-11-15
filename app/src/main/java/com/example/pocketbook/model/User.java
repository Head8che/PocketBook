package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String photo;

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public User() {}

    /**
     *  User constructor
     * @param firstName user first name
     * @param lastName user last name
     * @param email user email
     * @param username user username
     * @param password user password
     * @param phoneNumber user phone number
     * @param photo user photo i.e. profile picture
     */
    public User(String firstName, String lastName, String email, String username,
                String password, String phoneNumber, String photo) {

        // if non-optional fields are not null
        if ((firstName != null) && (lastName != null)
                && (email != null) && (username != null) && (password != null)) {

            // trim all values
            firstName = firstName.trim();
            lastName = lastName.trim();
            email = email.trim().toLowerCase();  // lowercase email
            username = username.trim();
            password = password.trim();
            phoneNumber = (phoneNumber == null) ? "" : phoneNumber.trim();  // replace null
            photo = (photo == null) ? "" : photo.trim();  // replace null with empty string

            // only sets User data if the data is valid
            if (Parser.isValidUserData(firstName, lastName,
                    email, username, password, phoneNumber, photo)) {

                this.firstName = firstName.trim();
                this.lastName = lastName.trim();
                this.email = email.trim();
                this.username = username.trim();
                this.password = password.trim();
                this.phoneNumber = phoneNumber.trim();
                this.photo = photo.trim();
            }
        }
    }

    /**
     * Getter method for firstName
     * @return firstName as String
     */
    public String getFirstName() { return this.firstName; }

    /**
     * Getter method for lastName
     * @return lastName as String
     */
    public String getLastName() { return this.lastName; }

    /**
     * Getter method for email
     * @return email as String
     */
    public String getEmail() { return this.email; }

    /**
     * Getter method for username
     * @return username as String
     */
    public String getUsername() { return this.username; }

    /**
     * Getter method for password
     * @return password as String
     */
    public String getPassword() { return this.password; }

    /**
     * Getter method for phoneNumber
     * @return phoneNumber as String
     */
    public String getPhoneNumber() { return this.phoneNumber; }

    /**
     * Getter method for photo
     * @return photo as String
     */
    public String getPhoto() { return this.photo; }

}
