package com.example.pocketbook.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
    TODO: declineRequest() and acceptRequest()
 */

public class RequestList implements Serializable {

    private LinkedHashMap<String, Request> requestList;
    private static int tempCount;
    private FirebaseFirestore db;
    private String bookId;

    public RequestList(String bookId) {

        this.requestList = new LinkedHashMap<String, Request>();
        this.db = FirebaseFirestore.getInstance();
        this.bookId = bookId;
        this.getData();
    }

    public void getData(){
        db.collection("catalogue").document(bookId)
                .collection("requests").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Request request = document.toObject(Request.class);
                                requestList.put(request.getRequester(),request);
                            }
                        } else {
                            Log.d("temp","failed to get data!");
                        }
                    }
                });
    }

    public Request getRequestAtPosition(int position) {
        List<String> keys = new ArrayList<String>(requestList.keySet());
        String positionalRequestID = keys.get(position);
        return requestList.get(positionalRequestID);
    }

    /* TODO: EXTEND */
    public Map<String, Request> getRequestList() {
        return requestList;
    }

    /* TODO: EXTEND */
    public Request getRequest(String requester) { return requestList.get(requester); }

    /* TODO: EXTEND */
    public int getSize() { return requestList.size(); }

    /* TODO: EXTEND */
    // overloaded containsRequest methods return true if request can be found in requestList
    public boolean containsRequest(String requester) { return requestList.get(requester) != null; }
    public boolean containsRequest(Request request) { return requestList.get(request.getRequester()) != null; }

    /* TODO: EXTEND */
    public boolean addRequest(Request request) {
        request.getRequestedBookObject().setStatus("REQUESTED");
        addRequestToListFirebase(request);
        return addRequestToListLocal(request);
    }

    /* TODO: EXTEND */
    public boolean removeRequest(Request request) {
        removeRequestFromListFirebase(request);
        return removeRequestFromListLocal(request);
    }

    /* TODO: EXTEND & OVERRIDE */
    public boolean addRequestToListLocal(Request request) {
        String requestID = request.getRequester();
        if (containsRequest(requestID)) {  // if request is already in list
            return false;
        }
        requestList.put(requestID, request);
        return true;
    }

    /* TODO: EXTEND & OVERRIDE */
    public boolean removeRequestFromListLocal(Request request) {
        String requestID = request.getRequester();
        if (!containsRequest(requestID)) {  // if request is not in list
            return false;
        }
        requestList.remove(requestID);
        return true;
    }

    /* TODO: EXTEND & OVERRIDE */
    public void addRequestToListFirebase(Request request) {

        if (containsRequest(request)) {  // if request is already in list
            return;
        }

        String requestee = request.getRequestee();
        String requester = request.getRequester();
        String requestDate = request.getRequestDate();
        String requestedBook = request.getRequestedBook();

        Map<String, Object> docData = new LinkedHashMap<>();
        docData.put("requestee", requestee);
        docData.put("requester", requester);
        docData.put("requestDate", requestDate);
        docData.put("requestedBook", requestedBook);

        db.collection("catalogue").document(requestedBook)
                .collection("requests").document(requester)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW_REQUEST", "Request data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("NEW_REQUEST", "Error writing request data!");
                    }
                });
    }

    /* TODO: EXTEND & OVERRIDE */
    public void removeRequestFromListFirebase(Request request) {

        if (!containsRequest(request)) {  // if request is not already in list
            return;
        }

        String requester = request.getRequester();
        String requestedBook = request.getRequestedBook();

        db.collection("catalogue").document(requestedBook)
                .collection("requests").document(requester)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REMOVE_REQUEST", "Request data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("REMOVE_REQUEST", "Error writing request data!");
                    }
                });
    }

    public boolean declineRequest(Request request) {
        if (removeRequest(request)) {
            /* TODO: notify requester of decline */
            /* TODO: update requester's requestedBooks (firebase) */
            /* TODO: update requestee's (currentUser) requestedBooks & set book status to AVAILABLE
                if requestList.size == 0 (local & firebase) */
            return true;
        }
        return false;
    }

    public boolean acceptRequest(Request request) {
        /* TODO: update requester's acceptedBooks (firebase) */
        /* TODO: update requestee's (currentUser) acceptedBooks (local & firebase) */
        /* TODO: decline all other requesters in local requestList */
        /* TODO: empty Firebase requestList */
        return false;
    }

}
