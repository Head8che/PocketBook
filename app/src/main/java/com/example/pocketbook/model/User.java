package com.example.pocketbook.model;

public class User {
    private String userId;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String photo;

    public User() {} // used by firestore to automatically create new object

    public User(String userId) { this.userId = userId; }

    public User(String userId, String username, String first_name, String last_name,
                String email, String photo) {
        this.userId = userId;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email ;
        this.photo = photo;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoto(String photo) { this.photo = photo; }
}
