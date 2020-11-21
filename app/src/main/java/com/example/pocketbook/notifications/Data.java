package com.example.pocketbook.notifications;

public class Data {

    private String body;
    private String title;
    private String date;
    private String group;
    private int icon;

    public Data(String body, String title, String date, String group, int icon) {

        this.body = body;
        this.title = title;
        this.date = date;
        this.group = group;
        this.icon = icon;

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
