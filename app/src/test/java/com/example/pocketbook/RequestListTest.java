package com.example.pocketbook;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.RequestList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;

public class RequestListTest {
    private RequestList mockRequestList(){
        return new RequestList(mockBook().getId());
    }

    private Book mockBook(){
        Book mockBook = new Book("mockBook", "testTitle", "testAuthor", "074754624X", "jane@gmail.com", "AVAILABLE", "this is a test", "GOOD", "");
        mockBook.pushNewBookToFirebase();
        return mockBook;
    }
    

}
