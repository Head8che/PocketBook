package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.NotificationAdapter;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


import static com.example.pocketbook.util.FirebaseIntegrity.deleteNotificationFromFirebase;
import static com.example.pocketbook.util.FirebaseIntegrity
        .getAllNotificationsForCurrentUserFromFirebase;
import static com.example.pocketbook.util.FirebaseIntegrity.setAllNotificationsToSeenTrue;

public class NotificationsFragment extends Fragment {

    private NotificationAdapter notificationAdapter;
    private User currentUser;
    private ArrayList<String> notifications;
    private FirestoreRecyclerOptions<Notification> options;
    private ListenerRegistration listenerRegistration;


    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of NotificationsFragment
     * @param currentUser current user
     * @return NotificationsFragment
     */
    public static NotificationsFragment newInstance(User currentUser) {
        NotificationsFragment NotificationsFragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("CURRENTUSER", currentUser);
        NotificationsFragment.setArguments(args);
        return NotificationsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the user argument passed to the newInstance() method
        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("CURRENTUSER");
        }
        if (currentUser != null)
            // set all the seen attribute in all notifications to true
            setAllNotificationsToSeenTrue(currentUser);

        // Initialize Firestore
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all user notifications
        Query mQuery = mFirestore.collection("users")
                .document(currentUser.getEmail())
                .collection("notifications");

        options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(mQuery, Notification.class)
                .build();

        EventListener<QuerySnapshot> dataListener = (snapshots, error) -> {
            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (error != null) {
                        Log.e("NOTIFICATION_SCROLL_UPDATE_ERROR",
                                "Listen failed.", error);
                        return;
                    }

                    DocumentSnapshot document = dc.getDocument();
                    switch (dc.getType()) {
                            case ADDED:
                                Log.d("SCROLL_UPDATE", "New doc: " + document);
                                notifications = getAllNotificationsForCurrentUserFromFirebase(
                                        currentUser);
                                notificationAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                notifications = getAllNotificationsForCurrentUserFromFirebase(
                                        currentUser);
                                notificationAdapter.notifyDataSetChanged();
                                break;

                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications,
                container, false);
        ImageView backButton = view.findViewById(R.id.notificationBackBtn);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                backButton.setClickable(false);
                getActivity().onBackPressed();
                backButton.setClickable(true);
            }
        });

        //get the id of the recycler for this layout and set its layout manager
        RecyclerView notificationsRecycler = view.findViewById(R.id.notificationsViewRecyclerView);
        notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //set a new requestAdapter as an adapter for the recycler
        notificationAdapter = new NotificationAdapter(options, currentUser);
        notificationsRecycler.setAdapter(notificationAdapter);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationsRecycler);



        return view;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        notificationAdapter.stopListening();
    }

    // Item touch helper to implement enable swiping on notifications
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback
            = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // delete notification when the user swipes on it
            deleteNotificationFromFirebase(notifications,
                    viewHolder.getAdapterPosition(),currentUser.getEmail());
            notificationAdapter.notifyDataSetChanged();
        }
    };
}
