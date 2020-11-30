package com.example.pocketbook.notifications;

// class carrying the notification's data and the receiver token for the RemoteMessage object
public class Sender {
    private Data data;
    private String to;

    /**
     *
     * @param data : data object carrying info about the notification
     * @param to : the reciever of the notification
     */
    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Sender() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
