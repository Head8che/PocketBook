package com.example.pocketbook.notifications;

import android.util.Log;
import android.widget.Toast;

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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.pocketbook.util.FirebaseIntegrity.pushNewNotificationToFirebase;

public class NotificationHandler {

    private static APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    public static String verifyReceiverTokenNotNull(DocumentSnapshot documentSnapshot){
        return Objects.requireNonNull(documentSnapshot.get("token")).toString();
    }

    public static void sendNotificationBookRequested(User currentUser,Book book){

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(book.getOwner())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String userToken = verifyReceiverTokenNotNull(task.getResult());
                            String msg = String.format("%s has requested '%s'",
                                    currentUser.getUsername(), book.getTitle());
                            Notification notification = new Notification(msg,
                                    currentUser.getEmail(), book.getOwner(),
                                    book.getId(), false, "BOOK_REQUESTED");
                            Data data = new Data(msg, "New Request",
                                    notification.getNotificationDate(),
                                    notification.getType(),
                                    R.drawable.ic_logo_vector,
                                    notification.getReceiver());
                            pushNewNotificationToFirebase(notification);
                            sendNotification(userToken,data);
                        }
                    }
                });
    }

    public static void sendNotificationRequestDeclined(Request request, Book book){
        //send a notification to the requester
        FirebaseFirestore.getInstance().collection("users")
                .document(request.getRequester())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(request.getRequestee())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document != null){
                                            User requestee = FirebaseIntegrity.getUserFromFirestore(document);
                                            String userToken = verifyReceiverTokenNotNull(document);
                                            String msg = String.format("Your request for '%s' has " + "been declined", book.getTitle());
                                            Notification notification = new Notification(msg, book.getOwner(), request.getRequester(), book.getId(), false, "REQUEST_DECLINED");
                                            Data data = new Data(msg,
                                                    "Request Declined",
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
    });
    }

    public static void sendNotificationRequestAccepted(Request request, Book book){
        FirebaseFirestore.getInstance().collection("users")
                .document(request.getRequester())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(request.getRequestee())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document != null){
                                            User requestee = FirebaseIntegrity.getUserFromFirestore(document);
                                            String userToken = verifyReceiverTokenNotNull(document);
                                            String msg = String.format("Your request for '%s' has " + "been accepted", book.getTitle());
                                            Notification notification = new Notification(msg, book.getOwner(), request.getRequester(), book.getId(), false, "REQUEST_ACCEPTED");
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
                });
    }

    private static void sendNotification(String token,Data data) {

        Sender sender = new Sender(data, token);
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
