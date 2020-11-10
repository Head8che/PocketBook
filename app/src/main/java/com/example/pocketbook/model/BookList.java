package com.example.pocketbook.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookList implements Serializable {

    private LinkedHashMap<String, Book> bookList = new LinkedHashMap<String, Book>();

    /**
     * Default constructor
     *  Creates a booklist with no arguments
     */
    public BookList() {
        this.bookList = new LinkedHashMap<String, Book>();
    }

    /**
     * Getter method for bookList
     * @return
     *      bookList as Map<Book>
     */
    /* TODO: EXTEND */
    public Map<String, Book> getBookList() {
        return bookList;
    }

    /**
     * Getter method for book
     * @param id
     * @return
     *      book as Book
     */
    /* TODO: EXTEND */
    public Book getBook(String id) {
        return bookList.get(id);
    }

    // this method is specific to BookList (i.e. not from superclass)
    public Book getBookAtPosition(int position) {
        List<String> keys = new ArrayList<String>(bookList.keySet());
        String positionalBookID = keys.get(position);
        return bookList.get(positionalBookID);
    }

    /* TODO: EXTEND */
    public int getSize() {
        return bookList.size();
    }

    /* TODO: EXTEND */
    // overloaded containsBook methods return true if book can be found in bookList
    public boolean containsBook(String bookID) { return bookList.get(bookID) != null; }
    public boolean containsBook(Book book) { return bookList.get(book.getId()) != null; }


    /**
     * Adds a Book to the bookList
     * @param book
     *      Candidate book to add
     */
    /* TODO: EXTEND */
    public boolean addBook(Book book) {
        addBookToListFirebase(book);
        return addBookToListLocal(book);
    }

    /**
     * Removes the specified book
     * from the booklist
     * @param book
     *      Candidate book to remove
     */
    /* TODO: EXTEND */
    public boolean removeBook(Book book) {
        removeBookFromListFirebase(book);
        return removeBookFromListLocal(book);
    }

    /* TODO: EXTEND & OVERRIDE */

    /**
     * Adds a book to the local LinkedHashMap if it exists
     * @param book : candidate book to add
     * @return
     *      returns true if successful
     *      false otherwise
     */
    public boolean addBookToListLocal(Book book) {
        String bookID = book.getId();
        if (containsBook(bookID)) {  // if book is already in list
            return false;
        }
        bookList.put(bookID, book);
        return true;
    }

    /* TODO: EXTEND & OVERRIDE */

    /**
     * Removes a book from the LinkedHashMap if it exists
     * @param book
     * @return
     *      true if removed successfully
     *      false otherwise
     */
    public boolean removeBookFromListLocal(Book book) {
        String bookID = book.getId();
        if (!containsBook(bookID)) {  // if book is not in list
            return false;
        }
        bookList.remove(bookID);
        return true;
    }

    /* TODO: EXTEND & OVERRIDE */

    /**
     * Adds a book to the catalogue collection firebase
     * @param book : candidate Book to add
     */
    public void addBookToListFirebase(Book book) {

        if (containsBook(book)) {  // if book is already in list
            return;
        }

        FirebaseFirestore.getInstance().collection("catalogue").document(book.getId())
                .update("id", book.getId(),
                        "title", book.getTitle(),
                        "author", book.getAuthor(),
                        "isbn", book.getISBN(),
                        "owner", book.getOwner(),
                        "status", book.getStatus(),
                        "comment", book.getComment(),
                        "condition", book.getCondition(),
                        "photo", book.getPhoto()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW_BOOK", "Book data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("NEW_BOOK", "Error writing book data!");
                    }
                });
    }

    /* TODO: EXTEND & OVERRIDE */

    /**
     * Removes a book from the catalogue collection in Firebase
     * @param book : candidate Book to remove
     */
    public void removeBookFromListFirebase(Book book) {

        if (!containsBook(book)) {  // if book is not already in list
            return;
        }

        FirebaseFirestore.getInstance().collection("catalogue").document(book.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REMOVE_BOOK", "Book data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("REMOVE_BOOK", "Error writing book data!");
                    }
                });
    }
}
