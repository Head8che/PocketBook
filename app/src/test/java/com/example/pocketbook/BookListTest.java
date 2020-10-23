package com.example.pocketbook;


import android.util.Log;

import org.junit.jupiter.api.Test;

import static android.content.ContentValues.TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookListTest {

    private static final String TAG = "DEBUG";

    /**
     * Create a mock User
     * @return
     *      User
     */
    private User mockUser() {
        return new User("mockUserId");
    }

    /**
     * Create an empty mock booklist and return
     * @return
     *      Booklist
     */
    private BookList mockBookList() {
        return new BookList();
    }

    /**
     * Create and return a mock book description
     * @return
     */
    private BookDescription mockDescription() {
        return new BookDescription(
                "mockTitle", "mockAuthor", "mockComment",
                "mockPicture", "mockISBN");
    }

    /**
     * Creates a mock book for the mock booklist
     * @return
     *      Book
     */
    private Book mockBook() {
        BookDescription description = mockDescription();
        User userId = mockUser();
        String bookID = "mockId";
        return new Book(bookID, userId, description);
    }

    /**
     * Test to add a Book to BookList
     */
    @Test
    void testAdd() {
        // create a booklist
        BookList bookList = mockBookList();
        // create a book with a description
        Book book = mockBook();
        // add to the list
        bookList.add(book);

        // check if it exists in the list
        assertEquals(1, bookList.getBookList().size());
        assertTrue(bookList.getBookList().contains(book));
    }

    /**
     * Checks if exception is thrown
     *  for a book that already exists
     */
    @Test
    void testAddException() {
        BookList bookList = mockBookList();
        // create a real book
        Book book = mockBook();
        // create a fake book with the same attributes
        Book fakeBook = mockBook();

        //add both books
        bookList.add(book);
        bookList.add(fakeBook);

        assertThrows(IllegalArgumentException.class, () -> {
            bookList.add(fakeBook);
        });
    }




}
