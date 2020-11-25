package com.example.pocketbook.notifications;

public class Token {
    // A Token is a unique id for each device
    private String token;

    public Token(String token){
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
