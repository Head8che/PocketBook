package com.example.pocketbook.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
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
import com.google.firebase.firestore.QuerySnapshot;
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

    public static void setBookTitleFirebase(Book book, String title) {
        if (Parser.isValidBookTitle(title)) {
            setBookDataFirebase(book, "title", title);
        }
    }
    public static void setBookAuthorFirebase(Book book, String author) {
        if (Parser.isValidBookAuthor(author)) {
            setBookDataFirebase(book, "author", author);
        }
    }
    public static void setBookIsbnFirebase(Book book, String isbn) {
        if (Parser.isValidBookIsbn(isbn)) {
            setBookDataFirebase(book, "isbn", Parser.convertToIsbn13(isbn));
        }
    }
    public static void setBookCommentFirebase(Book book, String comment) {
        if (Parser.isValidBookComment(comment)) {
            setBookDataFirebase(book, "comment", comment);
        }
    }
    public static void setBookConditionFirebase(Book book, String condition) {
        if (Parser.isValidBookCondition(condition)) {
            setBookDataFirebase(book, "condition", condition);
        }
    }
    public static void setBookStatusFirebase(Book book, String status) {
        if (Parser.isValidBookStatus(status)) {
            setBookDataFirebase(book, "status", status);
        }
    }
    public static void setBookPhotoFirebase(Book book, String photo) {
        if (Parser.isValidBookPhoto(photo)) {
            setBookDataFirebase(book, "photo", photo);
        }
    }

    public static void setBookDataFirebase(Book book, String bookFieldName, String bookFieldValue) {
        FirebaseFirestore.getInstance().collection("catalogue")
                .document(book.getId())
                .update(bookFieldName, bookFieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SET_BOOK", "Book data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_BOOK", "Error writing book data!", e);
                    }
                });
    }

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

    public static void deleteBookFirebase(Book book) {
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

    public static void setEmailFirebase(User user, String email) {
        if (Parser.isValidUserEmail(email)) {
            setUserDataFirebase(user, "email", email);
        }
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
    public static void setPasswordFirebase(User user, String password) {
        if (Parser.isValidPassword(password)) {
            setUserDataFirebase(user, "password", password);
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

    /**
     * puts user information into Firebase*
     */
    public static void pushUserToFirebaseNoAuth(User newUser) {

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
            docData.put("phoneNumber", phoneNumber);
            docData.put("photo", photo);

            FirebaseIntegrity.setDocumentFromObject("users", email, docData);

        }
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

        if (Parser.isValidRequestObject(request)) {

            String requestee = request.getRequestee();
            String requester = request.getRequester();
            String requestDate = request.getRequestDate();
            String requestedBook = request.getRequestedBook();

            // TODO: create Exchange Model to handle accepting a request

//            // if request is already in list
//            if (requestedBook.getRequesters().contains(requester)) {
//                return;
//            }
//
//            Map<String, Object> docData = new LinkedHashMap<>();
//            docData.put("requestee", requestee);
//            docData.put("requester", requester);
//            docData.put("requestDate", requestDate);
//            docData.put("requestedBook", requestedBook.getId());
//
//            FirebaseFirestore.getInstance().collection("catalogue")
//                    .document(requestedBook.getId())
//                    .collection("requests").document(requester)
//                    .set(docData)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d("NEW_REQUEST", "Request data successfully written!");
//                            FirebaseIntegrity.setBookStatusFirebase(requestedBook,
//                                    "REQUESTED");
//                            FirebaseIntegrity.updateBookRequestersInCollection(
//                                    "catalogue", requestedBook.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("NEW_REQUEST", "Error writing request data!");
//                        }
//                    });
        }
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
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("NEW_REQUEST", "Request data successfully written!");
                            FirebaseIntegrity.handleDeclineBookRequest(
                                    "catalogue", requestedBook, requester);
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
        }


    }


    public static Book getBookFromFirestore(DocumentSnapshot document) {
        String id = document.getString("id");
        String title = document.getString("title");
        String author = document.getString("author");
        String isbn = document.getString("isbn");
        String owner = document.getString("owner");
        String status = document.getString("status");
        String comment = document.getString("comment");
        String condition = document.getString("condition");
        String photo = document.getString("photo");
        ArrayList<String> requesters = (ArrayList<String>) document.get("requesters");

//        Log.e("GET_DOC_FIRE_FROM_OBJECT", Parser.parseBook(id, title, author, isbn, owner,
//                status, comment, condition, photo, requesters) + " " + id);

        // this assumes that Firebase books are valid
        return Parser.parseBook(id, title, author, isbn, owner,
                status, comment, condition, photo, requesters);
    }

    public static User getUserFromFirestore(DocumentSnapshot document) {
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String username = document.getString("username");
        String password = document.getString("password");
        String phoneNumber = document.getString("phoneNumber");
        String photo = document.getString("photo");

        Log.e("GET_DOC_FIRE_USER_FROM_OBJECT", Parser.parseUser(firstName, lastName, email,
                username, password, phoneNumber, photo) + " " + email);

//        return new User(firstName, lastName, email, username, password, phoneNumber, photo);

        return Parser.parseUser(firstName, lastName, email,
                username, password, phoneNumber, photo);  // this assumes that Firebase users are valid
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

    public static void addRequestersToAllBooksInCollection(String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if collection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // for each document in collection
                            for (DocumentSnapshot document : task.getResult()) {

                                ArrayList<String> requesters = new ArrayList<>();

                                FirebaseFirestore.getInstance().collection(collectionName)
                                        .document(document.getId())
                                        .collection("requests")
                                        .whereEqualTo("requestedBook", document.getId())
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
                                                            .document(document.getId()).update("requesters", requesters);
                                                } else {  // no requests
                                                    FirebaseFirestore.getInstance().collection("catalogue")
                                                            .document(document.getId()).update("requesters", new ArrayList<>());
                                                }
                                            }
                                        });
                            }
                        }
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
                                            String bookID, String requester) {

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

                                String status = (requesters.size() == 0)
                                        ? "AVAILABLE" : "REQUESTED";

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

    public static HashMap<String, Object> getBookMapObjectFromSnapshot(DocumentSnapshot document) {
        String id = document.getString("id");
        String title = document.getString("title");
        String author = document.getString("author");
        String isbn = document.getString("isbn");

        HashMap<String, Object> bookMapObject = new HashMap<>();
        bookMapObject.put("id", id);
        bookMapObject.put("title", document.getString("title"));
        bookMapObject.put("author", document.getString("author"));
        bookMapObject.put("isbn", document.getString("isbn"));
        bookMapObject.put("owner", document.getString("owner"));
        bookMapObject.put("status", document.getString("status"));
        bookMapObject.put("comment", document.getString("comment"));
        bookMapObject.put("condition", document.getString("condition"));
        bookMapObject.put("photo", document.getString("photo"));

        if ((id != null)  // non-null id
                && (Parser.isValidBook(bookMapObject)) // valid book
                && (id.equals(document.getId()))) { // valid id

            bookMapObject.put("isbn", Parser.convertToIsbn13(isbn));
            bookMapObject.put("keywords", getBookKeywords(title, author, isbn));
            return bookMapObject;
        }

        return null;
    }

    public static HashMap<String, Object> getUserMapObjectFromSnapshot(DocumentSnapshot document) {
        String email = document.getString("email");

        HashMap<String, Object> userMapObject = new HashMap<>();
        userMapObject.put("email", email);
        userMapObject.put("firstName", document.getString("firstName"));
        userMapObject.put("lastName", document.getString("lastName"));
        userMapObject.put("username", document.getString("username"));
        userMapObject.put("password", document.getString("password"));
        userMapObject.put("phoneNumber", document.getString("phoneNumber"));
        userMapObject.put("photo", document.getString("photo"));

        if ((email != null)  // non-null email
                && (Parser.isValidUser(userMapObject)) // valid user
                && (email.equals(document.getId()))) { // valid email id

            return userMapObject;
        }

        return null;

    }

    public static HashMap<String, Object>
    getRequestMapObjectFromSnapshot(DocumentSnapshot document) {

        String requester = document.getString("requester");

        HashMap<String, Object> requestMapObject = new HashMap<>();

        requestMapObject.put("requester", document.getString("requester"));
        requestMapObject.put("requestee", document.getString("requestee"));
        requestMapObject.put("requestedBook", document.getString("requestedBook"));
        requestMapObject.put("requestDate", document.getString("requestDate"));

        if ((requester != null)  // non-null requester
                && (Parser.isValidRequest(requestMapObject)) // valid request
                && (requester.equals(document.getId()))) { // valid requester id

            return requestMapObject;
        }

        return null;

    }

    public static HashMap<String, Object>
    getNotificationMapObjectFromSnapshot(DocumentSnapshot document) {

        HashMap<String, Object> notificationMapObject = new HashMap<>();

        notificationMapObject.put("message", document.getString("message"));
        notificationMapObject.put("sender", document.getString("sender"));
        notificationMapObject.put("receiver", document.getString("receiver"));
        notificationMapObject.put("relatedBook", document.getString("relatedBook"));
        notificationMapObject.put("seen", document.getBoolean("seen"));
        notificationMapObject.put("type", document.getString("type"));
        notificationMapObject.put("notificationDate", document.getString("notificationDate"));

        if (Parser.isValidNotification(notificationMapObject)) { // valid notification
            return notificationMapObject;
        }

        return null;

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

    private static void setSubcollectionDocumentFromObject(String collectionName,
                                                             String docID,
                                                             String subcollectionName,
                                                             String subcollectionDocID,
                                                             HashMap<String, Object> subMapObject,
                                                             String objectType) {

        FirebaseIntegrity.chainSetSubcollectionDocumentFromObject(collectionName, docID,
                subcollectionName, subcollectionDocID,
                subMapObject, objectType, null, null, false);

    }

    private static void chainSetSubcollectionDocumentFromObject(String collectionName,
                                                                  String docID,
                                                                  String subcollectionName,
                                                                  String subcollectionDocID,
                                                                  HashMap<String, Object>
                                                                        subMapObject,
                                                                  String objectType,
                                                                  String chain,
                                                                  String srcCollectionNameIfChain,
                                                                  boolean makeChainCall) {

        // set an instance of subcollection document
        // i.e. collectionName/docID/subcollectionName/subcollectionDocID to document
        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(docID)
                .collection(subcollectionName)
                .document(subcollectionDocID)
//                .set(Objects.requireNonNull(document.getData()))
                .set(subMapObject)
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("SET_SUBCOLLECTION_DOCUMENT_FROM_SNAPSHOT",
                                "Error writing subcollection document!");
                    } else {

                        if (makeChainCall && (chain != null)
                                && (chain.equals(CLEAN_OC_SRC_D_CHAIN)
                                || chain.equals(CLEAN_OC_DEST_S_CHAIN))) {
                            FirebaseIntegrity.chainDeleteObjectFromCollectionFirebase(
                                    objectType, srcCollectionNameIfChain,
                                    chain, collectionName);
                        }
                    }
                });
    }

    private static void getSubcollectionAndSetDestinationSubcollectionDocs(
            String srcCollectionName, String docID, String subcollectionName,
            String destCollectionName, String objectType, String chain,
            boolean makeChainCall) {

        // get an instance of subcollection i.e. srcCollection/docID/subcollection
        FirebaseFirestore.getInstance()
                .collection(srcCollectionName)
                .document(docID)
                .collection(subcollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if srcCollection subcollection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // for each document in subcollection
                            for (DocumentSnapshot document : task.getResult()) {

                                // get subcollection document id
                                String subcollectionDocID = document.getId();

                                // if the document is valid
                                if (document.exists()) {

                                    HashMap<String, Object> subMapObject;

                                    if (objectType.equals("User")) {
                                        subMapObject = getNotificationMapObjectFromSnapshot(document);
                                    } else {
                                        // get a HashMap object of the book's data
                                        // HashMap is Parser-verified, so data is valid
                                        subMapObject = getRequestMapObjectFromSnapshot(document);
                                    }

                                    // add only valid object data to destCollection
                                    if (subMapObject != null) {

                                        // set destCollection subcollection document
                                        FirebaseIntegrity.chainSetSubcollectionDocumentFromObject(
                                                destCollectionName,
                                                docID,
                                                subcollectionName,
                                                subcollectionDocID,
                                                subMapObject,
                                                objectType,
                                                chain,
                                                srcCollectionName,
                                                makeChainCall);
                                    }  // because we're not deleting data, there's no need
                                       // to handle the case where subMapObject is not valid
                                }
                            }
                        }
                    }
                });
    }

    public static void copyObjectBetweenCollectionsFirebase(String srcCollectionName,
                                                            String objectType,
                                                            String destCollectionName) {
        // random chain string allows getSubcollectionAndSet... to be invoked
        chainCopyObjectBetweenCollectionsFirebase(srcCollectionName, objectType,
                destCollectionName, "COPY");

    }

    private static void chainCopyObjectBetweenCollectionsFirebase(String srcCollectionName,
                                                            String objectType,
                                                            String destCollectionName,
                                                            String chain) {
        // copy Book or User object (so, doc and subcollection) from one collection to another

        String subcollectionName;

        // switch the name of the subcollection depending on the object being copied
        switch (objectType) {
            case "Book":
                subcollectionName = "requests";
                break;
            case "User":
                subcollectionName = "notifications";
                break;
            default:
                subcollectionName = "";
                break;
        }

        // get an instance of srcCollection
        FirebaseFirestore.getInstance().collection(srcCollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if srcCollection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // put the number of documents in the collection
                            // into numOfReturnedRows
                            int numOfReturnedRows = task.getResult().size();
                            int rowCount = 0;

                            // for each document in srcCollection
                            for (DocumentSnapshot document : task.getResult()) {
                                rowCount += 1;  // rowCount goes from [1, 2, ..., numOfReturnedRows]
                                String docID = document.getId();  // get id of current document

                                // if the document is valid
                                if (document.exists()) {
                                    Log.e("CHAIN_COPY_OBJECT_BETWEEN_COLLECTIONS",
                                            document.getId());

                                    HashMap<String, Object> mapObject;

                                    if (objectType.equals("User")) {
                                        mapObject = getUserMapObjectFromSnapshot(document);
                                    } else {
                                        // get a HashMap object of the book's data
                                        // HashMap is Parser-verified, so data is valid
                                        mapObject = getBookMapObjectFromSnapshot(document);
                                    }

                                    // lastDocumentReached depends on whether or not the current
                                    // document is the last document in the collection
                                    boolean lastDocumentReached = (rowCount == numOfReturnedRows);

                                    // add only valid object data to destCollection
                                    if (mapObject != null) {

                                        // put mapObject into destCollection/docID path
                                        setDocumentFromObject(destCollectionName, docID,
                                                mapObject);

                                        // put subcollection data from srcCollection into
                                        // destCollection/docID/subcollection/
                                        // [subcollectionDocID] (which is generated in method below)
                                        getSubcollectionAndSetDestinationSubcollectionDocs(
                                                srcCollectionName,
                                                docID,
                                                subcollectionName,
                                                destCollectionName,
                                                objectType,
                                                chain,
                                                lastDocumentReached
                                        );
                                    } else {  // if the current document is not a valid object

                                        // if the current document is the last document
                                        // and there is a CLEAN_OC... chain call, delete
                                        // the object type from srcCollection
                                        if (lastDocumentReached && (chain != null)
                                                && (chain.equals(CLEAN_OC_SRC_D_CHAIN)
                                                || chain.equals(CLEAN_OC_DEST_S_CHAIN))) {
                                            FirebaseIntegrity
                                                    .chainDeleteObjectFromCollectionFirebase(
                                                    objectType, srcCollectionName,
                                                    chain, destCollectionName);
                                        }
                                    }

                                }
                            }
                        }
                    }
                });

    }

    private static void chainDeleteAllDocumentsInCollectionFirebase(String objectType,
                                                              String srcCollectionName,
                                                              String chain,
                                                              String destCollectionNameIfChain) {

        // get an instance of srcCollection
        FirebaseFirestore.getInstance().collection(srcCollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if srcCollection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // put the number of documents in the collection
                            // into numOfReturnedRows
                            int numOfReturnedRows = task.getResult().size();
                            int rowCount = 0;

                            // for each document in srcCollection
                            for (DocumentSnapshot document : task.getResult()) {
                                rowCount += 1;  // rowCount goes from [1, 2, ..., numOfReturnedRows]
                                String docID = document.getId();  // get id of current document

                                // lastDocumentReached depends on whether the current
                                // document is the last document in the collection
                                boolean lastDocumentReached = (rowCount == numOfReturnedRows);

                                deleteDocumentFromCollectionFirebase(srcCollectionName, docID);

                                // if the current document is the last document
                                // and there is a CLEAN_OC_SRC_D... chain call, copy
                                // the object type from destCollection into srcCollection
                                if (lastDocumentReached && (chain != null)
                                        && (chain.equals(CLEAN_OC_SRC_D_CHAIN))) {
                                    FirebaseIntegrity.chainCopyObjectBetweenCollectionsFirebase(
                                            destCollectionNameIfChain,
                                            objectType, srcCollectionName,
                                            CLEAN_OC_DEST_S_CHAIN);
                                }
                            }
                        } else {  // if srcCollection has no documents

                            // if there is a CLEAN_OC_SRC_D... chain call, copy
                            // the object type from destCollection into srcCollection
                            if ((chain != null) && (chain.equals(CLEAN_OC_SRC_D_CHAIN))) {
                                FirebaseIntegrity.chainCopyObjectBetweenCollectionsFirebase(
                                        destCollectionNameIfChain,
                                        objectType, srcCollectionName,
                                        CLEAN_OC_DEST_S_CHAIN);
                            }
                        }
                    }
                });
    }

    private static void chainDeleteSubcollectionDocAndChainDeleteCollectionDocsFirebase(
            String srcCollectionName, String docID, String subcollectionName, String objectType,
            String chain, String destCollectionNameIfChain, boolean lastDocumentReached) {

        // get an instance of the subcollection
        FirebaseFirestore.getInstance()
                .collection(srcCollectionName)
                .document(docID)
                .collection(subcollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if srcCollection subcollection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // put the number of documents in the subcollection
                            // into subNumOfReturnedRows
                            int subNumOfReturnedRows = task.getResult().size();
                            int subRowCount = 0;

                            // for each subcollection document
                            for (DocumentSnapshot document : task.getResult()) {
                                subRowCount += 1;  // subRowCount goes up to subNumOfReturnedRows
                                String subcollectionDocID = document.getId();

                                if (document.exists()) {

                                    // makeChainCall depends on whether or not the current
                                    // document is the last document in the collection and whether
                                    // or not the current sub-document is the last sub-document
                                    // in the subcollection
                                    boolean makeChainCall = (lastDocumentReached
                                            && (subRowCount == subNumOfReturnedRows));

                                    // delete the subcollection document i.e the document at
                                    // srcCollection/docID/subcollection/subcollectionDocID
                                    // and delete all documents in srcCollection,
                                    // if makeChainCall is true
                                    chainDeleteSubcollectionDocFromCollectionDocFirebase(
                                            srcCollectionName, docID, subcollectionName,
                                            subcollectionDocID, objectType,
                                            chain, destCollectionNameIfChain, makeChainCall);
                                }
                            }

                        } else {  // document has no subcollection

                            // if current doc is the last one in current doc
                            if (lastDocumentReached) {

                                // delete all documents in srcCollection
                                chainDeleteAllDocumentsInCollectionFirebase(objectType,
                                        srcCollectionName,
                                        chain,
                                        destCollectionNameIfChain);
                            }
                        }
                    }
                });

    }

    public static void deleteSubcollectionDocFromCollectionDocFirebase(
            String srcCollectionName, String docID, String subcollectionName,
            String subcollectionDocID, String objectType) {

        chainDeleteSubcollectionDocFromCollectionDocFirebase(srcCollectionName, docID,
                subcollectionName, subcollectionDocID, objectType,
                null, null, false);

    }

    private static void chainDeleteSubcollectionDocFromCollectionDocFirebase(
            String srcCollectionName, String docID, String subcollectionName,
            String subcollectionDocID, String objectType,
            String chain, String destCollectionNameIfChain, boolean makeChainCall) {

        // delete subcollection document
        // i.e. delete srcCollectionName/docID/subcollectionName/subcollectionDocID
        FirebaseFirestore.getInstance()
                .collection(srcCollectionName)
                .document(docID)
                .collection(subcollectionName)
                .document(subcollectionDocID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (!(task.isSuccessful())) {
                        Log.e("CHAIN_DELETE_SUBCOLLECTION_DOC_FROM_COLLECTION_DOC",
                                "Error deleting subcollection document!");
                    } else {

                        // if makeChainCall is true
                        // i.e. if current subcollection doc is the last one in current doc
                        if (makeChainCall) {
                            chainDeleteAllDocumentsInCollectionFirebase(objectType,
                                    srcCollectionName,
                                    chain,
                                    destCollectionNameIfChain);
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

    private static void chainDeleteObjectFromCollectionFirebase(String objectType,
                                                                String srcCollectionName,
                                                                String chain,
                                                                String destCollectionNameIfChain) {

        String subcollectionName;

        // switch the name of the subcollection depending on the object being deleted
        switch (objectType) {
            case "Book":
                subcollectionName = "requests";
                break;
            case "User":
                subcollectionName = "notifications";
                break;
            default:
                subcollectionName = "";
                break;
        }

        // get an instance of srcCollection
        FirebaseFirestore.getInstance().collection(srcCollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // if srcCollection has documents
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {

                            // put the number of documents in the collection
                            // into numOfReturnedRows
                            int numOfReturnedRows = task.getResult().size();
                            int rowCount = 0;

                            // for each document in srcCollection
                            for (DocumentSnapshot document : task.getResult()) {
                                rowCount += 1; // rowCount goes from [1, 2, ..., numOfReturnedRows]
                                String docID = document.getId();  // get id of current document

                                // if the document is valid
                                if (document.exists()) {

                                    // lastDocumentReached depends on whether or not the current
                                    // document is the last document in the collection
                                    boolean lastDocumentReached =
                                            (rowCount == numOfReturnedRows);

                                    // delete the subcollection documents and also the
                                    // collection documents, depending on lastDocumentReached
                                    chainDeleteSubcollectionDocAndChainDeleteCollectionDocsFirebase(
                                            srcCollectionName, docID, subcollectionName,
                                            objectType, chain, destCollectionNameIfChain,
                                            lastDocumentReached);

                                }
                            }
                        }
                    }
                });

    }

    public static void cleanObjectCollection(String objectType) {
        /* This method only cleans a collection of REAL documents.
           To remove VIRTUAL documents:
           - FirebaseIntegrity.copyObjectBetweenCollectionsFirebase(srcCollectionName,
                objectType, destCollectionName, null);
           - Go into Firebase console and delete srcCollectionName
           - FirebaseIntegrity.copyObjectBetweenCollectionsFirebase(destCollectionName,
                objectType, srcCollectionName, null);
           - Go into Firebase console and delete destCollectionName
        */

        // Book Validations
        // TODO: delete Parser-invalid books from Firebase
        // TODO: does id exist in Firebase?
        // TODO: does owner exist in FirebaseAuth and in Firestore?
        // TODO: does photo exist in Firebase?
        // TODO: if any of the above checks fail, delete book

        // User Validations
        // TODO: does email exist in FirebaseAuth and in Firestore?
        // TODO: does photo exist in Firebase?
        // TODO: does each bookID in list exist in Firebase?

        String srcCollectionName;
        String destCollectionName = "TEMP_CLEAN_OBJECT_COLLECTION";

        // switch the name of srcCollection depending
        // on the object type the collection includes
        switch (objectType) {
            case "Book":
                srcCollectionName = "test_catalogue";
                break;
            case "User":
                srcCollectionName = "users";
                break;
            default:
                srcCollectionName = "";
                break;
        }

        // start the cleaning process by invoking the
        // copyObject... method with CLEAN_OC_SRC_D_CHAIN
        FirebaseIntegrity.chainCopyObjectBetweenCollectionsFirebase(srcCollectionName,
                objectType, destCollectionName, CLEAN_OC_SRC_D_CHAIN);

    }

    /*
    ALWAYS DELETE SUBCOLLECTIONS BEFORE DELETING COLLECTIONS!

     HIGH LEVEL OPERATIONS BROKEN DOWN INTO RELEVANT METHODS:

     changeUserEmail("from@email.com", "to@email.com")
     - createAuthUserFirebase("to@email.com", userPasswordFromFirestore)
     - copyUserDocumentFirebase("from@email.com", "to@email.com")
     - copyUserDocumentSubcollectionFirebase("from@email.com", "notifications", "to@email.com")
     - deleteUserDocumentSubcollectionFirebase("from@email.com", "notifications")
     - deleteUserDocumentFirebase("from@email.com")
     - deleteAuthUserFirebase("from@email.com")

     changeUserPassword("user@email.com", "newPassword") // alternative to sending emails
     - changeUserPasswordFieldFirebase("user@email.com", "newPassword")
     - copyUserDocumentFirebase("user@email.com", "randomlyGeneratedTemp@email.com")
     - copyUserDocumentSubcollectionFirebase("user@email.com", "notifications", "randomlyGeneratedTemp@email.com")
     - deleteUserDocumentSubcollectionFirebase("user@email.com", "notifications")
     - deleteUserDocumentFirebase("user@email.com")
     - deleteAuthUserFirebase("user@email.com")
     - createAuthUserFirebase("user@email.com", newUserPasswordFromFirestore)
     - copyUserDocumentFirebase("randomlyGeneratedTemp@email.com", "user@email.com")  // copy vaild books back
     - copyUserDocumentSubcollectionFirebase("randomlyGeneratedTemp@email.com", "notifications", "user@email.com")
     - deleteUserDocumentSubcollectionFirebase("randomlyGeneratedTemp@email.com", "notifications")  // delete book requests
     - deleteUserDocumentFirebase("randomlyGeneratedTemp@email.com")  // delete books

     ? Add API Book to Firebase User ? Add API Book to Firebase (random user) ? Add in loop for multiple random books ?
     - randomly get real book ISBN
     - download book data & download image from URL
     - parse book to user (or random user)
     - pushNewBookToFirebase();

     addRandomBooksToRandomUsers(count)  // count is the number of books to add; should NOT work if no users exist
     addRandomUsers(count)  // first part of email should be name from API, pw should be 123456;
                            // first check if email exists in Firebase, then auth create, then Firestore
     addRandomBooksToUser(count, user)
     addBookToUser(isbn, user)  // if API works properly
     makeRandomUserRequestsToRandomBooks(count)  // if user has already requested book, request should fail
     makeRandomRequestsToBook(count, isbn)  // book has to exist in Firebase;
                                            // count other users request book; if count > # of users, create count requests then stop

     Notes
     - copyDocument for user should only work if auth account exists (SCRATCH THAT; need temp docs w/o email)

     Stuff from other places
     - book.pushNewBookToFirebase() --> FI.pNBTF(book)
     - currentBookCover = FirebaseStorage.getInstance().getReference()
                .child("default_images").child("no_book_cover_light.png") --> FI.getDefaultBookCover();
     - StorageReference childRef = storageRef.child(userName+".jpg") --> FI.uploadImage();
     ...

     Listener Chain should be:
      HomeFragment --> ViewBookFragment
      (ProfileFragment tabs have different queries so they should have their own listeners)
      OwnerFragment --> ViewMyBookFragment --> ViewMyBookRequestsFragment
                                               & ViewMyBookBookFragment
                                                          --> EditBookActivity (do new activities
                                                                                need new listeners?)
      BorrowerFragment --> ViewBookFragment
      (SearchFragment tabs have different queries so they should have their own listeners)
      SearchAllFragment --> ViewBookFragment
      SearchAvailableFragment --> ViewBookFragment

     BUGS W/IN APP CURRENTLY
     - Search Frag is not tabbed and items are not clickable
     - ViewBook toolbar scrolls up
     - AddBookActivity toolbar scroll up on field click (EditBookActivity doesn't [check manifest])
     - ProfileFragment has scroll for top Edit section AND for books section below

     - Home Frag (and ViewBook from Home Frag)
       does not have live listener (scroll update is not live):
      - TEST:
       - go to Home Frag in app (books are loaded)
       - change a book's title in Firebase
       - FAIL: book in Home Frag does not update title
       - SUCCESS: book in Home frag does update title

     - Profile Frag does not have live listener (scroll update is not live):
      - TEST:
       - go to Profile Frag in app (books are loaded)
       - change a book's title in Firebase
       - FAIL: book in Profile Frag does not update title
       - SUCCESS: book in Home frag does update title

     ViewMyBookBook Frag does update, but the listener should be in ProfileFrag not ViewMyBook...

     - App crashes after Profile Frag is returned to from
       ViewMyBookBook Frag and Firebase change is made:
      - TEST:
       - go to Profile Frag in app (books are loaded)
       - click on a book (go to ViewMyBookBook Frag)
       - go back to Profile Frag in app (books are loaded)
       - change a book's title in Firebase
       - FAIL: app crashes with error: <Fragment ViewMyBookBookFragment{1fa3b0b}
                                        (89fefd17-d5ce-40d2-a688-52fd14533add)}
                                        not associated with a fragment manager.>
       - SUCCESS: app does not crash

     - Book Search keywords do not update after EditBookActivity (check AddBookActivity too):
      - TEST:
       - go to Profile Frag in app (books are loaded)
       - click on a book (go to ViewMyBookBook Frag)
       - click on edit (go to EditBookActivity)
       - edit the book's title and save the changes
       - FAIL: Book keywords are not updated in Firebase
       - SUCCESS: Book keywords are updated in Firebase
       BONUS: changes should persist to ViewMyBookBook Frag onBackPressed and Profile Frag too

     */

}
