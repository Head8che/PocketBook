package com.example.pocketbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookList {

    private List<Book> bookList;

    /**
     * Default Constructor
     */
    public BookList() {
        this.bookList = new ArrayList<Book>();
    }

    /**
     * Getter method
     * @return
     *      BookList
     */
    public List<Book> getBookList() {
        List<Book> list = bookList;
        Collections.sort(list);
        return list;
    }

    /**
     * Adds a book to the booklist
     * @param book
     */
    public void add(Book book) {
        if (bookList.contains(book)) {
            throw new IllegalArgumentException();
        }
        bookList.add(book);
    }
}
