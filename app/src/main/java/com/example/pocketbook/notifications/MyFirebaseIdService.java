package com.example.pocketbook.notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Token token = new Token(refreshToken);
        FirebaseFirestore.getInstance().collection("users")
                .document(firebaseUser.getEmail())
                .update("token",token.getToken());
    }
}
