package com.example.pocketbook.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

/* TODO: Make this class superclass for BookList, RequestList and NotificationList, if possible */

public class ObjectList implements Serializable {
    protected LinkedHashMap<String, Object> objectList;

    public ObjectList() {
        this.objectList = new LinkedHashMap<>();
    }
}
