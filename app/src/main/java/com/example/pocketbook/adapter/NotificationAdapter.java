package com.example.pocketbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {

    User currentUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options, User currentUser) {
        super(options);
        this.currentUser = currentUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationAdapter.NotificationHolder holder, int position, @NonNull Notification model) {


    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotificationAdapter.NotificationHolder(inflater.inflate(R.layout.item_notification, parent, false));
    }

    static class NotificationHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView description;
        private CircleImageView userProfile;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            //get the views' ids
            username = itemView.findViewById(R.id.itemNotiUsernameTextView);
            description = itemView.findViewById(R.id.itemNotiDescriptionTextView);
            userProfile = itemView.findViewById(R.id.itemNotiProfileImageView);

        }
    }
}