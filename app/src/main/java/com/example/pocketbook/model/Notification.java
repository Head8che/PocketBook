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
    private String seen;  // can only be "true" or "false"
    private String type;
    private String notificationDate;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /* Default Constructor */
    public Notification(String message, String sender, String receiver, String relatedBook,
                        String seen, String type){
        this.message = message.trim();
        this.sender = sender.trim().toLowerCase();  // lowercase email
        this.receiver = receiver.trim().toLowerCase();  // lowercase email
        this.relatedBook = relatedBook.trim();
        this.seen = seen.trim().toLowerCase();  // lowercase for "true" or "false"
        this.type = type.trim().toUpperCase();  /* one of ["BOOK_REQUESTED", "REQUEST_ACCEPTED",
                                                           "REQUEST_DECLINED", "RETURN_REQUESTED",
                                                           "LOCATION_SPECIFIED"] */
        this.notificationDate = LocalDateTime.now().format(formatter).trim();
    }

    public String getMessage() { return this.message; }
    public String getSender() {  return this.sender; }
    public String getReceiver() {  return this.receiver; }
    public String getRelatedBook() { return this.relatedBook; }
    public String getSeen() { return this.seen; }
    public String getType() { return this.type; }
    public String getNotificationDate() {  return this.notificationDate;  }

    /**
     * Adds a notification to Firebase
     */
    public void pushNewNotificationToFirebase() {

        Map<String, Object> docData = new HashMap<>();
        docData.put("message", this.message);
        docData.put("sender", this.sender);
        docData.put("receiver", this.receiver);
        docData.put("relatedBook", this.relatedBook);
        docData.put("seen", this.seen);
        docData.put("type", this.type);
        docData.put("notificationDate", this.notificationDate);

        FirebaseFirestore.getInstance().collection("users").document(receiver)
                .collection("notifications").document(this.notificationDate)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW_NOTIFICATION", "Notification data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NEW_NOTIFICATION", "Error writing notification data!", e);
                    }
                });

    }

}