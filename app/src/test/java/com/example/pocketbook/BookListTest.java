package com.example.pocketbook;


import android.app.Instrumentation;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class BookListTest {

    private static final String TAG = "DEBUG";
    private FirebaseFirestore mockedDatabaseReference;

    @Before
    public void before() {
//        FirebaseApp.initializeApp();
//
//        FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
//        Mockito.when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);
//
//        BookList interactor = new BookList();
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
     * Creates a mock book for the mock booklist
     * @return
     *      Book
     */
    private Book mockBook() {
        String bookID = "mockId";

        return new Book(bookID, "mockTitle", "mockAuthor",
                "mockISBN", "booklisttest@email.com", "available");
    }


    /**
     * Test to add a Book to BookList
     */
    @Test
    void testAddBook() {
        // create a bookList
        BookList bookList = mockBookList();

        // create a book with a description
        Book book = mockBook();

        // add to the list
        bookList.addBook(book);

        // check if it exists in the list
        assertEquals(1, bookList.getSize());
        assertTrue(bookList.containsBook(book));

        // check if it exists in Firebase
//        FirebaseApp.initializeApp(Context);
//        FirebaseFirestore.getInstance().collection("books").document(book.getId())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                assertTrue(task.isSuccessful() && task.getResult().exists());
//
//                // clear mockBook data from list and Firebase (in onComplete b/c async)
//                bookList.clear();
//            }
//        });
    }

    /**
     *  Check if an invalid add, for a book that
     *  already exists, is prevented
     */
    @Test
    void testInvalidAddBook() {
        BookList bookList = mockBookList();

        // create a real book
        Book book = mockBook();

        // create a fake book with the same attributes
        Book fakeBook = mockBook();

        // add valid book
        bookList.addBook(book);

        // adding book with the same values should be false
        assertFalse(bookList.addBook(fakeBook));

        // should still only be one book in list
        assertEquals(1, bookList.getSize());

        // clear mockBook data from Firebase
        bookList.clear();
    }

    /**
     * Check if an existing specified book is
     * removed from the booklist.
     */
    @Test
    void testRemoveBook() {
        BookList bookList = mockBookList();
        Book book = mockBook();
        bookList.addBook(book);
        bookList.removeBook(book);
        assertEquals(0, bookList.getBookList().size());

//        FirebaseFirestore.getInstance().collection("books").document(book.getId())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                // make sure it doesn't exist in Firebase
//                assertFalse(task.isSuccessful());
//            }
//        });
    }

    /**
     *  Check if an invalid remove, for a book that
     *  does not exist, is prevented
     */
    @Test
    void testInvalidRemoveBook() {
        BookList bookList = mockBookList();

        // create a real book
        Book book = mockBook();

        // removing a book that hasn't been added should be false
        assertFalse(bookList.removeBook(book));

        // should still be no books in list
        assertEquals(0, bookList.getSize());

    }


}
