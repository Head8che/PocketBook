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
//        FirebaseApp.initializeApp(this);
    }

    /**
     * Getter method for bookList
     * @return
     *      bookList as List<Book>
     */
    public Map<String, Book> getBookList() {
        return bookList;
    }

    public boolean containsBook(Book book) {
        // returns true if book can be found in bookList
        return bookList.get(book.getId()) != null;
    }


    /**
     * Adds a Book to the bookList
     * @param book
     *      Candidate book to add
     */
    public boolean addBook(Book book) {
        String bookID = book.getId();
        if (bookList.get(bookID) != null) {  // if book is already in list
            return false;
        }
        bookList.put(bookID, book);

        Map<String, Object> docData = new LinkedHashMap<>();
        docData.put("id", book.getId());
        docData.put("title", book.getBookTitle());
        docData.put("author", book.getAuthor());
        docData.put("isbn", book.getISBN());
        docData.put("owner", book.getOwner());
        docData.put("status", book.getStatus());
        docData.put("comment", book.getComment());
        docData.put("condition", book.getCondition());
        docData.put("photo", book.getPhoto());

//        FirebaseFirestore.getInstance().collection("books").document(book.getId())
//                .set(docData)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("SET_BOOK", "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("SET_BOOK", "Error writing document", e);
//                    }
//                });

        /*
            TODO: Update book owner's owned_books list
        */

        return true;
    }

    public Book getBook(String id) {
        return bookList.get(id);
    }

    public Book getBookAtPosition(int position) {
        List<String> keys = new ArrayList<String>(bookList.keySet());
        String positionalBookID = keys.get(position);
        return bookList.get(positionalBookID);
    }

    public int getSize() {
        return bookList.size();
    }

    /**
     * Removes the specified book
     * from the booklist
     * @param book
     *      Candidate book to remove
     */
    public boolean removeBook(Book book) {
        String bookID = book.getId();
        if (bookList.get(bookID) == null) {  // if book is not in list
            return false;
        }
        bookList.remove(bookID);

//        FirebaseFirestore.getInstance().collection("books").document(book.getId())
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("DELETE_BOOK", "DocumentSnapshot successfully deleted!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("DELETE_BOOK", "Error deleting document", e);
//                    }
//                });

        /*
            TODO: Update book owner's owned_books list
        */

        return true;
    }

    public void clear() {
        List<String> keys = new ArrayList<String>(bookList.keySet());
        for (String key : keys) {
            bookList.remove(getBook(key));
        }
    }
}