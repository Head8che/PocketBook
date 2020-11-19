package com.example.pocketbook.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/*
    TODO: make pushNewNotificationToFirebase() to addNotificationToListFirebase() in notificationList
    TODO: update code below, if necessary, once notificationList is done
 */

public class Notification implements Serializable {
    private String message;
    private String sender;
    private String receiver;
    private String relatedBook;
    private boolean seen;  // can only be "true" or "false"
    private String type;
    private String notificationDate;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    public Notification(){

    }
    /* Default Constructor */
    public Notification(String message, String sender, String receiver, String relatedBook,
                        boolean seen, String type){
        this.message = message.trim();
        this.sender = sender.trim().toLowerCase();  // lowercase email
        this.receiver = receiver.trim().toLowerCase();  // lowercase email
        this.relatedBook = relatedBook.trim();
        this.seen = seen;
        this.type = type.trim().toUpperCase();  /* one of ["BOOK_REQUESTED", "REQUEST_ACCEPTED",
                                                           "REQUEST_DECLINED", "RETURN_REQUESTED",
                                                           "LOCATION_SPECIFIED"] */
        this.notificationDate = LocalDateTime.now().format(formatter).trim();
    }

    public String getMessage() { return this.message; }
    public String getSender() {  return this.sender; }
    public String getReceiver() {  return this.receiver; }
    public String getRelatedBook() { return this.relatedBook; }
    public boolean getSeen() { return this.seen; }
    public String getType() { return this.type; }
    public String getNotificationDate() { return this.notificationDate;  }

    // TODO: seen should have a setter

}