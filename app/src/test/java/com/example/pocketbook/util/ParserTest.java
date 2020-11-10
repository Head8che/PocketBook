package com.example.pocketbook.util;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
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

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

public class ParserTest {

    @Before
    public void setUp(){
        /* One option for dealing with testing & Firebase is to have a guard in the Model classes
        * primarily that toggles a variable's value. All code that engages with Firebase could then
        * be wrapped in an if-statement as such: if guard is on, do not call Firebase. In this case,
        * we would not need to mock Firebase because we would avoid engaging with it all-together. */

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
    public void testIsValidNewBookId() {
        String id;
        assertFalse(Parser.isValidNewBookId(null));  // id cannot be null

        id = "iTOSUu7PbMyjXOgkYmNm";  // new book cannot have a non-empty id
        assertFalse(Parser.isValidNewBookId(id));

        id = "";  // valid new book id
        assertTrue(Parser.isValidNewBookId(id));
    }

    @Test
    public void testIsValidBookId() {
        String id;
        assertFalse(Parser.isValidBookId(null));  // id cannot be null

        id = "";  // invalid book id
        assertFalse(Parser.isValidBookId(id));

        id = "iTOSUu7PbMyjXOgkYmNm";  // locally valid id, not in Firebase
        assertTrue(Parser.isValidBookId(id));
    }

    @Test
    public void testIsValidBookTitle() {
        String title;
        assertFalse(Parser.isValidBookTitle(null));  // title cannot be null

        title = "";  // invalid title
        assertFalse(Parser.isValidBookTitle(title));

        title = "mockBookTitle";  // valid title
        assertTrue(Parser.isValidBookTitle(title));
    }

    @Test
    public void testIsValidBookAuthor() {
        String author;
        assertFalse(Parser.isValidBookAuthor(null));  // author cannot be null

        author = "";  // invalid author
        assertFalse(Parser.isValidBookAuthor(author));

        author = "mockBookAuthor";  // valid author
        assertTrue(Parser.isValidBookAuthor(author));
    }

    @Test
    public void testIsValidBookOwner() {
        String owner;
        assertFalse(Parser.isValidBookOwner(null));  // owner cannot be null

        owner = "";  // invalid empty string owner
        assertFalse(Parser.isValidBookOwner(owner));

        owner = "Mock@mock.com";  // invalid non-lowercase owner
        assertFalse(Parser.isValidBookOwner(owner));

        owner = "mock@mock.com";  // valid owner (all lowercase)
        assertTrue(Parser.isValidBookOwner(owner));
    }

    @Test
    public void testIsValidBookStatus() {
        String status;
        assertFalse(Parser.isValidBookStatus(null));  // status cannot be null

        status = "";  // invalid empty string status
        assertFalse(Parser.isValidBookStatus(status));

        status = "Available";  // invalid non-uppercase status
        assertFalse(Parser.isValidBookStatus(status));

        status = "AVAILABLE";  // valid status (all uppercase)
        assertTrue(Parser.isValidBookStatus(status));

        status = "REQUESTED";  // valid status (all uppercase)
        assertTrue(Parser.isValidBookStatus(status));

        status = "ACCEPTED";  // valid status (all uppercase)
        assertTrue(Parser.isValidBookStatus(status));

        status = "BORROWED";  // valid status (all uppercase)
        assertTrue(Parser.isValidBookStatus(status));
    }

    @Test
    public void testIsValidBookCondition() {
        String condition;
        assertFalse(Parser.isValidBookCondition(null));  // condition cannot be null

        condition = "";  // invalid empty string condition
        assertFalse(Parser.isValidBookCondition(condition));

        condition = "GO0D";  // invalid non-uppercase condition
        assertFalse(Parser.isValidBookCondition(condition));

        condition = "GREAT";  // valid condition (all uppercase)
        assertTrue(Parser.isValidBookCondition(condition));

        condition = "GOOD";  // valid condition (all uppercase)
        assertTrue(Parser.isValidBookCondition(condition));

        condition = "FAIR";  // valid condition (all uppercase)
        assertTrue(Parser.isValidBookCondition(condition));

        condition = "ACCEPTABLE";  // valid condition (all uppercase)
        assertTrue(Parser.isValidBookCondition(condition));
    }

    @Test
    public void testIsValidBookPhoto() {
        String photo;
        assertFalse(Parser.isValidBookPhoto(null));  // photo cannot be null

        photo = "photoString";  // invalid photo string (no .jpg)
        assertFalse(Parser.isValidBookPhoto(photo));

        photo = ".jpg";  // invalid photo string (nothing before .jpg)
        assertFalse(Parser.isValidBookPhoto(photo));

        photo = "";  // valid empty string photo
        assertTrue(Parser.isValidBookPhoto(photo));

        photo = "965iry5f-474f-4cf0-91aa-fa76e6a1b4b8.jpg";  // valid photo string
        assertTrue(Parser.isValidBookPhoto(photo));
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
    public void testParseNewBook() {
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
        assertThat(Parser.parseNewBook(id, title, author, isbn, owner,
                status, comment, condition, photo), instanceOf(Book.class));

        isbn = "123456789X";  // valid isbn10 condition
        // assert that Parser returns a Book i.e. assert that input was valid
        assertThat(Parser.parseNewBook(id, title, author, isbn, owner,
                status, comment, condition, photo), instanceOf(Book.class));

        // assert that Parser fails with bad data
        assertNull(Parser.parseNewBook("FRo3Rn4iaIHD04qOej", title, author, isbn, owner,
                status, comment, condition, photo));

        // assert that Parser fails with bad data
        assertNull(Parser.parseNewBook(id, title, author, isbn, owner,
                status, comment, condition, "jpg"));

        // Parser.ParseBook(...) is based on the other Parser Book methods,
        // which have been tested, so further argument testing would be redundant.
    }

    @Test
    public void testParseBook() {
        String id = "bookID";  // valid book id
        String title = "mockBookTitle";
        String author = "mockBookAuthor";
        String isbn = "9781861972712";  // valid isbn
        String owner = "mock@mock.com";  // locally valid owner, not in Firebase
        String status = "AVAILABLE";
        String comment = "This is a mock book.";
        String condition = "GOOD";
        String photo = "";

        /* tests are commented out because they fail
        since Firebase is not avoided or mocked in Book.java */
//
//        // assert that Parser returns a Book i.e. assert that input was valid
//        assertThat(Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, photo), instanceOf(Book.class));
//
//        isbn = "123456789X";  // valid isbn10 condition
//        // assert that Parser returns a Book i.e. assert that input was valid
//        assertThat(Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, photo), instanceOf(Book.class));
//
//        // assert that Parser returns a Book i.e. assert that input was valid
//        assertThat(Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, photo), instanceOf(Book.class));
//
//        // assert that Parser fails with bad data
//        assertNull(Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, "jpg"));
//
//        // Parser.ParseBook(...) is based on the other Parser Book methods,
//        // which have been tested, so further argument testing would be redundant.
    }

    @Test
    public void testIsValidFirstName() {
        String firstName;
        assertFalse(Parser.isValidFirstName(null));  // firstName cannot be null

        firstName = "";  // invalid firstName
        assertFalse(Parser.isValidFirstName(firstName));

        firstName = "mockFirstName";  // valid firstName
        assertTrue(Parser.isValidFirstName(firstName));
    }

    @Test
    public void testIsValidLastName() {
        String lastName;
        assertFalse(Parser.isValidLastName(null));  // lastName cannot be null

        lastName = "";  // invalid lastName
        assertFalse(Parser.isValidLastName(lastName));

        lastName = "mockLastName";  // valid lastName
        assertTrue(Parser.isValidLastName(lastName));
    }

    @Test
    public void testIsValidUserEmail() {
        String email;
        assertFalse(Parser.isValidUserEmail(null));  // email cannot be null

        email = "";  // invalid empty string email
        assertFalse(Parser.isValidUserEmail(email));

        email = "Mock@mock.com";  // invalid non-lowercase email
        assertFalse(Parser.isValidUserEmail(email));

        email = "mock@mock.com";  // valid email (all lowercase)
        assertTrue(Parser.isValidUserEmail(email));
    }

    @Test
    public void testIsValidUsername() {
        String username;
        assertFalse(Parser.isValidUsername(null));  // username cannot be null

        username = "";  // invalid username
        assertFalse(Parser.isValidUsername(username));

        username = "mockUsername";  // valid username
        assertTrue(Parser.isValidUsername(username));
    }

    @Test
    public void testIsValidPassword() {
        String password;
        assertFalse(Parser.isValidPassword(null));  // password cannot be null

        password = "";  // invalid empty string password
        assertFalse(Parser.isValidPassword(password));

        password = "12345";  // invalid length < 6 password
        assertFalse(Parser.isValidPassword(password));

        password = "mockPassword";  // valid password
        assertTrue(Parser.isValidPassword(password));
    }

    @Test
    public void testIsValidUserPhoto() {
        String photo;
        assertFalse(Parser.isValidUserPhoto(null));  // photo cannot be null

        photo = "photoString";  // invalid photo string (no .jpg)
        assertFalse(Parser.isValidUserPhoto(photo));

        photo = ".jpg";  // invalid photo string (nothing before .jpg)
        assertFalse(Parser.isValidUserPhoto(photo));

        photo = "";  // valid empty string photo
        assertTrue(Parser.isValidUserPhoto(photo));

        photo = "123iry5f-474f-4cf0-91aa-fa76e6a1b4b8.jpg";  // valid photo string
        assertTrue(Parser.isValidUserPhoto(photo));
    }

    @Test
    public void testIsValidOwnedBooksList() {
        ArrayList<String> ownedBooks = new ArrayList<>();
        assertFalse(Parser.isValidOwnedBooksList(null));  // cannot be null

        ownedBooks.add("");  // invalid ownedBooks with empty bookID
        assertFalse(Parser.isValidOwnedBooksList(ownedBooks));

        ownedBooks.add("icOSUu7PbMyjXOgkYmNm");  // invalid ownedBooks with valid and empty bookID
        assertFalse(Parser.isValidOwnedBooksList(ownedBooks));

        ownedBooks.remove("");  // valid ownedBooks with one valid bookID
        assertTrue(Parser.isValidOwnedBooksList(ownedBooks));

        ownedBooks.add("XVcSUu7PbPoeXOgkYmNp");  // valid ownedBooks with two valid bookIDs
        assertTrue(Parser.isValidOwnedBooksList(ownedBooks));
    }

    @Test
    public void testIsValidRequestedBooksList() {
        ArrayList<String> requestedBooks = new ArrayList<>();
        assertFalse(Parser.isValidRequestedBooksList(null));  // cannot be null

        requestedBooks.add("");  // invalid requestedBooks with empty bookID
        assertFalse(Parser.isValidRequestedBooksList(requestedBooks));

        requestedBooks.add("icOSUu7PbMyjXOgkYmNm");  // invalid requestedBooks with valid and empty bookID
        assertFalse(Parser.isValidRequestedBooksList(requestedBooks));

        requestedBooks.remove("");  // valid requestedBooks with one valid bookID
        assertTrue(Parser.isValidRequestedBooksList(requestedBooks));

        requestedBooks.add("XVcSUu7PbPoeXOgkYmNp");  // valid requestedBooks with two valid bookIDs
        assertTrue(Parser.isValidRequestedBooksList(requestedBooks));
    }

    @Test
    public void testIsValidAcceptedBooksList() {
        ArrayList<String> acceptedBooks = new ArrayList<>();
        assertFalse(Parser.isValidAcceptedBooksList(null));  // cannot be null

        acceptedBooks.add("");  // invalid acceptedBooks with empty bookID
        assertFalse(Parser.isValidAcceptedBooksList(acceptedBooks));

        acceptedBooks.add("icOSUu7PbMyjXOgkYmNm");  // invalid acceptedBooks with valid and empty bookID
        assertFalse(Parser.isValidAcceptedBooksList(acceptedBooks));

        acceptedBooks.remove("");  // valid acceptedBooks with one valid bookID
        assertTrue(Parser.isValidAcceptedBooksList(acceptedBooks));

        acceptedBooks.add("XVcSUu7PbPoeXOgkYmNp");  // valid acceptedBooks with two valid bookIDs
        assertTrue(Parser.isValidAcceptedBooksList(acceptedBooks));
    }

    @Test
    public void testIsValidBorrowedBooksList() {
        ArrayList<String> borrowedBooks = new ArrayList<>();
        assertFalse(Parser.isValidBorrowedBooksList(null));  // cannot be null

        borrowedBooks.add("");  // invalid borrowedBooks with empty bookID
        assertFalse(Parser.isValidBorrowedBooksList(borrowedBooks));

        borrowedBooks.add("icOSUu7PbMyjXOgkYmNm");  // invalid borrowedBooks with valid and empty bookID
        assertFalse(Parser.isValidBorrowedBooksList(borrowedBooks));

        borrowedBooks.remove("");  // valid borrowedBooks with one valid bookID
        assertTrue(Parser.isValidBorrowedBooksList(borrowedBooks));

        borrowedBooks.add("XVcSUu7PbPoeXOgkYmNp");  // valid borrowedBooks with two valid bookIDs
        assertTrue(Parser.isValidBorrowedBooksList(borrowedBooks));
    }

    @Test
    public void testParseUser() {
        String firstName = "Joey";
        String lastName = "Monday";
        String email = "joey@monday.com";  // locally valid email, not in Firebase
        String username = "joemon";
        String password = "123456";
        String photo = "";

        // assert that Parser returns a User i.e. assert that input was valid
        assertThat(Parser.parseUser(firstName, lastName, email, username,
                password, photo), instanceOf(User.class));

        photo = "photo.jpg";  // valid photo condition
        // assert that Parser returns a User i.e. assert that input was valid
        assertThat(Parser.parseUser(firstName, lastName, email, username,
                password, photo), instanceOf(User.class));

        username = "";
        // assert that Parser fails with bad data
        assertNull(Parser.parseUser(firstName, lastName, email, username,
                password, photo));

        // Parser.ParseUser(...) is based on the other Parser User methods,
        // which have been tested, so further argument testing would be redundant.
    }
}
