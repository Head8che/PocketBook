package com.example.pocketbook.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Firestore constructor
     *  to populate a book
     */
    public Book() {}

    /**
     * Minimum arg constructor for Book
     * @param id : uniquely identifies the book in the db
     * @param title : title of book
     * @param author : author of book
     * @param isbn : isbn as a String
     * @param owner : Owner of Book
     * @param status : indicates availability of book (available, requested, accepted, borrowed)
     */
    public Book(String id, String title, String author, String isbn, String owner, String status) {
        this.id = id.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.owner = owner.trim();
        this.status = status.trim();
    }

    /**
     * Maximum arg constructor for Book
     * @param id : unique book id
     * @param title : title of book
     * @param author : author of book
     * @param isbn : isbn retrieved as String of Book
     * @param owner : User that owns the Book
     * @param status : indicates availability of book (available, requested, accepted, borrowed)
     * @param comment : Comment set by owner
     * @param condition : Condition of the book, set by owner
     * @param photo : photo string of book by owner
     */
    public Book(String id, String title, String author, String isbn, String owner,
                String status, String comment, String condition, String photo ) {
        this.id = id.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.owner = owner.trim();
        this.status = status.trim();
        this.comment = ((comment == null) || (comment.trim().equals("")))
                ? null : comment.trim();
        this.condition = ((condition == null) || (condition.trim().equals("")))
                ? null : condition.trim();
        this.photo = ((photo == null) || (photo.trim().equals("")))
                ? null : photo.trim();
    }

    /* Getter Functions */
    public String getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public String getISBN() { return this.isbn; }
    public String getOwner() { return this.owner; }
    public String getComment() { return this.comment; }
    public String getCondition() { return this.condition; }
    public String getStatus() { return this.status; }
    public String getPhoto() { return this.photo; }

    public StorageReference getBookCover() {
        if (this.photo == null || this.photo.equals("") || !this.photo.endsWith(".jpg")) {
            return FirebaseStorage.getInstance().getReference()
                    .child("default_images").child("no_book_cover_light.png");
        }
        return FirebaseStorage.getInstance().getReference().child("book_covers").child(this.photo);
    }

    /*
            TODO: upload new image to FirebaseStorage and overwrite old image
        */
    /* Setter Functions */
    public void setBook(String id) {
        this.id = id.trim();

        Map<String, Object> docData = new HashMap<>();
        docData.put("id", this.id);
        docData.put("title", this.title);
        docData.put("author", this.author);
        docData.put("isbn", this.isbn);
        docData.put("owner", this.owner);
        docData.put("status", this.status);
        docData.put("comment", this.comment);
        docData.put("condition", this.condition);
        docData.put("photo", this.photo);

        FirebaseFirestore.getInstance().collection("books").document(this.id)
            .set(docData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("SET_BOOK", "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("SET_BOOK", "Error writing document", e);
                }
            });

    }

    /* Allows comparison between bookIds as Strings
     *   Overrides compareTo method in Comparable
     * */
//    @Override
//    public int compareTo(com.example.pocketbook.model.Book book) {
//        return this.id.compareTo(book.getTitle());
//    }

}
