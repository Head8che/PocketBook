package com.example.pocketbook.util;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.Book;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

public class ParserTest {

    @Before
    public void setUp(){
        /* One option for dealing with testing & Firebase is to have a guard, like below,
        * that toggles a variable's value. All code that engages with Firebase could then be
        * wrapped in an if-statement as such: if guard is on, do not call Firebase. In this case,
        * we would not need to mock Firebase because we would avoid engaging with it all-together. */
        Parser.turnOffFirebaseChecks();

        /* The second option is to mock Firebase. This would involve us passing a FirebaseFirestore
        * (and perhaps FirebaseAuth for Login/SignUp) object into each place that interacts with
        * Firebase. We would then need to pass a mock object into our classes when we test them. */
        DocumentReference documentReference;
        FirebaseFirestore firebaseFirestore;

        FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
        CollectionReference catalogueReference = Mockito.mock(CollectionReference.class);
        DocumentReference bookReference = Mockito.mock(DocumentReference.class);
        CollectionReference requestsReference = Mockito.mock(CollectionReference.class);
        Task taskReference = Mockito.mock(Task.class);

        Mockito.when(mockFirestore.collection("catalogue")).thenReturn(catalogueReference);
        Mockito.when(mockFirestore.collection("catalogue").document("bookID")).thenReturn(bookReference);
        Mockito.when(mockFirestore.collection("catalogue").document("bookID")
                .collection("requests")).thenReturn(requestsReference);
        Mockito.when(mockFirestore.collection("catalogue").document("bookID")
                .collection("requests").get()).thenReturn(taskReference);

//        Class instance = new Class(mockFirestore, ...);
    }

    @Test
    public void testIsValidId() {
        String id;
        assertFalse(Parser.isValidId(null));  // id cannot be null

        id = "";  // valid new book id
        assertTrue(Parser.isValidId(id));

        id = "iTOSUu7PbMyjXOgkYmNm";  // locally valid id, not in Firebase
        assertTrue(Parser.isValidId(id));
    }

    @Test
    public void testIsValidTitle() {
        String title;
        assertFalse(Parser.isValidTitle(null));  // title cannot be null

        title = "";  // invalid title
        assertFalse(Parser.isValidTitle(title));

        title = "mockBookTitle";  // valid title
        assertTrue(Parser.isValidTitle(title));
    }

    @Test
    public void testIsValidAuthor() {
        String author;
        assertFalse(Parser.isValidAuthor(null));  // author cannot be null

        author = "";  // invalid author
        assertFalse(Parser.isValidAuthor(author));

        author = "mockBookAuthor";  // valid author
        assertTrue(Parser.isValidAuthor(author));
    }

    @Test
    public void testIsValidOwner() {
        String owner;
        assertFalse(Parser.isValidOwner(null));  // owner cannot be null

        owner = "";  // invalid empty string owner
        assertFalse(Parser.isValidOwner(owner));

        owner = "Mock@mock.com";  // invalid non-lowercase owner
        assertFalse(Parser.isValidOwner(owner));

        owner = "mock@mock.com";  // valid owner (all lowercase)
        assertTrue(Parser.isValidOwner(owner));
    }

    @Test
    public void testIsValidStatus() {
        String status;
        assertFalse(Parser.isValidStatus(null));  // status cannot be null

        status = "";  // invalid empty string status
        assertFalse(Parser.isValidStatus(status));

        status = "Available";  // invalid non-uppercase status
        assertFalse(Parser.isValidStatus(status));

        status = "AVAILABLE";  // valid status (all uppercase)
        assertTrue(Parser.isValidStatus(status));

        status = "REQUESTED";  // valid status (all uppercase)
        assertTrue(Parser.isValidStatus(status));

        status = "ACCEPTED";  // valid status (all uppercase)
        assertTrue(Parser.isValidStatus(status));

        status = "BORROWED";  // valid status (all uppercase)
        assertTrue(Parser.isValidStatus(status));
    }

    @Test
    public void testIsValidCondition() {
        String condition;
        assertFalse(Parser.isValidCondition(null));  // condition cannot be null

        condition = "";  // invalid empty string condition
        assertFalse(Parser.isValidCondition(condition));

        condition = "GO0D";  // invalid non-uppercase condition
        assertFalse(Parser.isValidCondition(condition));

        condition = "GREAT";  // valid condition (all uppercase)
        assertTrue(Parser.isValidCondition(condition));

        condition = "GOOD";  // valid condition (all uppercase)
        assertTrue(Parser.isValidCondition(condition));

        condition = "FAIR";  // valid condition (all uppercase)
        assertTrue(Parser.isValidCondition(condition));

        condition = "ACCEPTABLE";  // valid condition (all uppercase)
        assertTrue(Parser.isValidCondition(condition));
    }

    @Test
    public void testIsValidPhoto() {
        String photo;
        assertFalse(Parser.isValidPhoto(null));  // photo cannot be null

        photo = "photoString";  // invalid photo string (no .jpg)
        assertFalse(Parser.isValidPhoto(photo));

        photo = ".jpg";  // invalid photo string (nothing before .jpg)
        assertFalse(Parser.isValidPhoto(photo));

        photo = "";  // valid empty string photo
        assertTrue(Parser.isValidPhoto(photo));

        photo = "965iry5f-474f-4cf0-91aa-fa76e6a1b4b8.jpg";  // valid photo string
        assertTrue(Parser.isValidPhoto(photo));
    }

    @Test
    public void testIsNotDigit() {
        String text;
        assertTrue(Parser.isNotDigit(null));  // null is NaN

        text = "";  // "" is NaN
        assertTrue(Parser.isNotDigit(text));

        text = "textString";  // "textString" is NaN
        assertTrue(Parser.isNotDigit(text));

        text = ".jpg";  // ".jpg" is NaN
        assertTrue(Parser.isNotDigit(text));

        text = "965.";  // "965." is NaN
        assertTrue(Parser.isNotDigit(text));

        text = "6583";  // "6583" IS A number
        assertFalse(Parser.isNotDigit(text));
    }

    @Test
    public void testIsValidIsbn10() {
        String isbn;
        assertFalse(Parser.isValidIsbn10(null));  // isbn cannot be null

        isbn = "";  // invalid empty string condition
        assertFalse(Parser.isValidIsbn10(isbn));

        isbn = "1234";  // valid digits but invalid length condition
        assertFalse(Parser.isValidIsbn10(isbn));

        isbn = "isbnString";  // valid length but NaN condition
        assertFalse(Parser.isValidIsbn10(isbn));

        isbn = "0136091812";  // valid length & digits, but invalid isbn condition
        assertFalse(Parser.isValidIsbn10(isbn));

        isbn = "123456789X";  // valid isbn with X condition
        assertTrue(Parser.isValidIsbn10(isbn));

        isbn = "0136091814";  // valid isbn condition
        assertTrue(Parser.isValidIsbn10(isbn));
    }

    @Test
    public void GetIsbn13CheckBit() {
        String isbn;
        assertNull(Parser.getIsbn13CheckBit(null));  // isbn cannot be null

        isbn = "";  // invalid empty string condition
        assertNull(Parser.getIsbn13CheckBit(isbn));

        isbn = "0136091814";  // valid digits but invalid length condition
        assertNull(Parser.getIsbn13CheckBit(isbn));

        isbn = "isbnString123";  // valid length but NaN condition
        assertNull(Parser.getIsbn13CheckBit(isbn));

        isbn = "9870136091813";  // valid length & digits, but invalid isbn condition
        assertNull(Parser.getIsbn13CheckBit(isbn));

        isbn = "9780136091817";  // invalid isbn but valid first 12 digits condition
        assertEquals("3", Parser.getIsbn13CheckBit(isbn));

        isbn = "978013609181";  // valid 12 digit condition
        assertEquals("3", Parser.getIsbn13CheckBit(isbn));

        isbn = "9780136091813";  // valid 13 digit condition
        assertEquals("3", Parser.getIsbn13CheckBit(isbn));
    }

    @Test
    public void testIsValidIsbn13() {
        String isbn;
        assertFalse(Parser.isValidIsbn13(null));  // isbn cannot be null

        isbn = "";  // invalid empty string condition
        assertFalse(Parser.isValidIsbn13(isbn));

        isbn = "0136091814";  // valid digits but invalid length condition
        assertFalse(Parser.isValidIsbn13(isbn));

        isbn = "isbnString123";  // valid length but NaN condition
        assertFalse(Parser.isValidIsbn13(isbn));

        isbn = "9780136091817";  // valid length & digits, but invalid isbn condition
        assertFalse(Parser.isValidIsbn13(isbn));

        isbn = "9780136091813";  // valid isbn condition
        assertTrue(Parser.isValidIsbn13(isbn));
    }

    @Test
    public void ConvertToIsbn13() {
        String isbn;
        assertNull(Parser.convertToIsbn13(null));  // isbn cannot be null

        isbn = "";  // invalid empty string condition
        assertNull(Parser.convertToIsbn13(isbn));

        isbn = "01360918148";  // valid digits but invalid length condition
        assertNull(Parser.convertToIsbn13(isbn));

        isbn = "isbnString123";  // valid length but NaN condition
        assertNull(Parser.convertToIsbn13(isbn));

        isbn = "0136091812";  // invalid isbn10 condition
        assertNull(Parser.convertToIsbn13(isbn));

        isbn = "9780136091817";  // invalid isbn13 condition
        assertNull(Parser.convertToIsbn13(isbn));

        isbn = "123456789X";  // valid isbn10 condition
        assertEquals("9781234567897", Parser.convertToIsbn13(isbn));

        isbn = "9780136091813";  // valid isbn13 condition
        assertEquals("9780136091813", Parser.convertToIsbn13(isbn));
    }

    @Test
    public void testParseBook() {
        String id = "";  // valid new book id
        String title = "mockBookTitle";
        String author = "mockBookAuthor";
        String isbn = "9781861972712";  // valid isbn
        String owner = "mock@mock.com";  // locally valid owner, not in Firebase
        String status = "AVAILABLE";
        String comment = "This is a mock book.";
        String condition = "GOOD";
        String photo = "";

        // assert that Parser returns a Book i.e. assert that input was valid
        assertThat(Parser.parseBook(id, title, author, isbn, owner,
                status, comment, condition, photo), instanceOf(Book.class));

        isbn = "123456789X";  // valid isbn10 condition
        // assert that Parser returns a Book i.e. assert that input was valid
        assertThat(Parser.parseBook(id, title, author, isbn, owner,
                status, comment, condition, photo), instanceOf(Book.class));

        /* id is commented out because this test fails since Firebase is not avoided or mocked */
//        id = "FRo3Rn4iaIHD04qOej";  // valid id condition
        // assert that Parser returns a Book i.e. assert that input was valid
        assertThat(Parser.parseBook(id, title, author, isbn, owner,
                status, comment, condition, photo), instanceOf(Book.class));

        // assert that Parser fails with bad data
        assertNull(Parser.parseBook(id, title, author, isbn, owner,
                status, comment, condition, "jpg"));

        // Parser.ParseBook(...) is based on the other Parser Book methods,
        // which have been tested, so further argument testing would be redundant.
    }
}
