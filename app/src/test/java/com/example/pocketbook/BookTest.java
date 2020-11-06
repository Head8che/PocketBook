package com.example.pocketbook;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.RequestList;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private BookList mockBookList() {
        BookList bookList = new BookList();
        bookList.addBookToListLocal(mockBook());
        return bookList;
    }

    private Book mockBook() {
        String id = "";
        String title = "mockBookTitle";
        String author = "mockBookAuthor";
        String isbn = "9781928374650";
        String owner = "mock@mock.com";
        String status = "AVAILABLE";
        String comment = "This is a mock book.";
        String condition = "GOOD";
        String photo = "";

        return new Book(id, title, author, isbn, owner, status, comment, condition, photo);
    }

    @Test
    void testGetters() {
        Book book = mockBook();

        assertEquals("", book.getId());
        assertEquals("mockBookTitle", book.getTitle());
        assertEquals("mockBookAuthor", book.getAuthor());
        assertEquals("9781928374650", book.getISBN());
        assertEquals("mock@mock.com", book.getOwner());
        assertEquals("AVAILABLE", book.getStatus());
        assertEquals("This is a mock book.", book.getComment());
        assertEquals("GOOD", book.getCondition());
        assertEquals("", book.getPhoto());
        assertEquals(0, book.getRequestList().getSize());
    }

    @Test
    void testLocalSetters() {
        Book book = mockBook();

        book.setTitleLocal("newMockBookTitle");
        book.setAuthorLocal("newMockBookAuthor");
        book.setIsbnLocal("9787463829473");
        book.setStatusLocal("ACCEPTED");
        book.setCommentLocal("This is a new mock book.");
        book.setConditionLocal("FAIR");
        book.setPhotoLocal("123456789.jpg");

        assertEquals("newMockBookTitle", book.getTitle());
        assertEquals("newMockBookAuthor", book.getAuthor());
        assertEquals("9787463829473", book.getISBN());
        assertEquals("ACCEPTED", book.getStatus());
        assertEquals("This is a new mock book.", book.getComment());
        assertEquals("FAIR", book.getCondition());
        assertEquals("123456789.jpg", book.getPhoto());
    }

    @Test
    void testStatus() {
        Book book = mockBook();

        assertTrue(book.setStatusLocal("AVAILABLE"));
        assertTrue(book.setStatusLocal("REQUESTED"));
        assertTrue(book.setStatusLocal("ACCEPTED"));
        assertTrue(book.setStatusLocal("BORROWED"));

        assertFalse(book.setStatusLocal("badChoice1"));
        assertFalse(book.setStatusLocal("oiweniewewdn"));

    }

}
