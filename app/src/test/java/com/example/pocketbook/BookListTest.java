//package com.example.pocketbook;
//
//
//import com.example.pocketbook.model.Book;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class BookListTest {
//
//    private static final String TAG = "DEBUG";
//
//    /**
//     * Create a mock User
//     * @return
//     *      User
//     */
//    private User mockUser() {
//        return new User("mockUserId");
//    }
//
//    /**
//     * Create an empty mock booklist and return
//     * @return
//     *      Booklist
//     */
//    private BookList mockBookList() {
//        return new BookList();
//    }
//
//
//    /**
//     * Creates a mock book for the mock booklist
//     * @return
//     *      Book
//     */
//    private Book mockBook() {
//        User userId = mockUser();
//        String bookID = "mockId";
//
//        return new Book(bookID, "mockTitle", "mockAuthor",
//                "mockISBN", userId);
//    }
//
//
//    /**
//     * Test to add a Book to BookList
//     */
//    @Test
//    void testAdd() {
//        // create a booklist
//        BookList bookList = mockBookList();
//        // create a book with a description
//        Book book = mockBook();
//        // add to the list
//        bookList.add(book);
//
//        // check if it exists in the list
//        assertEquals(1, bookList.getBookList().size());
//        assertTrue(bookList.getBookList().contains(book));
//    }
//
//    /**
//     * Checks if exception is thrown
//     *  for a book that already exists
//     */
//    @Test
//    void testAddException() {
//        BookList bookList = mockBookList();
//        // create a real book
//        Book book = mockBook();
//        // create a fake book with the same attributes
//        Book fakeBook = mockBook();
//
//        //add both books
//        bookList.add(book);
//        bookList.add(fakeBook);
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            bookList.add(fakeBook);
//        });
//    }
//
//    /**
//     * Checks if an existing specified book is
//     * deleted from the booklist.
//     */
//    @Test
//    void testDelete() {
//        BookList bookList = mockBookList();
//        Book book = mockBook();
//        bookList.add(book);
//        bookList.delete(book);
//        assertEquals(0, bookList.getBookList().size());
//    }
//
//    /**
//     * Check an Exception is thrown if
//     * a book that does not exist is attempted to be deleted
//     */
//    @Test
//    void testDeleteException() {
//        // assert that an Exception is thrown if the book does not exist
//        BookList bookList = mockBookList();
//        Book book = mockBook();
//        assertThrows(IllegalArgumentException.class, () -> {
//            bookList.delete(book);
//        });
//
//    }
//
//
//}
