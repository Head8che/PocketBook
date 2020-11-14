package com.example.pocketbook;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;


public class RequestTest {

    /**
     * creates an invalid instance of the Request class
     * @return invalid Request object
     */
    private Request mockInvalidRequest() {
        String requester = "mockrequester@mock.com";
        String requestee = "mockrequestee@mock.com";
        Book requestedBookObject = new Book();  // invalid Book
        String requestDate = "2020/11/06 15:17";

        return new Request(requester, requestee, requestedBookObject, requestDate);
    }

    /**
     * creates a valid instance of the Request class
     * @return valid Request object
     */
    private Request mockValidRequest() {
        String requester = "mockrequester@mock.com";
        String requestee = "mockrequestee@mock.com";
        Book requestedBookObject = new Book("mockID", "mockBookTitle",
                "mockBookAuthor", "9781861972712", "mock@mock.com",
                "AVAILABLE", "This is a mock book.", "GOOD",
                "photo.jpg", new ArrayList<>());  // valid Book
        String requestDate = "2020/11/06 15:17";

        return new Request(requester, requestee, requestedBookObject, requestDate);
    }

    /**
     * tests the getters of the Request class
     * getters should return a valid value if the Request is valid and null otherwise
     */
    @Test
    void testGetters() {
        Request request;

        request = mockInvalidRequest();

        // all getters should return null with invalid Request
        assertNull(request.getRequester());
        assertNull(request.getRequestee());
        assertNull(request.getRequestedBook());
        assertNull(request.getRequestDate());

        request = mockValidRequest();

        // all getters should be non-null with a valid Request
        Assertions.assertEquals("mockrequester@mock.com", request.getRequester());
        Assertions.assertEquals("mockrequestee@mock.com", request.getRequestee());
        Assertions.assertEquals("mockID", request.getRequestedBook());
        Assertions.assertEquals("mockID", request.getRequestedBookObject().getId());
        Assertions.assertEquals("2020/11/06 15:17", request.getRequestDate());
    }
}
