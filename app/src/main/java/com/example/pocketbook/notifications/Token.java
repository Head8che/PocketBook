package com.example.pocketbook.notifications;

public class Token {
    //A Token is unique id for each device, changes everytime the app starts
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
