package com.example.pocketbook.util;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Notification;

public class NotificationCounter {

    private final TextView notificationNumberCounter;
    private final CardView notificationBubble;

    public NotificationCounter(View view){
        notificationNumberCounter = view.findViewById(R.id.fragmentHomeCounterTextView);
        this.notificationBubble = view.findViewById(R.id.fragmentHomeNotiNumberContainerCardView);
    }

    public void setNotificationNumberCounterInTextView(int counter) {
        int maxNumber = 99;
        int notificationNumber;
        if (counter == 0){
            notificationNumber = counter;
        }
        else if (counter <= maxNumber){
            this.notificationBubble.setVisibility(View.VISIBLE);
            notificationNumber = counter;
        }
        else{
            this.notificationBubble.setVisibility(View.VISIBLE);
            notificationNumber = maxNumber;
        }
        notificationNumberCounter.setText(String.valueOf(notificationNumber));
    }
}
