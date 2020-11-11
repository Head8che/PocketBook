package com.example.pocketbook.model;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocketbook.activity.EditBookActivity;
import com.example.pocketbook.model.RequestList;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/*
    TODO: handle setPhoto() & uploading image to FirebaseStorage and overwriting old image;
          will likely be similar to how SignUpActivity sets images.
*/

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
    private RequestList requestList;


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

        this.requestList = new RequestList(this.id);
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

        this.requestList = new RequestList(this.id, true);
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
    public RequestList getRequestList() { return this.requestList; }

    /**
     * Getter method for BookCover
     * @return
     *      StorageReference to image
     */
    public StorageReference getBookCover() {
        if (this.photo == null || this.photo.equals("") || !(this.photo.endsWith(".jpg"))) {
            return FirebaseStorage.getInstance().getReference()
                    .child("default_images").child("no_book_cover_light.png");
        }
        return FirebaseStorage.getInstance().getReference().child("book_covers").child(this.photo);
    }

    /**
     * Sets the cover of the book as an image
     * if the url is a local file
     * @param localURL : url of the book
     */
    public void setBookCover(String localURL) {
        if(localURL != null) {

            String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

            StorageReference childRef = FirebaseStorage.getInstance().getReference().child("book_covers").child(photoName);

            if (localURL.equals("REMOVE")) {
                childRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("REMOVE_BOOK_COVER", "Book data successfully written!");
                                setPhoto("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("REMOVE_BOOK_COVER", "Error writing book data!");
                            }
                        });
                return;
            }

            //uploading the image
            UploadTask uploadTask = childRef.putFile(Uri.fromFile(new File(localURL)));

            Log.e("SET_BOOK_COVER", "After parse!");

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_BOOK_COVER", "Successful upload!");
                    setPhoto(photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("SET_BOOK_COVER", "Failed upload!");
                }
            });
        }
    }

    /**
     * Sets the cover of the book
     * if the argument is a bimap file
     * @param bitmap : photo of book
     */
    public void setBookCover(Bitmap bitmap) {

        String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

        if(bitmap != null) {

            StorageReference childRef = FirebaseStorage.getInstance().getReference().child("book_covers").child(photoName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            Book thisBook = this;

            //uploading the image
            UploadTask uploadTask = childRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_BOOK_COVER", "Successful upload!");
                    thisBook.setPhoto(photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("SET_BOOK_COVER", "Failed upload!");
                }
            });
        }
    }

    /* Setter Functions for Local and Firebase */
    public void setTitle(String title) {
        setTitleLocal(title);
        setTitleFirebase(title);
    }
    public void setAuthor(String author) {
        setAuthorLocal(author);
        setAuthorFirebase(author);
    }
    public void setIsbn(String isbn) {
        setIsbnLocal(isbn);
        setIsbnFirebase(isbn);
    }
    public void setComment(String comment) {
        setCommentLocal(comment);
        setCommentFirebase(comment);
    }
    public void setCondition(String condition) {
        setConditionLocal(condition);
        setConditionFirebase(condition);
    }
    public boolean setStatus(String status) {
        String statusUpper = status.toUpperCase();

        if (!(statusUpper.equals("AVAILABLE")) && !(statusUpper.equals("REQUESTED"))
        && !(statusUpper.equals("ACCEPTED")) && !(statusUpper.equals("BORROWED"))) {
            return false;
        }
        setStatusLocal(status);
        setStatusFirebase(status);

        return true;
    }

    public void setPhoto(String photo) {
        setPhotoLocal(photo);
        setPhotoFirebase(photo);
    }

    /* Setter Function Attributes
     */
    public void setTitleLocal(String title) { this.title = title.trim(); }
    public void setAuthorLocal(String author) { this.author = author.trim(); }
    public void setIsbnLocal(String isbn) { this.isbn = isbn.trim(); }
    public void setCommentLocal(String comment) {
        this.comment = ((comment == null) || (comment.trim().equals("")))
                ? "" : comment.trim();
    }
    public void setConditionLocal(String condition) {
        this.condition = ((condition == null) || (condition.trim().equals("")))
                ? "" : condition.trim().toUpperCase();
    }
    public boolean setStatusLocal(String status) {
        String statusUpper = status.toUpperCase();

        if (!(statusUpper.equals("AVAILABLE")) && !(statusUpper.equals("REQUESTED"))
                && !(statusUpper.equals("ACCEPTED")) && !(statusUpper.equals("BORROWED"))) {
            return false;
        }
        this.status = status.trim().toUpperCase();
        return true;
    }

    public void setPhotoLocal(String photo) {
        this.photo = ((photo == null) || (photo.trim().equals("")))
            ? "" : photo.trim();
    }

    public void setTitleFirebase(String title) { setBookDataFirebase("title", title); }
    public void setAuthorFirebase(String author) { setBookDataFirebase("author", author); }
    public void setIsbnFirebase(String isbn) { setBookDataFirebase("isbn", isbn); }
    public void setCommentFirebase(String comment) {
        setBookDataFirebase("comment", (this.comment == null) ? "" : this.comment);
    }
    public void setConditionFirebase(String condition) {
        setBookDataFirebase("condition", (this.condition == null) ? "" : this.condition);
    }
    public void setStatusFirebase(String status) {
        setBookDataFirebase("status", (this.status == null) ? "" : this.status);
    }
    public void setPhotoFirebase(String status) {
        setBookDataFirebase("photo", (this.photo == null) ? "" : this.photo);
    }

    public void setBookDataFirebase(String bookFieldName, String bookFieldValue) {
        FirebaseFirestore.getInstance().collection("catalogue").document(this.id)
                .update(bookFieldName, bookFieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SET_BOOK", "Book data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_BOOK", "Error writing book data!", e);
                    }
                });
    }

    /*
    Pushes a new Book to Firebase
     */
    public void pushNewBookToFirebase() {

        DocumentReference bookDoc = FirebaseFirestore.getInstance().collection("books").document();
        this.id = bookDoc.getId();

        Map<String, Object> docData = new HashMap<>();
        docData.put("id", this.id);
        docData.put("title", this.title);
        docData.put("author", this.author);
        docData.put("isbn", this.isbn);
        docData.put("owner", this.owner);
        docData.put("status", this.status);
        docData.put("comment", (this.comment == null) ? "" : this.comment);
        docData.put("condition", (this.condition == null) ? "" : this.condition);
        docData.put("photo", (this.photo == null) ? "" : this.photo);

        Book thisBook = this;

        FirebaseFirestore.getInstance().collection("catalogue").document(this.id)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW_BOOK", "Book data successfully written!");
                        Log.d("DESCRIPTION : ", id + title + author + isbn + owner + status + comment + condition);
//                        FirebaseIntegrity.createKeywordsForBook(thisBook);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NEW_BOOK", "Error writing book data!", e);
                    }
                });

    }

    /**
     * Adds a request to the request list
     * @param request
     * @return
     *      true if successful,
     *      false otherwise
     */
    public boolean addRequest(Request request) {
        if (!this.status.equals("REQUESTED")) {
            this.setStatus("REQUESTED");
        }
        return requestList.addRequest(request);
    }

    /**
     * Accepts a request made to the book
     * @param request : request made to the owner
     * @return
     *      true if ACCEPTED
     */
    public boolean acceptRequest(Request request) {
        this.setStatus("ACCEPTED");
        return requestList.acceptRequest(request);
    }

    /**
     * Declines a request made to the book
     */
    public boolean declineRequest(Request request) {
        return requestList.declineRequest(request);
    }

}
