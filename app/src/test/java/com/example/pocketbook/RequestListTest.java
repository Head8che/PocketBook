package com.example.pocketbook;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.RequestList;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.*;
import static org.junit.Assert.*;

public class RequestListTest {
    private RequestList mockRequestList(){
        return new RequestList(mockBook().getId());
    }


    /**
     * Create a mock Book that is AVAILABLE
     * Owner of book is jane@gmail.com
     * @return
     */
    private Book mockBook(){
        Book mockBook = new Book("mockBook", "testTitle", "testAuthor",
                "074754624X", "jane@gmail.com", "AVAILABLE",
                "this is a test", "GOOD", "");
        mockBook.pushNewBookToFirebase();
        return mockBook;
    }

    private User mockOwner() {
        return new User("Jane", "Doe", "jane@gmail.com",
                "janeOwner", "123456", "noPhoto");
    }

    /**
     * Create a mock borrower
     * Borrower is jake@gmail.com
     * @return
     *      User that is borrowing a book
     */
    private User mockBorrower() {

        return new User("Jake", "Doe", "jake@gmail.com",
                "jakeBorrower", "123456", "noPhoto");
    }


    @Test
    void testRequestBook() {

        // get a random mockBook
        Book bookToRequest = mockBook();
        // create an owner for this book
        String owner = mockOwner().getEmail();
        // user wants to borrow this book
        String borrower = mockBorrower().getEmail();

        // request for it
        Request requestBook = new Request(borrower, owner, bookToRequest);
        // add to list of requests for this book
        // assert that there are 0 requests for this book
        RequestList requestList = new RequestList(bookToRequest.getId());
        assertEquals(0, requestList.getSize());
        requestList.addRequest(requestBook);
        // ensures that the request has been added
        assertEquals(1, requestList.getSize());

    }

    @Test
    void viewRequestedBooks() {

    }

    @Test
    void newRequestNotification() {

    }

    @Test
    void viewAllRequestsForBook() {

    }
    

}
