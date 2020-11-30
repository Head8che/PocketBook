package com.example.pocketbook.notifications;

// class carrying the notification's data and the receiver token for the RemoteMessage object
public class Sender {
    private Data data;
    private String to;

    /**
     *
     * @param data  data object carrying info about the notification
     * @param to  the receiver of the notification
     */
    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    /**
     * empty constructor for the Sender class
     */
    public Sender() {
    }

    /**
     * getter function for the data of a notification
     * @return data as a Data object
     */
    public Data getData() {
        return data;
    }

    /**
     * setter function for the data attribute
     * @param data data for a notification as a Data object
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * getter function for he token of the receiver
     * @return to as String
     */
    public String getTo() {
        return to;
    }

    /**
     * setter function for the to attribute
     * @param to the token of the receiver as a String
     */
    public void setTo(String to) {
        this.to = to;
    }
}
