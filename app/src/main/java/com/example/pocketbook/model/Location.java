package com.example.pocketbook.model;

import com.example.pocketbook.util.Parser;
import java.io.Serializable;
import java.util.ArrayList;

public class Location implements Serializable {
    private String exchangeId;
    private double longitude;
    private double latitude;
    private String locationName;
    private String date;
    private String time;

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
    public Location(String exchangeId, double longitude, double latitude, String locationName, String date, String time) {

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
                this.date = date;
                this.time = time;
            }
        }

    }
    /**
     * Getter method for exchangeId
     * @return ExchangeId as String
     */
    public String getExchangeId()
    { return this.exchangeId;
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
     * Getter method for locationName
     * @return isbn as String
     */
    public String getLocationName()
    { return this.locationName;
    }

    /**
     * Getter method for latitude
     * @return author as Double
     */
    public String getDate()
    { return this.date;
    }

    /**
     * Getter method for locationName
     * @return isbn as String
     */
    public String getTime()
    { return this.time;
    }



}
