package com.example.pocketbook.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookList {

    private List<Book> bookList;

    /**
     * Default constructor
     *  Creates a booklist with no arguments
     */
    public BookList() { this.bookList = new ArrayList<Book>(); }


    /**
     * Getter method for bookList
     * @return
     *      bookList as List<Book>
     */
    public List<Book> getBookList() {
        List<Book> list = bookList;
        Collections.sort(list);
        return list;
    }


    /**
     * Adds a Book to the bookList
     * @param book
     *      Candidate book to add
     */
    public void add(Book book) {
        if (bookList.contains(book)) {
            throw new IllegalArgumentException();
        }
        bookList.add(book);
    }

    /**
     * Deletes the specified book
     * from the booklist
     * @param book
     *      Candidate book to delete
     */
    public void delete(Book book) {
        if (!bookList.contains(book)) {
            throw new IllegalArgumentException();
        }
        bookList.remove(book);

    }
}