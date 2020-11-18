package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;

public class Location implements Serializable {
    private String exchangeId;
    private double longitude;
    private double latitude;
    private String locationName;

    /**
     * Empty Constructor for Firestore to auto-create new object
     */
    public Location() {}

    /**
     *
     * @param exchangeId id of related exchange
     * @param longitude location longitude
     * @param latitude location latitude
     * @param locationName name of location, if one exists
     */
    public Location(String exchangeId, double longitude, double latitude, String locationName) {

        // if non-optional fields are not null
        if (exchangeId != null) {

            // trim all values
            exchangeId = exchangeId.trim();
            locationName = (locationName == null) ? "" : locationName.trim();

            // only sets Location data if the data is valid
            if (true/*Parser.isValidLocationData(exchangeId, longitude, latitude, locationName)*/) {

                this.exchangeId = exchangeId;
                this.longitude = longitude;
                this.latitude = latitude;
                this.locationName = locationName;
            }
        }

    }
}
