package com.example.pocketbook.util;

import com.example.pocketbook.model.User;

import java.util.ArrayList;

public class RequestHandler {

    private ArrayList<User> requesters;
    private String[] bookStatuses;
    private String status;


    /**
     * Default Constructor to handle Incoming Requests
     * @param status : status of book
     * @param requesters : list of users for the requesting for the book
     */
    public RequestHandler(String status, ArrayList<User> requesters) {
        this.bookStatuses = new String[]
                {
                        "AVAILABLE",
                        "REQUESTED",
                        "BORROWED",
                        "ACCEPTED"
                };
        this.status = status;
        this.requesters = requesters;
    }
}