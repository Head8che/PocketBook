package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.NotificationAdapter;
import com.example.pocketbook.adapter.RequestAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationsFragment extends Fragment {




    private RecyclerView notificationsRecycler;
    private NotificationAdapter notificationAdapter;
    private User currentUser;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FirestoreRecyclerOptions<Notification> options;
    ListenerRegistration listenerRegistration;


    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of NotificationsFragment
     * @return
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

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all book requests
        mQuery = mFirestore.collection("users").document(currentUser.getEmail())
                .collection("notifications");

        options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(mQuery, Notification.class)
                .build();

        EventListener<QuerySnapshot> dataListener = (snapshots, error) -> {
            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (error != null) {
                        Log.e("NOTIFICATION_SCROLL_UPDATE_ERROR", "Listen failed.", error);
                        return;
                    }

                    DocumentSnapshot document = dc.getDocument();
                    switch (dc.getType()) {
                            case ADDED:
                                Log.d("NOTIFICATION_SCROLL_UPDATE", "New doc: " + document);

                                notificationAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("NOTIFICATION_SCROLL_UPDATE", "Modified doc: " + document);

                                notificationAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("NOTIFICATION_SCROLL_UPDATE", "Removed doc: " + document);

                                notificationAdapter.notifyDataSetChanged();
                                break;

                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.notificationBackBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //get the id of the recycler for this layout and set its layout manager
        notificationsRecycler = view.findViewById(R.id.notificationsViewRecyclerView);
        notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //set a new requestAdapter as an adapter for the recycler
        notificationAdapter = new NotificationAdapter(options, currentUser);
        notificationsRecycler.setAdapter(notificationAdapter);

        return view;
    }
}



