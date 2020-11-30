package com.example.pocketbook.model;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

    /**
     * Notification Constructor
     * @param message the message of the notification
     * @param sender the sender of the notification
     * @param receiver the sender of the notification
     * @param relatedBook the book related to the notification
     * @param seen a boolean whether the notification has been seen for the user or not
     * @param type the type of the notification
     */
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

    /**
     * getter method for message
     * @return message as String
     */
    public String getMessage() { return this.message; }

    /**
     * getter method for sender
     * @return sender as String
     */
    public String getSender() {  return this.sender; }

    /**
     * getter method for receiver
     * @return receiver as String
     */
    public String getReceiver() {  return this.receiver; }

    /**
     * getter method fro relatedBook
     * @return relatedBook as String
     */
    public String getRelatedBook() { return this.relatedBook; }

    /**
     * getter method for seen
     * @return seen as boolean
     */
    public boolean getSeen() { return this.seen; }

    /**
     * getter method for type
     * @return type as String
     */
    public String getType() { return this.type; }

    /**
     * getter method for notificationDate
     * @return notificationDate as String
     */
    public String getNotificationDate() { return this.notificationDate;  }

}