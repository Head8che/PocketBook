package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;

public class MeetingDetails implements Serializable {
    private double longitude;
    private double latitude;
    private String address;
    private String meetingDate;
    private String meetingTime;

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public MeetingDetails() {}

    /**
     *
     * @param longitude location longitude
     * @param latitude location latitude
     * @param address address name, if one exists
     */
    public MeetingDetails(double latitude, double longitude,
                          String address, String meetingDate, String meetingTime) {

        // if non-optional fields are not null
        if ((address != null)
                && (meetingDate != null) && (meetingTime != null)) {

            // trim all values
            address = address.trim();
            meetingDate = meetingDate.trim();
            meetingTime = meetingTime.trim();

            // only sets MeetingDetails data if the data is valid
            if (Parser.isValidMeetingDataFormat(latitude,
                    longitude, address, meetingDate, meetingTime)) {

                this.latitude = latitude;
                this.longitude = longitude;
                this.address = address;
                this.meetingDate = meetingDate;
                this.meetingTime = meetingTime;
            }
        }

    }

    /**
     * Getter method for longitude
     * @return longitude as Double
     */
    public Double getLongitude()
    { return this.longitude;
    }

    /**
     * Getter method for latitude
     * @return author as Double
     */
    public Double getLatitude()
    { return this.latitude;
    }

    /**
     * Getter method for address
     * @return isbn as String
     */
    public String getAddress()
    { return this.address;
    }

    /**
     * Getter method for latitude
     * @return author as Double
     */
    public String getMeetingDate()
    { return this.meetingDate;
    }

    /**
     * Getter method for address
     * @return isbn as String
     */
    public String getMeetingTime()
    { return this.meetingTime;
    }



}
