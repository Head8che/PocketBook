package com.example.pocketbook.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Exchange;
import com.example.pocketbook.model.MeetingDetails;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FirebaseIntegrity {

    /*
      FIREBASE HAS THE FOLLOWING RETURN VALUES FOR INVALID QUERY RESULTS:
        INVALID_COLLECTION:
          - FirebaseFirestore.getInstance().collection(INVALID_COLLECTION).get()
          - RETURN: onComplete --> task.isSuccessful() & task.getResult().isEmpty()
        INVALID_DOCUMENT:
          - FirebaseFirestore.getInstance().collection(COLLECTION).document(INVALID_DOCUMENT).get()
          - RETURN: onComplete --> task.isSuccessful() & !(task.getResult().exists())
        INVALID_FIELD:
          - FirebaseFirestore.getInstance().collection(COLLECTION).document(VALID_DOCUMENT).get()
          - RETURN: onComplete --> task.isSuccessful() & task.getResult().exists()
                & null field i.e. task.getResult().getString(INVALID_FIELD) = null
     */

    ////////////////////////////// FIREBASE METHODS FOR THE BOOK MODEL /////////////////////////////

    /**
     * Getter method for BookCover
     * @return
     *      StorageReference to image
     */
    public static StorageReference getBookCover(Book book) {
        String bookPhoto = book.getPhoto();

        // return the image of a valid book photo
        if (Parser.isValidBookPhoto(bookPhoto) && (!(bookPhoto.equals("")))) {
            return FirebaseStorage.getInstance()
                    .getReference().child("book_covers").child(bookPhoto);
        }
        // return the default image if book photo is invalid
        return FirebaseStorage.getInstance()
                .getReference().child("default_images").child("no_book_cover_light.png");
    }

    /**
     * Sets the cover of the book as an image
     * if the url is a local file
     * @param localURL : url of the book
     */
    public static void setBookCover(Book book, String localURL) {
        if(localURL != null) {

            String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

            StorageReference childRef = FirebaseStorage.getInstance()
                    .getReference().child("book_covers").child(photoName);

            if (localURL.equals("REMOVE")) {
                FirebaseStorage.getInstance()
                        .getReference().child("book_covers")
                        .child(book.getPhoto())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("REMOVE_BOOK_COVER",
                                        "Book data successfully written!");
                                FirebaseIntegrity.setBookPhotoFirebase(book, "");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("REMOVE_BOOK_COVER", "Error writing book data!");
                            }
                        });
                return;
            }

            //uploading the image
            UploadTask uploadTask = childRef.putFile(Uri.fromFile(new File(localURL)));

            Log.e("SET_BOOK_COVER", "After parse!");

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_BOOK_COVER", "Successful upload!");
                    FirebaseIntegrity.setBookPhotoFirebase(book, photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("SET_BOOK_COVER", "Failed upload!");
                }
            });
        }
    }

    /**
     * Sets the cover of the book
     * if the argument is a bitmap file
     * @param bitmap : photo of book
     */
    public static void setBookCoverBitmap(Book book, Bitmap bitmap) {

        String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

        if(bitmap != null) {

            StorageReference childRef = FirebaseStorage.getInstance()
                    .getReference().child("book_covers").child(photoName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //uploading the image
            UploadTask uploadTask = childRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_BOOK_COVER", "Successful upload!");
                    FirebaseIntegrity.setBookPhotoFirebase(book, photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("SET_BOOK_COVER", "Failed upload!");
                }
            });
        }
    }

    /**
     * sets book title in Firebase
     * @param book related book
     * @param title book title
     */
    public static void setBookTitleFirebase(Book book, String title) {
        if (Parser.isValidBookTitle(title)) {
            setBookDataFirebase(book, "title", title);
        }
    }

    /**
     * sets book author in Firebase
     * @param book related book
     * @param author book author
     */
    public static void setBookAuthorFirebase(Book book, String author) {
        if (Parser.isValidBookAuthor(author)) {
            setBookDataFirebase(book, "author", author);
        }
    }

    /**
     * sets book isbn in Firebase
     * @param book related book
     * @param isbn book isbn
     */
    public static void setBookIsbnFirebase(Book book, String isbn) {
        if (Parser.isValidBookIsbn(isbn)) {
            setBookDataFirebase(book, "isbn", Parser.convertToIsbn13(isbn));
        }
    }

    /**
     * sets book comment in Firebase
     * @param book related book
     * @param comment book comment
     */
    public static void setBookCommentFirebase(Book book, String comment) {
        if (Parser.isValidBookComment(comment)) {
            setBookDataFirebase(book, "comment", comment);
        }
    }

    /**
     * sets book condition in Firebase
     * @param book related book
     * @param condition book condition
     */
    public static void setBookConditionFirebase(Book book, String condition) {
        if (Parser.isValidBookCondition(condition)) {
            setBookDataFirebase(book, "condition", condition);
        }
    }

    /**
     * sets book status in Firebase
     * @param book related book
     * @param status book status
     */
    public static void setBookStatusFirebase(Book book, String status) {
        if (Parser.isValidBookStatus(status)) {
            setBookDataFirebase(book, "status", status);
        }
    }

    /**
     * sets book photo in Firebase
     * @param book related book
     * @param photo book photo
     */
    public static void setBookPhotoFirebase(Book book, String photo) {
        if (Parser.isValidBookPhoto(photo)) {
            setBookDataFirebase(book, "photo", photo);
        }
    }

    /**
     * sets book data in Firebase
     * @param book related book
     * @param bookFieldName book field to change
     * @param bookFieldValue value that book field should be changed to
     */
    public static void setBookDataFirebase(Book book, String bookFieldName, String bookFieldValue) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("catalogue")
                .document(book.getId());

        documentReference
                .update(bookFieldName, bookFieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SET_BOOK", "Book data successfully written!");
                        if (bookFieldName.equals("status")) {
                            if ((bookFieldValue.equals("AVAILABLE"))
                                    || (bookFieldValue.equals("REQUESTED"))) {
                                documentReference.update("nonExchange", true);
                            } else {
                                documentReference.update("nonExchange", false);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_BOOK", "Error writing book data!", e);
                    }
                });
    }

    /**
     * pushes new book to Firebase
     * @param newBook book object
     * @param localURL url of photo if it exists
     */
    public static void pushNewBookToFirebaseWithURL(Book newBook, String localURL) {

        if (Parser.isValidBookObject(newBook)) {

            String id = newBook.getId();
            String title = newBook.getTitle();
            String author = newBook.getAuthor();
            String isbn = newBook.getISBN();
            String owner = newBook.getOwner();
            String status = newBook.getStatus();
            String comment = newBook.getComment();
            String condition = newBook.getCondition();
            String photo = newBook.getPhoto();

            HashMap<String, Object> docData = new HashMap<>();
            docData.put("id", id);
            docData.put("title", title);
            docData.put("author", author);
            docData.put("isbn", Parser.convertToIsbn13(isbn));
            docData.put("owner", owner);
            docData.put("status", status);
            docData.put("comment", comment);
            docData.put("nonExchange", true);
            docData.put("condition", condition);
            docData.put("keywords", getBookKeywords(title, author, isbn));
            docData.put("requesters", new ArrayList<>());

            if ((localURL != null) && (!localURL.equals(""))) {
                FirebaseIntegrity.setBookCover(newBook, localURL);
            } else {
                docData.put("photo", photo);
            }

            FirebaseIntegrity.setDocumentFromObject("catalogue", id, docData);

        }

    }

    /**
     * pushes new book to Firebase
     * @param newBook book object
     * @param bitmap bitmap of photo is it exists
     */
    public static void pushNewBookToFirebaseWithBitmap(Book newBook, Bitmap bitmap) {

        if (Parser.isValidBookObject(newBook)) {

            String id = newBook.getId();
            String title = newBook.getTitle();
            String author = newBook.getAuthor();
            String isbn = newBook.getISBN();
            String owner = newBook.getOwner();
            String status = newBook.getStatus();
            String comment = newBook.getComment();
            String condition = newBook.getCondition();

            HashMap<String, Object> docData = new HashMap<>();
            docData.put("id", id);
            docData.put("title", title);
            docData.put("author", author);
            docData.put("isbn", Parser.convertToIsbn13(isbn));
            docData.put("owner", owner);
            docData.put("nonExchange", true);
            docData.put("status", status);
            docData.put("comment", comment);
            docData.put("condition", condition);
            docData.put("keywords", getBookKeywords(title, author, isbn));
            docData.put("requesters", new ArrayList<>());

            FirebaseIntegrity.setDocumentFromObject("catalogue", id, docData);

            if (bitmap != null) {
                FirebaseIntegrity.setBookCoverBitmap(newBook, bitmap);
            }

        }

    }

    /**
     * deletes book from Firebase
     * @param book book to delete
     */
    public static void deleteBookFirebase(Book book) {
        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .document(book.getId())
                .collection("requests")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        // if subcollection has documents
                        if (!Objects.requireNonNull(
                                task1.getResult()).isEmpty()) {

                            int numOfReturnedRows = task1.getResult().size();
                            int rowCount = 0;

                            // for each document in subcollection
                            for (DocumentSnapshot document1 : task1.getResult()) {

                                rowCount += 1;

                                if (document1.exists()) {

                                    // get an instance of the document and delete it
                                    int finalRowCount = rowCount;
                                    FirebaseFirestore.getInstance()
                                            .collection("catalogue")
                                            .document(book.getId())
                                            .collection("requests")
                                            .document(document1.getId())
                                            .delete()
                                            .addOnCompleteListener(task2 -> {
                                                if (!(task2.isSuccessful())) {
                                                    Log.e("DELETE_DOCUMENT_FROM_COLLECTION",
                                                            "Error deleting collection document!");
                                                } else {
                                                    if (finalRowCount == numOfReturnedRows) {
                                                        FirebaseFirestore.getInstance()
                                                                .collection("catalogue")
                                                                .document(book.getId())
                                                                .delete();

                                                        FirebaseFirestore.getInstance()
                                                                .collection("exchange")
                                                                .whereEqualTo("relatedBook", book.getId())
                                                                .get()
                                                                .addOnCompleteListener(task3 -> {
                                                                    if (task3.isSuccessful()) {
                                                                        Log.e("SIZE",
                                                                                String.valueOf(task3.getResult().size()));
                                                                        if (task3.getResult().size() > 0) {

                                                                            for (QueryDocumentSnapshot document2
                                                                                    : task3.getResult()) {

                                                                                FirebaseFirestore
                                                                                        .getInstance()
                                                                                        .collection("exchange")
                                                                                        .document(document2.getId())
                                                                                        .delete();

                                                                            }
                                                                        }
                                                                    }

                                                                });
                                                    }
                                                }
                                            });

                                }
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .document(book.getId())
                .delete()
                .addOnSuccessListener(aVoid
                        -> Log.e("DELETE_BOOK", "Book data successfully written!"))
                .addOnFailureListener(e
                        -> Log.e("DELETE_BOOK", "Error writing book data!"));
    }


    ////////////////////////////// FIREBASE METHODS FOR THE USER MODEL /////////////////////////////

    /**
     * Sets the cover of the user as an image
     * if the url is a local file
     * @param localURL : url of the user
     */
    public static void setUserProfilePicture(User user, String localURL) {
        if(localURL != null) {

            String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

            StorageReference childRef = FirebaseStorage.getInstance()
                    .getReference().child("profile_pictures").child(photoName);

            if (localURL.equals("REMOVE")) {
                FirebaseStorage.getInstance()
                        .getReference().child("profile_pictures")
                        .child(user.getPhoto())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("REMOVE_USER_PROFILE_PICTURE",
                                        "User data successfully written!");
                                FirebaseIntegrity.setUserPhotoFirebase(user, "");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("REMOVE_USER_PROFILE_PICTURE",
                                        "Error writing user data!");
                            }
                        });
                return;
            }

            //uploading the image
            UploadTask uploadTask = childRef.putFile(Uri.fromFile(new File(localURL)));

            Log.e("SET_USER_PROFILE_PICTURE", "After parse!");

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_USER_PROFILE_PICTURE", "Successful upload!");
                    FirebaseIntegrity.setUserPhotoFirebase(user, photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("SET_USER_PROFILE_PICTURE", "Failed upload!");
                }
            });
        }
    }

    /**
     * Sets the cover of the user
     * if the argument is a bitmap file
     * @param bitmap : photo of user
     */
    public static void setUserProfilePictureBitmap(User user, Bitmap bitmap) {

        String photoName = String.format("%s.jpg", UUID.randomUUID().toString());

        if(bitmap != null) {

            StorageReference childRef = FirebaseStorage.getInstance()
                    .getReference().child("profile_pictures").child(photoName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //uploading the image
            UploadTask uploadTask = childRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SET_USER_PROFILE_PICTURE", "Successful upload!");
                    FirebaseIntegrity.setUserPhotoFirebase(user, photoName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("SET_USER_PROFILE_PICTURE", "Failed upload!");
                }
            });
        }
    }

    /**
     * returns default photo for no uploaded image for user
     * @return
     */
    public static StorageReference getUserProfilePicture(User user) {
        String userPhoto = user.getPhoto();

        // return the image of a valid user photo
        if (Parser.isValidUserPhoto(userPhoto) && (!(userPhoto.equals("")))) {
            return FirebaseStorage.getInstance().getReference()
                    .child("profile_pictures").child(userPhoto);
        }
        // return the default image if user photo is invalid
        return FirebaseStorage.getInstance().getReference()
                .child("default_images").child("no_profileImg.png");
    }

    public static void setFirstNameFirebase(User user, String firstName) {
        if (Parser.isValidFirstName(firstName)) {
            setUserDataFirebase(user, "firstName", firstName);
        }
    }
    public static void setLastNameFirebase(User user, String lastName) {
        if (Parser.isValidLastName(lastName)) {
            setUserDataFirebase(user, "lastName", lastName);
        }
    }
    public static void setUsernameFirebase(User user, String username) {
        if (Parser.isValidUsername(username)) {
            setUserDataFirebase(user, "username", username);
        }
    }

    public static void setPhoneNumberFirebase(User user, String phoneNumber) {
        if (Parser.isValidPhoneNumber(phoneNumber)) {
            setUserDataFirebase(user, "phoneNumber", phoneNumber);
        }
    }public static void setUserPhotoFirebase(User user, String photo) {
        if (Parser.isValidUserPhoto(photo)) {
            setUserDataFirebase(user, "photo", photo);
        }
    }

    public static void setUserDataFirebase(User user, String userFieldName, String userFieldValue) {
        FirebaseFirestore.getInstance().collection("users").document(user.getEmail())
                .update(userFieldName, userFieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SET_USER", "User data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_USER", "Error writing user data!", e);
                    }
                });
    }

    public static void pushNewUserToFirebaseWithURL(User newUser, String localURL) {

        if (Parser.isValidUserObject(newUser)) {

            String firstName = newUser.getFirstName();
            String lastName = newUser.getLastName();
            String email = newUser.getEmail();
            String username = newUser.getUsername();
            String password = newUser.getPassword();
            String phoneNumber = newUser.getPhoneNumber();
            String photo = newUser.getPhoto();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e("CREATE_USER", "createUserWithEmail:success");

                                HashMap<String, Object> docData = new HashMap<>();
                                docData.put("firstName", firstName);
                                docData.put("lastName", lastName);
                                docData.put("email", email);
                                docData.put("username", username);
                                docData.put("password", password);
                                docData.put("phoneNumber", phoneNumber);
                                docData.put("photo", photo);

                                if ((localURL != null) && (!localURL.equals(""))) {
                                    FirebaseIntegrity.setUserProfilePicture(newUser, localURL);
                                } else {
                                    docData.put("photo", photo);
                                }

                                FirebaseIntegrity.setDocumentFromObject("users", email, docData);

                            } else {

                            }
                        }
                    });

        }

    }

    public static void pushNewUserToFirebaseWithBitmap(User newUser, Bitmap bitmap) {

        if (Parser.isValidUserObject(newUser)) {

            String firstName = newUser.getFirstName();
            String lastName = newUser.getLastName();
            String email = newUser.getEmail();
            String username = newUser.getUsername();
            String password = newUser.getPassword();
            String phoneNumber = newUser.getPhoneNumber();
            String photo = newUser.getPhoto();

            HashMap<String, Object> docData = new HashMap<>();
            docData.put("firstName", firstName);
            docData.put("lastName", lastName);
            docData.put("email", email);
            docData.put("username", username);
            docData.put("password", password);
            docData.put("photo", photo);
            docData.put("phoneNumber", phoneNumber);

            FirebaseIntegrity.setDocumentFromObject("users", email, docData);

            if (bitmap != null) {
                FirebaseIntegrity.setUserProfilePictureBitmap(newUser, bitmap);
            }

        }

    }


    ///////////////////////////////// FIREBASE METHODS FOR REQUESTS ////////////////////////////////

    public static void addBookRequest(Request request) {

        if (Parser.isValidRequestObject(request)) {

            String requestee = request.getRequestee();
            String requester = request.getRequester();
            String requestDate = request.getRequestDate();
            Book requestedBookObject = request.getRequestedBookObject();

            // if request is already in list
            if (requestedBookObject.getRequesters().contains(requester)) {
                return;
            }

            Map<String, Object> docData = new LinkedHashMap<>();
            docData.put("requestee", requestee);
            docData.put("requester", requester);
            docData.put("requestDate", requestDate);
            docData.put("requestedBook", requestedBookObject.getId());

            FirebaseFirestore.getInstance().collection("catalogue")
                    .document(requestedBookObject.getId())
                    .collection("requests").document(requester)
                    .set(docData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("NEW_REQUEST", "Request data successfully written!");
                            FirebaseIntegrity.setBookStatusFirebase(requestedBookObject,
                                    "REQUESTED");
                            FirebaseIntegrity.updateBookRequestersInCollection(
                                    "catalogue", requestedBookObject.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("NEW_REQUEST", "Error writing request data!");
                        }
                    });
        }
    }

    public static void acceptBookRequest(Request request) {

        if (Parser.isValidRequestWithBookIdObject(request)) {

            String requester = request.getRequester();
            String requestedBook = request.getRequestedBook();

            FirebaseFirestore.getInstance().collection("catalogue")
                    .document(requestedBook)
                    .collection("requests")
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {

                            // if collection has documents
                            if (!Objects.requireNonNull(task1.getResult()).isEmpty()) {

                                FirebaseIntegrity.makeBookStatusAccepted(request);

                                // for each document in collection
                                for (DocumentSnapshot document1 : task1.getResult()) {
                                    Log.e("PRE_DEL_REQUESTER", document1.getId()
                                            + " " + requester);
                                    if (document1.exists()
                                            && (!(document1.getId().equals(requester)))) {
                                        Log.e("DEL_REQUESTER", document1.getId()
                                                + " " + requester);
                                        FirebaseIntegrity.declineSpecificBookRequest(request,
                                                document1.getId(), true);
                                    }
                                }

                                FirebaseIntegrity.makeBookStatusAccepted(request);
                            }
                        }
                    });
        }
    }

    public static void makeBookStatusAccepted(Request request) {

        if (Parser.isValidRequestWithBookIdObject(request)) {

            String requestedBook = request.getRequestedBook();

            FirebaseFirestore.getInstance()
                    .collection("catalogue")
                    .document(requestedBook)
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {

                            DocumentSnapshot document = task2.getResult();

                            // if the document exists
                            if ((document != null) && (document.exists())) {

                                ArrayList<String> requesters =
                                        (ArrayList<String>) document.get("requesters");

                                if (requesters != null) {

                                    String status = "ACCEPTED";

                                    FirebaseFirestore.getInstance()
                                            .collection("catalogue")
                                            .document(requestedBook)
                                            .update("status", status);
                                }

                            }
                        }
                    });
        }
    }

    public static void deleteBookRequest(String requestedBook, String requester) {

        FirebaseFirestore.getInstance().collection("catalogue")
                .document(requestedBook)
                .collection("requests")
                .document(requester)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DELETE_REQUEST", "Request data successfully written!");
                    FirebaseIntegrity.handleDeclineBookRequest(
                            "catalogue", requestedBook, requester, false);
                })
                .addOnFailureListener(e -> Log.e("DELETE_REQUEST",
                        "Error writing request data!"));
    }

    public static void declineBookRequest(Request request) {

        if (Parser.isValidRequestWithBookIdObject(request)) {

            String requester = request.getRequester();
            String requestedBook = request.getRequestedBook();

            FirebaseFirestore.getInstance().collection("catalogue")
                    .document(requestedBook)
                    .collection("requests")
                    .document(requester)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DECLINE_REQUEST", "Request data successfully written!");
                        FirebaseIntegrity.handleDeclineBookRequest(
                                "catalogue", requestedBook, requester, false);
                    })
                    .addOnFailureListener(e -> Log.e("DECLINE_REQUEST",
                            "Error writing request data!"));
        }
    }

    public static void declineSpecificBookRequest(Request request,
                                                  String requester, boolean accepted) {

        if (Parser.isValidRequestWithBookIdObject(request)) {

            String requestedBook = request.getRequestedBook();

            FirebaseFirestore.getInstance().collection("catalogue")
                    .document(requestedBook)
                    .collection("requests")
                    .document(requester)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NEW_REQUEST", "Request data successfully written!");
                        FirebaseIntegrity.handleDeclineBookRequest(
                                "catalogue", requestedBook, requester, accepted);
                    })
                    .addOnFailureListener(e -> Log.e("NEW_REQUEST",
                            "Error writing request data!"));
        }
    }

    ///////////////////////////////// FIREBASE METHODS FOR EXCHANGE ////////////////////////////////

    public static void pushNewExchangeToFirebase(Exchange exchange) {

        Map<String, Object> docData = new HashMap<>();
        docData.put("exchangeId", exchange.getExchangeId());
        docData.put("owner", exchange.getOwner());
        docData.put("borrower", exchange.getBorrower());
        docData.put("relatedBook", exchange.getRelatedBook());
        docData.put("ownerBookStatus", exchange.getOwnerBookStatus());
        docData.put("borrowerBookStatus", exchange.getBorrowerBookStatus());
        docData.put("meetingDetails", exchange.getMeetingDetails());

        FirebaseFirestore.getInstance().collection("exchange")
                .document(exchange.getExchangeId())
                .set(docData)
                .addOnSuccessListener(aVoid -> Log.d("NEW_EXCHANGE",
                        "exchange data successfully written!"))
                .addOnFailureListener(e -> Log.w("NEW_EXCHANGE",
                        "Error writing exchange data!", e));

    }

    public static Exchange getExchangeFromFirestore(DocumentSnapshot document) {
        String exchangeId = document.getString("exchangeId");
        String relatedBook = document.getString("relatedBook");
        String owner = document.getString("owner");
        String borrower = document.getString("borrower");
        String ownerBookStatus = document.getString("ownerBookStatus");
        String borrowerBookStatus = document.getString("borrowerBookStatus");
        HashMap<String, Object> meetingDetailsMap
                = (HashMap<String, Object>) document.get("meetingDetails");

        Log.e("GET_EXCHANGE", "meetingMap is: " + meetingDetailsMap);
        if (meetingDetailsMap == null) {
            return null;
        }

        double latitude = (double) meetingDetailsMap.get("latitude");
        double longitude = (double) meetingDetailsMap.get("longitude");
        String address = (String) meetingDetailsMap.get("address");
        String meetingDate = (String) meetingDetailsMap.get("meetingDate");
        String meetingTime = (String) meetingDetailsMap.get("meetingTime");

        MeetingDetails meetingDetails = (Parser.isValidMeetingDataFormat(latitude,
                longitude, address, meetingDate, meetingTime)) ? new MeetingDetails(latitude,
                longitude, address, meetingDate, meetingTime) : null;

        if (meetingDetails == null) {
            return null;
        }

        // return a valid Exchange variable
        return (Parser.isValidExchangeDataFormat(exchangeId, relatedBook, owner, borrower,
                ownerBookStatus, borrowerBookStatus, meetingDetails)) ? new Exchange(exchangeId,
                relatedBook, owner, borrower, ownerBookStatus,
                borrowerBookStatus, meetingDetails) : null;
    }


    /////////////////////////////// FIREBASE METHODS FOR NOTIFICATIONS /////////////////////////////

    /**
     * Adds a notification to Firebase
     */
    public static void pushNewNotificationToFirebase(Notification notification) {

        Map<String, Object> docData = new HashMap<>();
        docData.put("message", notification.getMessage());
        docData.put("sender", notification.getSender());
        docData.put("receiver", notification.getReceiver());
        docData.put("relatedBook", notification.getRelatedBook());
        docData.put("seen", notification.getSeen());
        docData.put("type", notification.getType());
        docData.put("notificationDate", notification.getNotificationDate());

        FirebaseFirestore.getInstance().collection("users")
                .document(notification.getReceiver())
                .collection("notifications")
                .document(notification.getNotificationDate())
                .set(docData)
                .addOnSuccessListener(aVoid -> Log.d("NEW_NOTIFICATION",
                        "Notification data successfully written!"))
                .addOnFailureListener(e -> Log.w("NEW_NOTIFICATION",
                        "Error writing notification data!", e));

    }

    public static void setAllNotificationsToSeenTrue(User currentUser){
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail())
                .collection("notifications")
                .whereEqualTo("seen",false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(currentUser.getEmail())
                                    .collection("notifications")
                                    .document(document.getId())
                                    .update("seen",true);
                        }
                    } else {
                        Log.d("UPDATE_ALL_NOTI_TO_SEEN_TRUE_FAILED",
                                "Error getting documents: ", task.getException());
                    }
                });

    }

    public static ArrayList<String> getAllNotificationsForCurrentUserFromFirebase(User currentUser){

        ArrayList<String> notifications = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail())
                .collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            notifications.add(document.getId());
                        }
                    } else {
                        Log.d("UPDATE_ALL_NOTI_TO_SEEN_TRUE_FAILED",
                                "Error getting documents: ", task.getException());
                    }
                });

        return notifications;
    }

    public static void deleteUserNotificationsFromFirebase(String userEmail) {

        // get an instance of the document and delete it
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userEmail)
                .collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userEmail)
                                    .collection("notifications")
                                    .document(document.getId())
                                    .delete();
                        }
                    } else {
                        Log.d("DELETE_ALL_NOTI_FAILED",
                                "Error getting documents: ", task.getException());
                    }
                });
    }

    public static void deleteNotificationFromFirebase(ArrayList<String> notifications,
                                                      int position, String userEmail) {

        // get an instance of the document and delete it
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userEmail)
                .collection("notifications")
                .document(notifications.get(position))
                .delete()
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("DELETE_DOCUMENT_FROM_COLLECTION",
                                "Error deleting collection document!");
                    }
                });
    }
    public static void setNotificationCounterNumber(NotificationCounter notificationCounter, User currentUser) {
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getEmail()).collection("notifications")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int counter = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("seen").toString().equals("false")){
                                counter++;
                            }
                        }
                        notificationCounter.setNotificationNumberCounterInTextView(counter);
                    } else {
                        Log.d("UPDATE_NOTIFICATION_COUNTER_FAILED", "Error getting documents: ", task.getException());
                    }
                }
            });
    }


    /////////////////////////////////// GENERAL FIREBASE METHODS ///////////////////////////////////

    private static String CLEAN_OC_SRC_D_CHAIN = "CLEAN_OBJECT_COLLECTION_SRC_DEST_CHAIN";
    private static String CLEAN_OC_DEST_S_CHAIN = "CLEAN_OBJECT_COLLECTION_DEST_SRC_CHAIN";

    public static void signOutCurrentlyLoggedInUser() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void deleteCurrentlyLoggedInUser() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if ((currentUser != null) && (currentUser.getEmail() != null)) {

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getEmail())
                    .delete()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            currentUser.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.e("DELETE_USER", "User account deleted.");
                                        }
                                    });
                        }
                    });
            Log.d("DEBUG", "Successfully deleted user from Firebase.");
        }


    }


    public static Book getBookFromFirestore(DocumentSnapshot document) {
        String id = document.getString("id");
        String title = document.getString("title");
        String author = document.getString("author");
        String isbn = document.getString("isbn");
        String owner = document.getString("owner");
        String status = document.getString("status");
        Log.e("GET_BOOK", id + " " + title + " " + author);
        if (document.get("nonExchange") == null) {
            return null;
        }
        boolean nonExchange = Objects.requireNonNull(document.get("nonExchange")).toString().equals("true");
//        Log.e("GET_BOOK", id + " " + title + " " + author);
        String comment = document.getString("comment");
        String condition = document.getString("condition");
        String photo = document.getString("photo");
        ArrayList<String> requesters = (ArrayList<String>) document.get("requesters");

//        Log.e("GET_DOC_FIRE_FROM_OBJECT", Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, photo, requesters) + " " + id);

        // this assumes that Firebase books are valid
        return Parser.parseBook(id, title, author, isbn, owner,
                status, nonExchange, comment, condition, photo, requesters);
    }

    public static User getUserFromFirestore(DocumentSnapshot document) {
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String username = document.getString("username");
        String password = document.getString("password");
        String phoneNumber = document.getString("phoneNumber");
        String photo = document.getString("photo");

        Log.e("GET_DOC_FIRE_USER_FROM_OBJECT", Parser.parseUser(firstName,
                lastName, email,
                username, password, phoneNumber, photo) + " " + email);

//        return new User(firstName, lastName, email, username, password, phoneNumber, photo);

        return Parser.parseUser(firstName, lastName, email,
                username, password, phoneNumber, photo);  // this assumes valid Firebase users
    }

    public static ArrayList<String> getBookKeywords (String title, String author, String isbn) {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add(""); // first element is empty string

        // fields we're interested in
        String[] fields = {"title", "author", "isbn"};

        for (String field : fields) {
            String curr = "", f = "";
            switch (field) {
                case "title": f = title.toLowerCase(); break;
                case "author": f = author.toLowerCase(); break;
                case "isbn": f = isbn.toLowerCase(); break;
            }
            for (int j = 0; j < f.length(); j++) {
                curr += f.charAt(j);
                keywords.add(curr);
            }
            // adding individual words
            Collections.addAll(keywords, f.split(" "));
        }

        return keywords;
    }

    public static void deleteDocumentsFromSubcollectionOnFieldValue(String collectionName,
                                                                    String subcollectionName,
                                                                    String field,
                                                                    String fieldValue) {
        // get an instance of the collection
        FirebaseFirestore.getInstance().collection(collectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if collection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // for each document in collection
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    FirebaseFirestore.getInstance()
                                            .collection(collectionName)
                                            .document(document.getId())
                                            .collection(subcollectionName)
                                            .whereEqualTo(field, fieldValue)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {

                                                    // if subcollection has documents
                                                    if (!Objects.requireNonNull(
                                                            task1.getResult()).isEmpty()) {

                                                        // for each document in subcollection
                                                        for (DocumentSnapshot document1
                                                                : task1.getResult()) {
                                                            if (document1.exists()) {

                                                                // delete the document
                                                                deleteDocumentFromSubcollectionFirebase(
                                                                        collectionName,
                                                                        document.getId(),
                                                                        subcollectionName,
                                                                        document1.getId());
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    public static void deleteDocumentFromSubcollectionFirebase(String collectionName,
                                                               String docID,
                                                               String subcollectionName,
                                                               String subcollectionDocID) {

        // get an instance of the document and delete it
        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(docID)
                .collection(subcollectionName)
                .document(subcollectionDocID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("DELETE_DOCUMENT_FROM_COLLECTION",
                                "Error deleting collection document!");
                    }
                });
    }

    public static void updateBookRequestersInCollection(String collectionName, String bookID) {
        ArrayList<String> requesters = new ArrayList<>();

        FirebaseFirestore.getInstance().collection(collectionName)
                .document(bookID)
                .collection("requests")
                .whereEqualTo("requestedBook", bookID)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        // if collection has documents
                        if (!Objects.requireNonNull(task1.getResult()).isEmpty()) {

                            // for each document in collection
                            for (DocumentSnapshot document1 : task1.getResult()) {
                                if (document1.exists()) {
                                    requesters.add(document1.getId());
                                }
                            }

                            FirebaseFirestore.getInstance().collection(collectionName)
                                    .document(bookID).update("requesters", requesters);
                        } else {  // no requests
                            FirebaseFirestore.getInstance().collection(collectionName)
                                    .document(bookID).update("requesters", new ArrayList<>());
                        }
                    }
                });

    }

    public static void handleDeclineBookRequest(String collectionName,
                                            String bookID, String requester, boolean accepted) {

        FirebaseFirestore.getInstance().collection(collectionName)
                .document(bookID)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        DocumentSnapshot document = task1.getResult();

                        // if the document exists
                        if ((document != null) && (document.exists())) {

                            ArrayList<String> requesters =
                                    (ArrayList<String>) document.get("requesters");

                            if (requesters != null) {

                                requesters.remove(requester);

                                String status;
                                if (!accepted) {
                                    status = (requesters.size() == 0)
                                            ? "AVAILABLE" : "REQUESTED";
                                } else {
                                    status = "ACCEPTED";
                                }

                                FirebaseFirestore.getInstance()
                                        .collection(collectionName)
                                        .document(bookID)
                                        .update("status", status,
                                                "requesters", requesters);
                            }

                        }
                    }
                });

    }

    public static void deleteDocumentsFromCollectionOnFieldValue(String collectionName,
                                                                 String field, String fieldValue) {
        // get an instance of the collection
        FirebaseFirestore.getInstance().collection(collectionName)
                .whereEqualTo(field, fieldValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if collection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // for each document in collection
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    // delete the document
                                    deleteDocumentFromCollectionFirebase(collectionName,
                                            document.getId());
                                }
                            }
                        }
                    }
                });
    }

    public static void setDocumentFromObject(String collectionName, String docID,
                                              HashMap<String, Object> mapObject) {

        // get an instance of the document and set it to mapObject
        FirebaseFirestore.getInstance().collection(collectionName).document(docID)
                .set(mapObject)
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("SET_DOCUMENT_FROM_OBJECT", "Error writing document!");
                        if (collectionName.equals("catalogue")) {
                            updateBookRequestersInCollection(collectionName, docID);
                        }
                    }
                });
    }


    public static void deleteDocumentFromCollectionFirebase(String srcCollectionName,
                                                            String docID) {

        // get an instance of the document and delete it
        FirebaseFirestore.getInstance()
                .collection(srcCollectionName)
                .document(docID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("DELETE_DOCUMENT_FROM_COLLECTION",
                                "Error deleting collection document!");
                    }
                });
    }

    public static void updateToken(User currentUser){
        //  get the current user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // get the token for this instance of the app
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshToken!=null){
        //  update the user's token in Firestore
        Token token = new Token(refreshToken);
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail())
                .update("token",token.getToken());
        Log.d("token","updated to "+refreshToken);}
    }

    public static void updateToken(){
        //  get the current user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // get the token for this instance of the app
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        //  update the user's token in Firestore
        if(refreshToken!=null){
        Token token = new Token(refreshToken);
        FirebaseFirestore.getInstance().collection("users")
                .document(firebaseUser.getEmail())
                .update("token",token.getToken());
        Log.d("token","updated to "+refreshToken);
        }
    }

}
