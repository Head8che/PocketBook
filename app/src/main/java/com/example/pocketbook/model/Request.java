package com.example.pocketbook.model;

import java.util.Date;

public class Request {
    private String requester;
    private String requestee;
    private String bookRequested;
    private String requestDate;
    private String requestStatus;

    public Request(String requester, String requestee, String bookRequested, String requestDate) {
        this.requester = requester;
        this.requestee = requestee;
        this.bookRequested = bookRequested;
        this.requestDate = requestDate;
        this.requestStatus = "Pending";
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequestee() {
        return requestee;
    }

    public void setRequestee(String requestee) {
        this.requestee = requestee;
    }

    public String getBookRequested() {
        return bookRequested;
    }

    public void setBookRequested(String bookRequested) {
        this.bookRequested = bookRequested;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }


}
