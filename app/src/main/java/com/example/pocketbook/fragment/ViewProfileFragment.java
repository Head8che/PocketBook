package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ViewProfileFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private User profileUser;
    private User currentUser;
    private Fragment viewProfileFragment = this;
    private boolean firstTimeFragLoads = true;

    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;

    TextView layoutFullName;
    TextView layoutEmail;
    TextView layoutUsername;
    ImageView layoutProfilePicture;

    /**
     * View Profile fragment instance that bundles the user information to be accessible/displayed
     * @param profileUser
     * @param currentUser
     * @return
     */
    public static ViewProfileFragment newInstance(User currentUser, User profileUser) {
        ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("VPF_CURRENT_USER", currentUser);
        args.putSerializable("VPF_PROFILE_USER", profileUser);
        viewProfileFragment.setArguments(args);
        return viewProfileFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("VPF_CURRENT_USER");
            this.profileUser = (User) getArguments().getSerializable("VPF_PROFILE_USER");
        }

        Log.e("VPF", profileUser + " " + profileUser.getEmail());

//        if ((profileUser == null) || (profileUser.getEmail() == null)) {
//            return;
//        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue").whereEqualTo("owner",
                profileUser.getEmail()).limit(LIMIT);

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

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(profileUser.getEmail());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("VMBBF_LISTENER", "Listen failed.", e);
                return;
            }

            if ((snapshot != null) && snapshot.exists()) {
                 profileUser = FirebaseIntegrity.getUserFromFirestore(snapshot);

                if ( profileUser == null) {
                    return;
                }

                // if fragment can have a manager; tests crash without this line
                if ((!firstTimeFragLoads) && viewProfileFragment.isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ViewProfileFragment.this)
                            .attach(ViewProfileFragment.this)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    firstTimeFragLoads = false;
                }
            }
            else if (viewProfileFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(ViewProfileFragment.this).commitAllowingStateLoss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View rootView = inflater.inflate(R.layout.fragment_view_profile,
                container, false);
        mBooksRecycler = rootView.findViewById(R.id.viewProfileRecyclerBooks);
        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(profileUser);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), numColumns));
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        mAdapter = new BookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);

        // extract user values into variables
        String firstName =  profileUser.getFirstName();
        String lastName =  profileUser.getLastName();
        String username =  profileUser.getUsername();
        String email =  profileUser.getEmail();

        // access the layout text fields
        layoutFullName = rootView.findViewById(R.id.viewProfileFullName);
        layoutEmail = rootView.findViewById(R.id.viewProfileEmail);
        layoutUsername = rootView.findViewById(R.id.viewProfileUsername);
        layoutProfilePicture = rootView.findViewById(R.id.viewProfileProfilePicture);

        // set the layout text fields to the appropriate user variables
        layoutFullName.setText(String.format("%s %s", firstName, lastName));
        layoutEmail.setText(email);
        layoutUsername.setText(username);

        // load the user's profile picture into ImageLayout
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(userProfilePicture)
                .circleCrop()
                .into(layoutProfilePicture);

//        scrollUpdate = new ScrollUpdate(mQuery, mAdapter, mBooksRecycler);
//        scrollUpdate.load();

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
