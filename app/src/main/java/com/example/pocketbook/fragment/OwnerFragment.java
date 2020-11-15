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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.ScrollUpdate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Owner Profile Page fragment that contains the user Profile (Books/Info)
 */
public class OwnerFragment extends Fragment {

    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private TextView profileName, userName;
    private TextView editProfile;
    private static final String USERS = "users";
    private User currentUser;
    private ScrollUpdate scrollUpdate;

    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;

    /**
     * Owner Profile fragment instance that bundles the user information to be accessible/displayed
     * @param user
     * @return
     */
    public static OwnerFragment newInstance(User user) {
        OwnerFragment ownerFragment = new OwnerFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        ownerFragment.setArguments(args);
        return ownerFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue").whereEqualTo("owner",currentUser.getEmail()).limit(LIMIT);

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
     * Inflates the layout/container in the respectful fields and fills the fields that require the onwer informationto be displayed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_profile_existing_user, container, false);
        mBooksRecycler = v.findViewById(R.id.profileOwnerRecyclerBooks);
        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), numColumns));
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        mAdapter = new BookAdapter(options, currentUser,getActivity());
        mBooksRecycler.setAdapter(mAdapter);


        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        // TODO: obtain user_photo from firebase
        String user_Pic = currentUser.getPhoto();
        ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_image);
        TextView ProfileName = (TextView) v.findViewById(R.id.profileName);
        TextView UserName = (TextView) v.findViewById(R.id.user_name);
        ProfileName.setText(first_Name + ' ' + last_Name);
        UserName.setText(user_Name);

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(userProfilePicture)
                .circleCrop()
                .into(profilePicture);

        editProfile = v.findViewById(R.id.edit_profile_button);

        scrollUpdate = new ScrollUpdate(mQuery, mAdapter, mBooksRecycler);
        scrollUpdate.load();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        return v;
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
