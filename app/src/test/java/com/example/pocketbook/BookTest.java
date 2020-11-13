package com.example.pocketbook;

import com.example.pocketbook.model.Book;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    /**
     * creates an invalid instance of the Book class
     * @return invalid Book object
     */
    private Book mockInvalidBook() {
        String id = "";  // invalid id
        String title = "mockBookTitle";
        String author = "mockBookAuthor";
        String isbn = "9781861972712";
        String owner = "mock@mock.com";
        String status = "AVAILABLE";
        String comment = "This is a mock book.";
        String condition = "GOOD";
        String photo = "";

        return new Book(id, title, author, isbn, owner, status, comment, condition, photo,
                new ArrayList<>());
    }

    /**
     * creates a valid instance of the Book class
     * @return valid Book object
     */
    private Book mockValidBook() {
        String id = "mockID";  // valid id
        String title = "mockBookTitle";
        String author = "mockBookAuthor";
        String isbn = "9781861972712";
        String owner = "mock@mock.com";
        String status = "AVAILABLE";
        String comment = "This is a mock book.";
        String condition = "GOOD";
        String photo = "";

        return new Book(id, title, author, isbn, owner, status, comment, condition, photo,
                new ArrayList<>());
    }

    /**
     * tests the getters of the Book class
     * getters should return a valid value if the Book is valid and null otherwise
     */
    @Test
    void testGetters() {
        Book book;

        book = mockInvalidBook();

        // all getters should return null with invalid Book
        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getISBN());
        assertNull(book.getOwner());
        assertNull(book.getStatus());
        assertNull(book.getComment());
        assertNull(book.getCondition());
        assertNull(book.getPhoto());
        assertNull(book.getRequesters());

        book = mockValidBook();

        // all getters should be non-null with a valid Book
        assertEquals("mockID", book.getId());
        assertEquals("mockBookTitle", book.getTitle());
        assertEquals("mockBookAuthor", book.getAuthor());
        assertEquals("9781861972712", book.getISBN());
        assertEquals("mock@mock.com", book.getOwner());
        assertEquals("AVAILABLE", book.getStatus());
        assertEquals("This is a mock book.", book.getComment());
        assertEquals("GOOD", book.getCondition());
        assertEquals("", book.getPhoto());
        assertEquals(0, book.getRequesters().size());
    }

}
