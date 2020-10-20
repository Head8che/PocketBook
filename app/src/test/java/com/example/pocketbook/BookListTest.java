package com.example.pocketbook;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookListTest {

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
        return new Book(userId, description);
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
        final BookList bookList = mockBookList();
        final Book book = mockBook();

        bookList.add(book);
        bookList.add(book);
        // TODO: why is this error occurring?
        assertThrows(IllegalArgumentException.class, () -> {
            bookList.add(book);
        });

    }




}
