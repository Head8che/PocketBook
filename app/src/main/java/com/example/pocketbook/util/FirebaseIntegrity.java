package com.example.pocketbook.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FirebaseIntegrity {
    // generate data that conforms to valid schema
    public void generateData() {
        /* GENERATE USERS (FIREBASE AUTH) */

        /* GENERATE USERS (FIRESTORE COLLECTIONS) */

        /* GENERATE BOOKS */
        ArrayList<String> book1 = new ArrayList<String>();
        book1.add("vBogJAw5sXKD4taOINlZ");   // id
        book1.add("To Kill a Mockingbird");  // title
        book1.add("Harper Lee");             // author
        book1.add("9780446310789");          // isbn
        book1.add("jane@gmail.com");         // owner
        book1.add("");                       //
        book1.add("");
        book1.add("");

        // verify that jane@gmail exists in FirebaseAuth & has a collection
        // in users (in Firestore) that matches the users schema

        ArrayList<String> book2 = new ArrayList<String>();

    }

    // removes data that does not conform to valid schema
    public void cleanFirebase() {

    }

    public static Book getBookFromFirestore(DocumentSnapshot document) {
        String id = document.getString("id");
        String title = document.getString("title");
        String author = document.getString("author");
        String isbn = document.getString("isbn");
        String owner = document.getString("owner");
        String status = document.getString("status");
        String comment = document.getString("comment");
        String condition = document.getString("condition");
        String photo = document.getString("photo");
        return new Book(id, title, author, isbn, owner, status, comment, condition, photo);

        // TODO: does id exist in Firebase?
        // TODO: does owner exist in FirebaseAuth and in Firestore?
        // TODO: does photo exist in Firebase?
    }

    public static User getUserFromFirestore(DocumentSnapshot document) {
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String username = document.getString("username");
        String password = document.getString("password");
        String photo = document.getString("photo");
//        ArrayList<String> ownedBooks = document.get(ownedBooks);
//        ArrayList<String> requestedBooks = document.get(requestedBooks);
//        ArrayList<String> acceptedBooks = document.get(acceptedBooks);
//        ArrayList<String> borrowedBooks = document.get(borrowedBooks);
        return new User(firstName, lastName, email, username, password, photo
                /*, ownedBooks, requestedBooks, acceptedBooks, borrowedBooks */);

        // TODO: does email exist in FirebaseAuth and in Firestore?
        // TODO: does photo exist in Firebase?
        // TODO: does each bookID in list exist in Firebase?
    }

    public static void updateCatalogueKeywords() {
        FirebaseFirestore.getInstance().collection("catalogue").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()){
                                createKeywordsForBook(getBookFromFirestore(document));
                            }
                        } else {
                            Log.d("UPDATE_CATALOGUE_KEYWORDS", "RIP ", task.getException());
                        }
                    }
                });
    }

    public static void createKeywordsForBook (Book book) {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add(""); // first element is empty string

        // fields we're interested in
        String[] fields = {"title", "author", "isbn"};

        // TODO: better algorithm
        for (String field : fields) {
            String curr = "", f = "";
            switch (field) {
                case "isbn": f = book.getISBN().toLowerCase(); break;
                case "title": f = book.getTitle().toLowerCase(); break;
                case "author": f = book.getAuthor().toLowerCase(); break;
            }
            for (int j = 0; j < f.length(); j++) {
                curr += f.charAt(j);
                keywords.add(curr);
            }
            // adding individual words
            Collections.addAll(keywords, f.split(" "));
        }

        FirebaseFirestore.getInstance().collection("catalogue")
                .document(book.getId()).update("keywords", keywords);
    }

    public static void removeAuthorFromFirestore(String author) {
        CollectionReference catalogueRef = FirebaseFirestore.getInstance().collection("catalogue");
        catalogueRef
                .whereEqualTo("author", author)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    catalogueRef.document(document.getId()).delete();
                                }
                            }
                        }
                    }
                });
    }

    public static void removeUserFromFirebase(String email){
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        usersRef
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    usersRef.document(document.getId()).delete();
                                }
                            }
                        }
                    }
                });
    }

    public static void createTempFromCatalogue() {
        CollectionReference catalogueRef = FirebaseFirestore.getInstance().collection("catalogue");
        catalogueRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.e("DOC", document.getId());
                                    FirebaseFirestore.getInstance().collection("temp").document(document.getId())
                                            .set(document.getData());
                                }
                            }
                        }
                    }
                });

        CollectionReference tempRef = FirebaseFirestore.getInstance().collection("catalogue");
        tempRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.e("DOC", document.getId());
                                    String bookID = document.getId();
                                    FirebaseFirestore.getInstance().collection("catalogue")
                                            .document(document.getId()).collection("requests")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            if (document.exists()) {
                                                                Log.e("DOC", document.getId());
                                                                FirebaseFirestore.getInstance().collection("temp").document(bookID)
                                                                        .collection("requests")
                                                                        .document(document.getId())
                                                                        .set(document.getData())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("TAG", "Deep write!");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w("TAG", "Error deep writing document", e);
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    public static void deleteCollectionDocumentsFirebase(String collection) {
        // if you also want to delete sub-collection documents, the sub-collection docs MUST
        // be deleted first; deleting sub-collection docs does not work on deleted collections

    }

    public static void deleteCollectionSubcollectionFirebase(String collection, String subcollection) {
        // delete all instances of a subcollection in a collection
    }

    /* TODO: have copy... call Parser to only copy valid stuff */
    public static void copyCollectionDocumentsFirebase(String srcCollection, String destCollection) {
        // if you also want to copy sub-collection documents, the sub-collection docs MUST
        // be copied first; deleting sub-collection docs does not work on copied collections

    }

    public static void copyCollectionSubcollectionFirebase(String srcCollection, String subcollection, String destCollection) {
        // copy all instances of a subcollection in a collection
    }

    /*
    ALWAYS DELETE SUBCOLLECTIONS BEFORE DELETING COLLECTIONS!

     HIGH LEVEL OPERATIONS BROKEN DOWN INTO RELEVANT METHODS:

     deleteVirtualDocumentsFromCatalogue()
     - copyCollectionDocumentsFirebase("catalogue", "temp")  // copy books
     - copyCollectionSubcollectionFirebase("catalogue", "requests", "temp")  // copy book requests
     - deleteCollectionSubcollectionFirebase("catalogue", "requests")  // delete book requests
     - deleteCollectionDocumentsFirebase("catalogue")  // delete books
     - copyCollectionDocumentsFirebase("temp", "catalogue")  // copy vaild books back
     - copyCollectionSubcollectionFirebase("temp", "requests", "catalogue")  // copy valid requests
     - deleteCollectionSubcollectionFirebase("temp", "requests")  // delete book requests
     - deleteCollectionDocumentsFirebase("temp")  // delete books

     changeUserEmail("from@email.com", "to@email.com")
     - createAuthUserFirebase("to@email.com", userPasswordFromFirestore)
     - copyUserDocumentFirebase("from@email.com", "to@email.com")
     - copyUserDocumentSubcollectionFirebase("from@email.com", "notifications", "to@email.com")
     - deleteUserDocumentSubcollectionFirebase("from@email.com", "notifications")
     - deleteUserDocumentFirebase("from@email.com")
     - deleteAuthUserFirebase("from@email.com")

     changeUserPassword("user@email.com", "newPassword") // alternative to sending emails
     - changeUserPasswordFieldFirebase("user@email.com", "newPassword")
     - copyUserDocumentFirebase("user@email.com", "randomlyGeneratedTemp@email.com")
     - copyUserDocumentSubcollectionFirebase("user@email.com", "notifications", "randomlyGeneratedTemp@email.com")
     - deleteUserDocumentSubcollectionFirebase("user@email.com", "notifications")
     - deleteUserDocumentFirebase("user@email.com")
     - deleteAuthUserFirebase("user@email.com")
     - createAuthUserFirebase("user@email.com", newUserPasswordFromFirestore)
     - copyUserDocumentFirebase("randomlyGeneratedTemp@email.com", "user@email.com")  // copy vaild books back
     - copyUserDocumentSubcollectionFirebase("randomlyGeneratedTemp@email.com", "notifications", "user@email.com")
     - deleteUserDocumentSubcollectionFirebase("randomlyGeneratedTemp@email.com", "notifications")  // delete book requests
     - deleteUserDocumentFirebase("randomlyGeneratedTemp@email.com")  // delete books

     ? Add API Book to Firebase User ? Add API Book to Firebase (random user) ? Add in loop for multiple random books ?
     - randomly get real book ISBN
     - download book data & download image from URL
     - parse book to user (or random user)
     - pushNewBookToFirebase();

     addRandomBooksToRandomUsers(count)  // count is the number of books to add; should NOT work if no users exist
     addRandomUsers(count)  // first part of email should be name from API, pw should be 123456;
                            // first check if email exists in Firebase, then auth create, then Firestore
     addRandomBooksToUser(count, user)
     addBookToUser(isbn, user)  // if API works properly
     makeRandomUserRequestsToRandomBooks(count)  // if user has already requested book, request should fail
     makeRandomRequestsToBook(count, isbn)  // book has to exist in Firebase;
                                            // count other users request book; if count > # of users, create count requests then stop

     Notes
     - copyDocument for user should only work if auth account exists (SCRATCH THAT; need temp docs w/o email)

     Stuff from other places
     - book.pushNewBookToFirebase() --> FI.pNBTF(book)
     - currentBookCover = FirebaseStorage.getInstance().getReference()
                .child("default_images").child("no_book_cover_light.png") --> FI.getDefaultBookCover();
     - StorageReference childRef = storageRef.child(userName+".jpg") --> FI.uploadImage();
     ...

     */

}
