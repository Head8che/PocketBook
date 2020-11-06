package com.example.pocketbook;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.RequestList;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * NOTE :
 * To run an individual test,
 * Run tab -> Edit Configurations ...
 *              -> (select your test)
 *              -> Shorten command line
 *              -> class path
 */

@RunWith(MockitoJUnitRunner.class)
public class RequestListTest {
    private RequestList mockRequestList(){
        return new RequestList(mockBook().getId());
    }

    @Mock
    private Context mockContext;

    private void mockFirebase() {

    }

    /**
     * Create a mock Book that is AVAILABLE
     * Owner of book is jane@gmail.com
     * @return
     */
    private Book mockBook(){
        return new Book("mockBook", "testTitle", "testAuthor",
                "074754624X", "jane@gmail.com", "AVAILABLE",
                "this is a test", "GOOD", null,true);
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
                "jakeBorrower", "123456", null);
    }

    private User anotherMockBorrower() {
        return new User("James", "Dean", "james@gmail.com",
                "jamesDean", "123456", null);
    }

    /**
     * Checks that the request has been made
     * for the book specified
     */
    @Test
    public void testAddRequest() {
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
        RequestList requestList = bookToRequest.getRequestList();
        assertEquals(0, requestList.getSize());

        requestList.addRequestToListLocal(requestBook);
        // ensures that the request has been added
        assertEquals(1, requestList.getSize());

    }

    /**
     * Checks that the onwer can see
     * the requests made for their book
     * represented by the requester's emails
     */
    @Test
    public void viewAllRequestsForMyBook() {
        // get a random mockBook
        Book bookToRequest = mockBook();
        // create an owner for this book
        String owner = mockOwner().getEmail();
        // users that wants to borrow this book
        String borrower = mockBorrower().getEmail();
        String anotherBorrower = anotherMockBorrower().getEmail();

        // request for it
        Request request = new Request(borrower, owner, bookToRequest);
        Request anotherRequest = new Request(anotherBorrower, owner, bookToRequest);

        // add to requestList
        RequestList requestList = bookToRequest.getRequestList();
        requestList.addRequestToListLocal(request);
        requestList.addRequestToListLocal(anotherRequest);
        
        // get the size list of requests for this book
        assertEquals(2, requestList.getSize());
        Map<String, Request> requestMap = (Map<String, Request>) requestList.getRequestList();

        ArrayList<String> requesters = new ArrayList<String>(requestMap.keySet());
        assertEquals(borrower, requesters.get(0));
        assertEquals(anotherBorrower, requesters.get(1));

    }

    /**
     * Checks that a request can be deleted
     */
    @Test
    public void testDeleteRequest() {
        // get a random mockBook
        Book bookToRequest = mockBook();
        // create an owner for this book
        String owner = mockOwner().getEmail();
        // users that wants to borrow this book
        String borrower = mockBorrower().getEmail();

        // request for it
        Request request = new Request(borrower, owner, bookToRequest);
        // add to requestList
        RequestList requestList = bookToRequest.getRequestList();
        requestList.addRequestToListLocal(request);
        assertEquals(1, requestList.getSize());

        // remove the request
        assertTrue(requestList.removeRequestFromListLocal(request));
        // check the decremented size
        assertEquals(0, requestList.getSize());
    }

    /**
     * Tests that a request has been accepted and removed
     * and all other requests removed
     */
    @Test
    public void testAcceptRequest() {
        // get a random mockBook
        Book bookToRequest = mockBook();
        // create an owner for this book
        String owner = mockOwner().getEmail();
        // users that wants to borrow this book
        String borrower = mockBorrower().getEmail();

        // request for it
        Request request = new Request(borrower, owner, bookToRequest);
        // add to requestList
        RequestList requestList = bookToRequest.getRequestList();
        requestList.addRequestToListLocal(request);

        // accept the request
        assertTrue(requestList.acceptRequest(request));
        assertEquals(0, requestList.getSize());
    }

    /**
     * Checks if a specified request or borrower
     * is in the list of requests
     */
    @Test
    public void testContainsRequest() {
        // get a random mockBook
        Book bookToRequest = mockBook();
        // create an owner for this book
        String owner = mockOwner().getEmail();
        // users that wants to borrow this book
        String borrower = mockBorrower().getEmail();
        String anotherBorrower = anotherMockBorrower().getEmail();

        // request for it
        Request request = new Request(borrower, owner, bookToRequest);
        // add to requestList
        RequestList requestList = bookToRequest.getRequestList();
        requestList.addRequestToListLocal(request);

        assertTrue(requestList.containsRequest(request));
        assertFalse(requestList.containsRequest(anotherBorrower));
    }



}
