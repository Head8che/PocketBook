package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;

public class Exchange implements Serializable {
    private String id;
    private String owner;
    private String borrower;
    private String ownerBookStatus;
    private String borrowerBookStatus;
    private Location location;

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public Exchange() {}

    /**
     *
     * @param id exchange id
     * @param owner owner involved in the exchange
     * @param borrower borrower involved in the exchange
     * @param ownerBookStatus book status for the owner
     * @param borrowerBookStatus book status for the borrower
     * @param location location for the exchange
     */
    public Exchange(String id, String owner, String borrower,
                    String ownerBookStatus, String borrowerBookStatus, Location location) {

        // if non-optional fields are not null
        if ((id != null) && (owner != null) && (borrower != null) && (ownerBookStatus != null)
                && (borrowerBookStatus != null) && (location != null)) {

            // trim all values
            id = id.trim();
            owner = owner.trim().toLowerCase();  // lowercase email
            borrower = borrower.trim().toLowerCase();  // lowercase email
            ownerBookStatus = ownerBookStatus.trim().toUpperCase();  // uppercase status
            borrowerBookStatus = borrowerBookStatus.trim().toUpperCase();  // uppercase status

            // only sets Exchange data if the data is valid
            if (true/*Parser.isValidExchangeData(id, owner, borrower,
                    ownerBookStatus, borrowerBookStatus, location)*/) {

                this.id = id;
                this.owner = owner;
                this.borrower = borrower;
                this.ownerBookStatus = ownerBookStatus;  // ["AVAILABLE", "BORROWED", "ACCEPTED"]
                this.borrowerBookStatus = borrowerBookStatus;  // same as ownerBookStatus
                this.location = location;
            }
        }

    }
}
