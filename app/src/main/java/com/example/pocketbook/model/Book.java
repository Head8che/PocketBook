package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String owner;
    private String status;
    private String comment;
    private String condition;
    private String photo;
    private boolean nonExchange;
    private ArrayList<String> requesters;


    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public Book() {}

    /**
     * Book constructor
     * @param id unique book id
     * @param title title of book
     * @param author author of book
     * @param isbn isbn of Book
     * @param owner User that owns the Book
     * @param status indicates availability of book
     * @param comment comment set by owner
     * @param condition condition of the book, set by owner
     * @param photo photo string of book by owner
     * @param requesters ArrayList of user emails that have requested the book
     */
    public Book(String id, String title, String author,
                String isbn, String owner, String status, boolean nonExchange,
                String comment, String condition, String photo, ArrayList<String> requesters) {

        // if non-optional fields are not null
        if ((id != null) && (title != null) && (author != null)
                && (isbn != null) && (owner != null) && (status != null)
                && (condition != null) && (requesters != null)) {

            // trim all values
            id = id.trim();
            title = title.trim();
            author = author.trim();
            isbn = isbn.trim();
            owner = owner.trim().toLowerCase();  // lowercase email
            status = status.trim().toUpperCase();  // uppercase status
            comment = (comment == null) ? "" : comment.trim();  // replace null with empty string
            condition = condition.trim().toUpperCase();  // uppercase condition
            photo = (photo == null) ? "" : photo.trim();  // replace null with empty string

            // only sets Book data if the data is valid
            if (Parser.isValidBookData(id, title, author, isbn, owner, status,
                    comment, condition, photo, requesters)) {

                this.id = id;
                this.title = title;
                this.author = author;
                this.isbn = isbn;
                this.owner = owner;
                this.status = status;  // one of ["AVAILABLE", "REQUESTED", "ACCEPTED", "BORROWED"]
                this.nonExchange = nonExchange;
                this.comment = comment;
                this.condition = condition;  // one of ["GREAT", "GOOD", "FAIR", "ACCEPTABLE"]
                this.photo = photo;
                this.requesters = requesters;
            }
        }
    }


    /**
     * Getter method for id
     * @return id as String
     */
    public String getId() { return this.id; }

    /**
     * Getter method for title
     * @return title as String
     */
    public String getTitle() { return this.title; }

    /**
     * Getter method for author
     * @return author as String
     */
    public String getAuthor() { return this.author; }

    /**
     * Getter method for isbn
     * @return isbn as String
     */
    public String getISBN() { return this.isbn; }

    /**
     * Getter method for owner
     * @return owner as String
     */
    public String getOwner() { return this.owner; }

    /**
     * Getter method for comment
     * @return comment as String
     */
    public String getComment() { return this.comment; }

    /**
     * Getter method for condition
     * @return condition as String
     */
    public String getCondition() { return this.condition; }

    /**
     * Getter method for status
     * @return status as String
     */
    public String getStatus() { return this.status; }

    /**
     * Getter method for nonExchange
     * @return nonExchange as boolean
     */
    public boolean getNonExchange() { return this.nonExchange; }

    /**
     * Getter method for photo
     * @return photo as String
     */
    public String getPhoto() { return this.photo; }

    /**
     * Getter method for requesters
     * @return requestList as ArrayList of Strings
     */
    public ArrayList<String> getRequesters() {
        return this.requesters;
    }

}
