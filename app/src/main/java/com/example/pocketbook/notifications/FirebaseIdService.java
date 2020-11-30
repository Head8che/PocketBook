package com.example.pocketbook.notifications;


import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.example.pocketbook.util.FirebaseIntegrity.updateToken;
// FirebaseIdService class to handle token updates for users
public class FirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        updateToken();
        }
    }

