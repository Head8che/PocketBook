package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;

public class Book {
    private String title;
    private String author;
    private String ISBN;
    private String owner;
    private String comment;
    private String status;
    private String photo;

    Parser parser;

    public Book() {} // used by firestore to populate a book

    public Book(String title, String author, String ISBN) {
        // validates the arguments
        parser = new Parser(title, author, ISBN);

        if (parser.checkTitleAndAuthor()) {
            this.title = title;
            this.author = author;
        }
        if (parser.checkIsbn()) {
            this.ISBN = ISBN;
        }
    }

    public Book(String title, String author, String ISBN, String owner,
                String comment, String status, String photo) {
        // validates the arguments
        parser = new Parser(title, author, ISBN, comment);
        if (parser.checkTitleAndAuthor()) {
            this.title = title;
            this.author = author;
        }
        if (parser.checkIsbn()) {
            this.ISBN = ISBN;
        }

        if (parser.checkComment()) {
            this.comment = comment;
        }

        this.owner = owner;
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