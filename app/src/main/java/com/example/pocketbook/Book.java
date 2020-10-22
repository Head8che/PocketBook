package com.example.pocketbook;

public class Book implements Comparable<Book>{

    String bookId;
    private User owner;
    private BookDescription description;

    public Book(String bookId, User owner, BookDescription description) {
        this.bookId = bookId;
        this.owner = owner;
        this.description = description;
    }

    public String getBookId() {
        return bookId;
    }

    @Override
    public int compareTo(Book book) {
        return this.bookId.compareTo(book.getBookId());
    }
}
