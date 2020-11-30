package com.example.pocketbook.util;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Notification;

// class for the notification counter displayed in the app
public class NotificationCounter {

    private final TextView notificationNumberCounter;
    private final CardView notificationBubble;

    /**
     * constructor for the NotificationCounter class
     * @param view  the view where the counter is displayed
     */
    public NotificationCounter(View view){
        notificationNumberCounter = view.findViewById(R.id.fragmentHomeCounterTextView);
        this.notificationBubble = view.findViewById(R.id.fragmentHomeNotiNumberContainerCardView);
    }

    /**
     * updates the notification counter in the app
     * @param counter  the number of unseen notifications for the user
     */
    public void setNotificationNumberCounterInTextView(int counter) {
        int maxNumber = 99; // max number that  can be displayed
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
