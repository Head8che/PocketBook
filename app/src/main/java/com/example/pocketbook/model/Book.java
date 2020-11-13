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
    private ArrayList<String> requesters;


    /**
     * Firestore constructor
     *  to populate a book
     */
    public Book() {}

    /**
     * Maximum arg constructor for Book
     * @param id : unique book id
     * @param title : title of book
     * @param author : author of book
     * @param isbn : isbn retrieved as String of Book
     * @param isbn : isbn retrieved as String of Book
     * @param owner : User that owns the Book
     * @param status : indicates availability of book (available, requested, accepted, borrowed)
     * @param comment : Comment set by owner
     * @param condition : Condition of the book, set by owner
     * @param photo : photo string of book by owner
     */
    public Book(String id, String title, String author, String isbn, String owner,
                String status, String comment, String condition, String photo) {

        this.id = (id == null) ? null : id.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.owner = owner.trim().toLowerCase();  // lowercase email
        this.status = status.trim().toUpperCase();  /* one of ["AVAILABLE", "REQUESTED",
                                                               "ACCEPTED", "BORROWED"] */

        if (!(status.equals("AVAILABLE")) && !(status.equals("REQUESTED"))
                && !(status.equals("ACCEPTED")) && !(status.equals("BORROWED"))) {
            this.status = "AVAILABLE";
        }

        this.comment = ((comment == null) || (comment.trim().equals("")))
                ? "" : comment.trim();
        this.condition = ((condition == null) || (condition.trim().equals("")))
                ? "" : condition.trim().toUpperCase();  /* one of ["GREAT", "GOOD",
                                                                     "FAIR", "ACCEPTABLE"] */
        this.photo = ((photo == null) || (photo.trim().equals("")))
                ? "" : photo.trim();

        this.requesters = new ArrayList<>();
    }

    public Book(String id, String title, String author, String isbn, String owner,
                String status, String comment, String condition, String photo, ArrayList<String> requesters) {

        this.id = (id == null) ? null : id.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.owner = owner.trim().toLowerCase();  // lowercase email
        this.status = status.trim().toUpperCase();  /* one of ["AVAILABLE", "REQUESTED",
                                                               "ACCEPTED", "BORROWED"] */

        if (!(status.equals("AVAILABLE")) && !(status.equals("REQUESTED"))
                && !(status.equals("ACCEPTED")) && !(status.equals("BORROWED"))) {
            this.status = "AVAILABLE";
        }

        this.comment = ((comment == null) || (comment.trim().equals("")))
                ? "" : comment.trim();
        this.condition = ((condition == null) || (condition.trim().equals("")))
                ? "" : condition.trim().toUpperCase();  /* one of ["GREAT", "GOOD",
                                                                     "FAIR", "ACCEPTABLE"] */
        this.photo = ((photo == null) || (photo.trim().equals("")))
                ? "" : photo.trim();

        this.requesters = requesters;
    }

    /**
     * Constructor made for testing
     * @param id : unique book id
     * @param title : title of book
     * @param author : author of book
     * @param isbn : isbn retrieved as String of Book
     * @param owner : User that owns the Book
     * @param status : indicates availability of book (available, requested, accepted, borrowed)
     * @param comment : Comment set by owner
     * @param condition : Condition of the book, set by owner
     * @param photo : photo string of book by owner
     * @param testing : true if the constructor is needed
     */
    public Book(String id, String title, String author, String isbn, String owner,
                String status, String comment, String condition, String photo, boolean testing) {

        this.id = (id == null) ? null : id.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.owner = owner.trim().toLowerCase();  // lowercase email
        this.status = status.trim().toUpperCase();  /* one of ["AVAILABLE", "REQUESTED",
                                                               "ACCEPTED", "BORROWED"] */

        if (!(status.equals("AVAILABLE")) && !(status.equals("REQUESTED"))
                && !(status.equals("ACCEPTED")) && !(status.equals("BORROWED"))) {
            this.status = "AVAILABLE";
        }

        this.comment = ((comment == null) || (comment.trim().equals("")))
                ? "" : comment.trim();
        this.condition = ((condition == null) || (condition.trim().equals("")))
                ? "" : condition.trim().toUpperCase();  /* one of ["GREAT", "GOOD",
                                                                     "FAIR", "ACCEPTABLE"] */
        this.photo = ((photo == null) || (photo.trim().equals("")))
                ? "" : photo.trim();

//        this.requestList = new RequestList(this.id, true);
    }


    /**
     * Getter method for Id
     * @return
     *      id as String
     */
    public String getId() { return this.id; }

    /**
     * Getter method for Title
     * @return
     *      title as String
     */
    public String getTitle() { return this.title; }

    /**
     * Getter method for Author
     * @return
     *      author as String
     */
    public String getAuthor() { return this.author; }

    /**
     * Getter method for ISBN
     * @return
     *      isbn as String
     */
    public String getISBN() { return this.isbn; }

    /**
     * Getter method for Owner
     * @return
     *      owner as String
     */
    public String getOwner() { return this.owner; }

    /**
     * Getter method for Comment
     * @return
     *      comment as String
     */
    public String getComment() { return this.comment; }

    /**
     * Getter method for Condition
     * @return
     *      condition as String
     */
    public String getCondition() { return this.condition; }

    /**
     * Getter method for Status
     * @return
     *      status as String
     */
    public String getStatus() { return this.status; }

    /**
     * Getter method for Photo
     * @return
     *      photo as String
     */
    public String getPhoto() { return this.photo; }

    /**
     * Getter method for RequestList
     * @return
     *      requestList as RequestList
     */
//    public RequestList getRequestList() { return this.requestList; }

    public ArrayList<String> getRequesters() {
        return this.requesters;
    }

    public boolean setId(String id) {
        id = id.trim();
        if (Parser.isValidBookId(id)) {
            this.id = id;
            return true;
        }
        return false;
    }

    /* Setter Functions for Local and Firebase */
    public boolean setTitle(String title) {
        title = title.trim();
        if (Parser.isValidBookTitle(title)) {
            this.title = title;
            return true;
        }
        return false;
    }
    public boolean setAuthor(String author) {
        author = author.trim();
        if (Parser.isValidBookAuthor(author)) {
            this.author = author;
            return true;
        }
        return false;
    }
    public boolean setIsbn(String isbn) {
        isbn = isbn.trim();
        if (Parser.isValidBookIsbn(isbn)) {
            this.isbn = isbn;
            return true;
        }
        return false;
    }
    public boolean setComment(String comment) {
        if (Parser.isValidBookComment(comment)) {
            this.comment = comment.trim();
            return true;
        }
        return false;
    }
    public boolean setCondition(String condition) {
        condition = condition.trim().toUpperCase();
        if (Parser.isValidBookCondition(condition)) {
            this.condition = condition;
            return true;
        }
        return false;
    }
    public boolean setStatus(String status) {
        status = status.trim().toUpperCase();
        if (Parser.isValidBookStatus(status)) {
            this.status = status;
            return true;
        }
        return false;
    }
    public boolean setRequesters(ArrayList<String> requesters) {
        if (Parser.isValidRequesters(requesters)) {
            this.requesters = requesters;
        }
        return false;
    }
    public boolean setPhoto(String photo) {
        photo = photo.trim();
        if (Parser.isValidBookPhoto(photo)) {
            this.photo = photo;
            return true;
        }
        return false;
    }

//    /**
//     * Adds a request to the request list
//     * @param request
//     * @return
//     *      true if successful,
//     *      false otherwise
//     */
//    public void addRequest(Request request) {
//        if (!this.status.equals("REQUESTED")) {
//            this.setStatus("REQUESTED");
//        }
//        return;
//    }

//    /**
//     * Accepts a request made to the book
//     * @param request : request made to the owner
//     * @return
//     *      true if ACCEPTED
//     */
//    public boolean acceptRequest(Request request) {
//        this.setStatus("ACCEPTED");
//        return true;
//    }

//    /**
//     * Declines a request made to the book
//     */
//    public boolean declineRequest(Request request) {
//    }

}
