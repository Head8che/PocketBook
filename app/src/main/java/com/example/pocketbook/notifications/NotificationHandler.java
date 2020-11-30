package com.example.pocketbook.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.pocketbook.util.FirebaseIntegrity.pushNewNotificationToFirebase;

// handles storing the data for different types of notification and sending the data in a RemoteMessage using FirebaseMessaging
public class NotificationHandler {

    // APIservice used for notification messaging
    private static final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    /**
     * verify if the user has non null token
     * @param documentSnapshot
     * @return
     */
    private static String verifyReceiverTokenNotNull(DocumentSnapshot documentSnapshot){
        if (documentSnapshot.get("token") != null){
            return documentSnapshot.get("token").toString();
        }
        else{
            return null;
        }
    }


    /**
     * send notification to a user when the currentUser requests a book owned by that user
     * @param currentUser  the user currently using the app
     * @param book  the book being requested
     */
    public static void sendNotificationBookRequested(User currentUser,Book book){

        String msg = String.format("%s has requested '%s'",
                currentUser.getUsername(), book.getTitle());// the message to be sent
        Notification notification = new Notification(msg,
                currentUser.getEmail(), book.getOwner(),
                book.getId(), false, "BOOK_REQUESTED");// create a new notification

        FirebaseFirestore.getInstance().collection("users").document(book.getOwner()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String userToken = verifyReceiverTokenNotNull(task.getResult());
                            if (userToken!=null) {
                                Log.d("NotiBookRequested",userToken);
                                Data data = new Data(msg, "New Request", notification.getNotificationDate(), notification.getType(), R.drawable.ic_logo_vector, notification.getReceiver());
                                pushNewNotificationToFirebase(notification); // store notification in firebase to view it in the app
                                sendNotification(userToken, data); // send the RemoteMessage to the other user carrying the data
                            }
                            else{
                                pushNewNotificationToFirebase(notification);
                            }
                        }
                    }
                });

    }

    /**
     * send a notification to a user when the current user declined their request
     * @param request  the request object carrying info about the request
     * @param book  the book its request got declined
     */
    public static void sendNotificationRequestDeclined(Request request, Book book){

        String msg = String.format("Your request for '%s' has been declined", book.getTitle());
        Notification notification = new Notification(msg, book.getOwner(), request.getRequester(), book.getId(), false, "REQUEST_DECLINED");

        // send a notification to the requester
        FirebaseFirestore.getInstance().collection("users")
                .document(request.getRequester())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String userToken = verifyReceiverTokenNotNull(document);
                        if(userToken!=null){
                            if (task.isSuccessful()){
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(request.getRequestee())
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document1 = task1.getResult();
                                                if (document1 != null) {
                                                    User requestee = FirebaseIntegrity.getUserFromFirestore(document1);
                                                    Data data = new Data(msg, "Request Declined",
                                                            notification.getNotificationDate(),
                                                            notification.getType(), R.drawable.ic_logo_vector,
                                                            notification.getReceiver());
                                                    pushNewNotificationToFirebase(notification);
                                                    sendNotification(userToken, data);
                                                }
                                            }
                                    });
                    }}}
                    else{
                        pushNewNotificationToFirebase(notification);
                    }


    });
    }

    /**
     * send notification to a user when currentUser accepts their request
     * @param request  request object carrying information about the request
     * @param book  the book its request was accepted
     */
    public static void sendNotificationRequestAccepted(Request request, Book book) {
        String msg = String.format("Your request for '%s' has " + "been accepted", book.getTitle());
        Notification notification = new Notification(msg, book.getOwner(), request.getRequester(), book.getId(), false, "REQUEST_ACCEPTED");
        FirebaseFirestore.getInstance().collection("users")
                    .document(request.getRequester())
                    .get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot document = task.getResult();
                        String userToken = verifyReceiverTokenNotNull(document);
                        if (userToken != null){
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(request.getRequestee())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document1 = task1.getResult();
                                            if (document1 != null) {
                                                User requestee = FirebaseIntegrity.getUserFromFirestore(document1);

                                                    Log.d("Notirequestaccepted",userToken);
                                                    Data data = new Data(msg,
                                                            "Request Accepted",
                                                            notification.getNotificationDate(),
                                                            notification.getType(),
                                                            R.drawable.ic_logo_vector,
                                                            notification.getReceiver());
                                                    pushNewNotificationToFirebase(notification);
                                                    sendNotification(userToken, data);


                                            }

                                        }
                                    });
                        }
                        }
                        else{
                            pushNewNotificationToFirebase(notification);
                        }
                    });
        }

    /**
     * send RemoteMessage carrying the notification's data to a user with a token
      * @param token  the token of the user receiving the notification as a String
     * @param data  the data of the notification as a Data object
     */
    private static void sendNotification(String token,Data data) {

        Sender sender = new Sender(data, token); // a sender object carrying the data and the token of the receiver

        // send RemoteMessage
        apiService.sendNotification(sender).enqueue((new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Log.d("FAILED_TO_SEND_NOTIFICATION","response.body().success != 1");
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        }));
    }
}
