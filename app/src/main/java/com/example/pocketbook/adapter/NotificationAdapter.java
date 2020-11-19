package com.example.pocketbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

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

        holder.description.setText(model.getMessage());

        FirebaseFirestore.getInstance().collection("users").document(model.getSender())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        User sender = FirebaseIntegrity.getUserFromFirestore(document);
                        holder.username.setText(sender.getUsername());
                        if (sender != null) {
                            GlideApp.with(Objects.requireNonNull(holder.itemView.getContext()))
                                    .load(FirebaseIntegrity.getUserProfilePicture(sender))
                                    .into(holder.userProfile);
                    }
                }
                });

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