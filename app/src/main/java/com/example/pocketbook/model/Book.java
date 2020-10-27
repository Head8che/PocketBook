package com.example.pocketbook.model;

public class Book {
    private String title;
    private String author;
    private String ISBN;
    private String owner;
    private String comment;
    private String status;
    private String photo;

    public Book() {} // used by firestore to populate a book

    public Book(String title, String author, String ISBN, String owner,
                String comment, String status, String photo ) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.owner = owner;
        this.comment = comment;
        this.status = status;
        this.photo = photo;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getISBN() { return ISBN; }
    public String getOwner() { return owner; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public String getPhoto() { return photo; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setComment(String comment) { this.comment = comment; }
    public void setStatus(String status) { this.status = status; }
    public void setPhoto(String photo) { this.photo = photo; }
}