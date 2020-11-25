package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;

import java.io.Serializable;

public class Exchange implements Serializable {
    private String exchangeId;
    private String relatedBook;
    private String owner;
    private String borrower;
    private String ownerBookStatus;
    private String borrowerBookStatus;
    private MeetingDetails meetingDetails;

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public Exchange() {}

    /**
     *
     * @param exchangeId exchange exchangeId
     * @param relatedBook related book Id
     * @param owner owner involved in the exchange
     * @param borrower borrower involved in the exchange
     * @param ownerBookStatus book status for the owner
     * @param borrowerBookStatus book status for the borrower
     * @param meetingDetails meetingDetails for the exchange
     */
    public Exchange(String exchangeId, String relatedBook, String owner, String borrower,
                    String ownerBookStatus, String borrowerBookStatus,
                    MeetingDetails meetingDetails) {

        // if non-optional fields are not null
        if ((exchangeId != null) && (relatedBook != null) && (owner != null)
                && (borrower != null) && (ownerBookStatus != null)
                && (borrowerBookStatus != null) && (meetingDetails != null)) {

            // trim all values
            exchangeId = exchangeId.trim();
            relatedBook = relatedBook.trim();
            owner = owner.trim().toLowerCase();  // lowercase email
            borrower = borrower.trim().toLowerCase();  // lowercase email
            ownerBookStatus = ownerBookStatus.trim().toUpperCase();  // uppercase status
            borrowerBookStatus = borrowerBookStatus.trim().toUpperCase();  // uppercase status

            // only sets Exchange data if the data is valid
            if (Parser.isValidExchangeDataFormat(exchangeId, relatedBook, owner, borrower,
                    ownerBookStatus, borrowerBookStatus, meetingDetails)) {

                this.exchangeId = exchangeId;
                this.relatedBook = relatedBook;
                this.owner = owner;
                this.borrower = borrower;
                this.ownerBookStatus = ownerBookStatus;  // ["AVAILABLE", "BORROWED", "ACCEPTED"]
                this.borrowerBookStatus = borrowerBookStatus;  // same as ownerBookStatus
                this.meetingDetails = meetingDetails;

            }
        }

    }

    /**
     * Getter method for exchangeId
     * @return exchangeId as String
     */
    public String getExchangeId()
    { return this.exchangeId;
    }

    /**
     * Getter method for relatedBook
     * @return relatedBook as String
     */
    public String getRelatedBook()
    { return this.relatedBook;
    }

    /**
     * Getter method for owner
     * @return owner as String
     */
    public String getOwner()
    { return this.owner;
    }

    /**
     * Getter method for borrower
     * @return borrower as String
     */
    public String getBorrower()
    { return this.borrower;
    }

    /**
     * Getter method for OwnerBookStatus
     * @return ownerBookStatus as String
     */
    public String getOwnerBookStatus()
    { return this.ownerBookStatus;
    }

    /**
     * Getter method for borrowerBookStatus
     * @return borrowerBookStatus as String
     */
    public String getBorrowerBookStatus()
    { return this.borrowerBookStatus;
    }

    /**
     * Getter method for meetingDetails
     * @return meetingDetails as String
     */
    public MeetingDetails getMeetingDetails()
    { return this.meetingDetails;
    }
}
