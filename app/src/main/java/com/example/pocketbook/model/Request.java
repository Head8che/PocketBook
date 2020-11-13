package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request implements Serializable {
    private String requester;
    private String requestee;
    private String requestedBook;
    private String requestDate;
    private Book requestedBookObject;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public Request(){
    }

    /**
     * Request constructor
     * @param requester email of user requesting the book
     * @param requestee email of book owner
     * @param requestedBookObject Book object that represents the requested book
     */
    public Request(String requester, String requestee,
                   Book requestedBookObject, String requestDate) {

        // if non-optional fields are not null
        if ((requester != null) && (requestee != null)
                && (requestedBookObject != null) && (requestDate != null)) {

            // trim all values
            requester = requester.trim().toLowerCase();  // lowercase email
            requestee = requestee.trim().toLowerCase();  // lowercase email
            requestDate = (requestDate.equals(""))
                    ? LocalDateTime.now().format(formatter).trim()
                    : requestDate.trim();  // replace null or empty string with current time

            // only sets Request data if the data is valid
            if (Parser.isValidRequestData(requester, requestee, requestedBookObject, requestDate)) {

                this.requester = requester;
                this.requestee = requestee;
                this.requestedBook = requestedBookObject.getId().trim();
                this.requestedBookObject = requestedBookObject;
                this.requestDate = requestDate;
            }
        }
    }

    /**
     * Getter method for requester
     * @return requester as String
     */
    public String getRequester() { return this.requester; }

    /**
     * Getter method for requestee
     * @return requestee as String
     */
    public String getRequestee() { return this.requestee; }

    /**
     * Getter method for requestedBook
     * @return requestedBook as String
     */
    public String getRequestedBook() { return this.requestedBook; }

    /**
     * Getter method for requestedBookObject
     * @return requestedBookObject as Book object
     */
    public Book getRequestedBookObject() { return this.requestedBookObject; }

    /**
     * Getter method for requesteDate
     * @return requesteDate as String
     */
    public String getRequestDate() { return this.requestDate; }

}
