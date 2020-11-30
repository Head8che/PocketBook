package com.example.pocketbook.notifications;

public class Token {

    private String token; // A Token is a unique id for each device

    /**
     * Constructor for the Token class
     * @param token the token of the user as String
     */
    public Token(String token){
        this.token = token;
    }

    /**
     * empty constructor for the Token class
     */
    public Token() {
    }

    /**
     * getter method for the Token String
     * @return Token as String
     */
    public String getToken() {
        return token;
    }

    /**
     * setter method for the Token String
     * @param token the token for the user
     */
    public void setToken(String token) {
        this.token = token;
    }
}
