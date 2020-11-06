package com.example.pocketbook.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Request implements Serializable {
    private String requester;
    private String requestee;
    private String requestedBook;
    private String requestDate;
    private Book requestedBookObject;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    /* Empty constructor */
    public Request(){
    }

    /* Constructor without Book object */
    public Request(String requester, String requestee, String requestedBook) {
        this.requester = requester.trim().toLowerCase();  // lowercase email
        this.requestee = requestee.trim().toLowerCase();  // lowercase email
        this.requestedBook = requestedBook.trim();
        this.requestDate = LocalDateTime.now().format(formatter).trim();
        this.requestedBookObject = null;
    }

    /* Constructor with a Book object */
    public Request(String requester, String requestee, Book requestedBookObject) {
        this.requester = requester.trim().toLowerCase();  // lowercase email
        this.requestee = requestee.trim().toLowerCase();  // lowercase email
        this.requestedBook = requestedBookObject.getId().trim();
        this.requestedBookObject = requestedBookObject;
        this.requestDate = LocalDateTime.now().format(formatter).trim();
    }

    /* Getter methods */
    public String getRequester() { return this.requester; }
    public String getRequestee() { return this.requestee; }
    public String getRequestedBook() { return this.requestedBook; }
    public Book getRequestedBookObject() { return this.requestedBookObject; }
    public String getRequestDate() { return this.requestDate; }

}
