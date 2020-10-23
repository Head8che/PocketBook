package com.example.pocketbook.model;

public class Notification {
    private String id;
    private String message;
    private String sender;
    private String related_book;
    private Boolean seen;
    private String type;
    private String date;

    public Notification(String id, String message, String sender, String rel_book_id,
                        Boolean seen, String type, String date){
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.related_book = rel_book_id;
        this.seen = seen;
        this.type = type;
        this.date = date;
    }

    public String getId() { return id;}
    public String getMessage() { return message; }
    public String getSender() {  return sender; }
    public String getRelated_book() { return related_book; }
    public Boolean getSeen() { return seen; }
    public String getType() { return type; }
    public String getDate() {  return date;  }

    public void setId(String id) { this.id = id; }
    public void setMessage(String message) { this.message = message;}
    public void setSender(String sender) { this.sender = sender; }
    public void setRelated_book(String related_book) { this.related_book = related_book; }
    public void setSeen(Boolean seen) { this.seen = seen; }
    public void setType(String type) {  this.type = type; }
    public void setDate(String date) { this.date = date; }

}
