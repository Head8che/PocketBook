package com.example.pocketbook;

import android.media.Image;

public class BookDescription {

    private String title;
    private String author;
    private String comment;
    // TODO: find a way to attach image objects. As path?
    private String image;
    private String isbn;

    /**
     * Default Constructor
     * @param title
     *      Title of the book
     * @param author
     *      Author of the book
     * @param isbn
     *      ISBN of the book
     */
    public BookDescription(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    /* Constructor without image */
    public BookDescription(String title, String author, String comment, String isbn) {
        this.title = title;
        this.author = author;
        this.comment = comment;
        this.isbn = isbn;
    }

    /* Constructor with image and comment */
    public BookDescription(String title, String author, String comment, String image, String isbn) {
        this.title = title;
        this.author = author;
        this.comment = comment;
        this.image = image;
        this.isbn = isbn;
    }


}
