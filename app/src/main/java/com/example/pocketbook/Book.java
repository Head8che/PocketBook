package com.example.pocketbook;

public class Book {

    private User owner;
    private BookDescription description;

    public Book(User owner, BookDescription description) {
        this.owner = owner;
        this.description = description;
    }
}
