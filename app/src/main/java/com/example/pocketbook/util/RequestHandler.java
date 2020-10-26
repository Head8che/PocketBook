package com.example.pocketbook.util;

import com.example.pocketbook.model.User;

import java.util.ArrayList;

public class RequestHandler {

    /* To use enums : Enum.X */
    enum BookStatus {
        AVAILABLE,
        REQUESTED,
        ACCEPTED,
        BORROWED
    }
    enum RequestStatus {
        ACCEPT,
        REJECT,
        PENDING
    }

    private BookStatus bookState;
    private RequestStatus requestStatus;
    private ArrayList<User> requesters;


    /**
     * Default Constructor to handle Incoming Requests
     * @param bookState : status of the book being requested
     * @param requestStatus : status of request for owner
     * @param requesters : list of users for the requesting for the book
     */
    public RequestHandler(BookStatus bookState, RequestStatus requestStatus, ArrayList<User> requesters) {
        this.bookState = bookState;
        this.requestStatus = requestStatus;
        this.requesters = requesters;
    }
}
