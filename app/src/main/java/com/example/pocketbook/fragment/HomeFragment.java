package com.example.pocketbook.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.NotificationCounter;
import com.example.pocketbook.util.ScrollUpdate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.pocketbook.util.FirebaseIntegrity.setNotificationCounterNumber;

/**
 * Home Page fragment that contains a wide range of books on the platform
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HOME_ACTIVITY";
    private static final int NUM_COLUMNS = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private NotificationCounter notificationCounter;

    private User currentUser;

    private ScrollUpdate scrollUpdate;
    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;
    /**
     * Home Page fragment instance that bundles the user/catalogue to be displayed
     * @param user
     * @return
     */
    public static HomeFragment newInstance(User user) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("HF_USER", user);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("HF_USER");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Retrieving books that do not belong to user
        mQuery = mFirestore.collection("catalogue")
                .whereNotEqualTo("owner",currentUser.getEmail()).limit(LIMIT);

        options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();

        EventListener<QuerySnapshot> dataListener = (snapshots, error) -> {
            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (error != null) {
                        Log.e("SCROLL_UPDATE_ERROR", "Listen failed.", error);
                        return;
                    }

                    DocumentSnapshot document = dc.getDocument();

                    Book book = FirebaseIntegrity.getBookFromFirestore(document);

                    if (book != null) {

                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("SCROLL_UPDATE", "New doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("SCROLL_UPDATE", "Modified doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("SCROLL_UPDATE", "Removed doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);
    }
    /**
     * Inflates the layout/container with the following (Layout and Books)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        mAdapter = new BookAdapter(options, currentUser, getActivity());

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Button notificationBtn = (Button) rootView.findViewById(R.id.homeFragmentNotificationBtn);
        mBooksRecycler = rootView.findViewById(R.id.recycler_books);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), NUM_COLUMNS));

        mBooksRecycler.setAdapter(mAdapter);

//        scrollUpdate = new ScrollUpdate(catalogue, mQuery, mAdapter, mBooksRecycler);
//        scrollUpdate.load();

        notificationBtn.setOnClickListener(v -> {
            NotificationsFragment nextFragment = NotificationsFragment.newInstance(currentUser);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, nextFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        });
        notificationCounter = new NotificationCounter(rootView);
        setNotificationCounterNumber(notificationCounter,currentUser);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
