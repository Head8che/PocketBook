package com.example.pocketbook.notifications;

import com.example.pocketbook.model.User;

// Data class to store the info to be displayed in notifications
public class Data {

    private String body;
    private String title;
    private String date;
    private String group;
    private int icon;
    private String receiver;

    /**
     * class constructor
     * @param body : the body of the notification
     * @param title :  the title of the notification
     * @param date : the date the notification was sent
     * @param group : the type of the notification
     * @param icon : the icon of the app
     * @param receiver : the user to receive the notification
     */
    public Data(String body, String title, String date, String group, int icon, String receiver) {

        this.body = body;
        this.title = title;
        this.date = date;
        this.group = group;
        this.icon = icon;
        this.receiver = receiver;

    }

    public Data() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
