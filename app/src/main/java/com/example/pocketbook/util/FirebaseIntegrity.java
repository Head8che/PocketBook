package com.example.pocketbook.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
    }

    public static User getUserFromFirestore(DocumentSnapshot document) {
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String username = document.getString("username");
        String password = document.getString("password");
        String photo = document.getString("photo");
        return new User(firstName, lastName, email, username, password, photo);
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
}
