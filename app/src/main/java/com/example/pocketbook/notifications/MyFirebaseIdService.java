package com.example.pocketbook.notifications;


import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.example.pocketbook.util.FirebaseIntegrity.updateToken;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        updateToken();
        }
    }

