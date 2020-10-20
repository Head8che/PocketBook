package com.example.pocketbook;

import java.util.ArrayList;

public class RequestHandler {

    private String bookState;
    private String requestStatus;
    private ArrayList<User> requesters;

    public RequestHandler(String bookState, String requestStatus, ArrayList<User> requesters) {
        this.bookState = bookState;
        this.requestStatus = requestStatus;
        this.requesters = requesters;
    }
}
