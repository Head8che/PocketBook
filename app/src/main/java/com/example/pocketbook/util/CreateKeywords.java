// WORK IN PROGRESS
// search by -> title, author, ISBN
// wanna have something close to a permutation of of every field
// i.e-> "great gatsby III"
// output -> [ '', 'great', 'gatsby, 'iii', 'great gatsby', 'gatsby iii', 'great iii',
// 'g', 'gr', 'grea', ... ]


package com.example.pocketbook.util;

import android.util.Log;

import com.example.pocketbook.model.Book;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CreateKeywords {
    private Book mBook;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CreateKeywords(Book book) { this.mBook = book; }

    public boolean create(){
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add(""); // first element is empty string

        // fields we're interested in
        String[] fields = {"title", "author", "isbn"};

        // TODO: better algorithm
        for(int i=0; i<fields.length; i++){
            String curr = "", f = "";
            switch (fields[i]){
                case "isbn": f = mBook.getISBN().toLowerCase(); break;
                case "title": f = mBook.getTitle().toLowerCase(); break;
                case "author": f = mBook.getAuthor().toLowerCase(); break;
            }
            for(int j=0; j<f.length(); j++) {
                curr += f.charAt(j);
                keywords.add(curr);
            }
            // adding individual words
            for(String s : f.split(" "))
                keywords.add(s);
        }

        db.collection("catalogue").document(mBook.getId()).update("keywords", keywords);

        return true;
    }

}
