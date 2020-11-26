package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Profile Page fragment that contains the user Profile (Books/Info)
 */
public class ProfileNewFragment extends Fragment {
    private static final int NUM_COLUMNS = 2;
    private BookAdapter mAdapter;
    private User currentUser;
    private Fragment profileFragment = this;

    private int itemCount;

    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;

    /**
     * Profile fragment instance that bundles the user information to be accessible/displayed
     * @param user current user
     * @return ProfileNewFragment
     */
    public static ProfileNewFragment newInstance(User user) {
        ProfileNewFragment profileNewFragment = new ProfileNewFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        profileNewFragment.setArguments(args);
        return profileNewFragment;
    }

    /**
     * Obtains and create the information/data required for this screen.
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
            this.itemCount = 0;
        }

        // Initialize Firestore
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Retrieving books that do not belong to user
        Query mQuery = mFirestore.collection("catalogue")
                .whereNotEqualTo("owner", currentUser.getEmail());

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
                                itemCount += 1;

                                mAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                Log.d("SCROLL_UPDATE", "Modified doc: " + document);

                                mAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                Log.d("SCROLL_UPDATE", "Removed doc: " + document);
                                itemCount -= 1;

                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        };

        listenerRegistration = mQuery.addSnapshotListener(dataListener);


        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("VMBBF_LISTENER", "Listen failed.", e);
                return;
            }

            if ((snapshot != null) && snapshot.exists()) {

                currentUser = FirebaseIntegrity.getUserFromFirestore(snapshot);

                // TODO; Add isAdded to other listeners
                // if fragment can have a manager; tests crash without this line
                if (profileFragment.isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ProfileNewFragment.this)
                            .attach(ProfileNewFragment.this)
                            .commitAllowingStateLoss();
                }
            } else if (profileFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(ProfileNewFragment.this).commitAllowingStateLoss();
            }
        });

    }

    /**
     * Inflates the layout/container in the respectful fields
     * and fills the fields that require the user information
     * @param inflater layout inflater
     * @param container ViewGroup container
     * @param savedInstanceState saved instance state
     * @return inflated layout
     */
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View rootView = inflater.inflate(R.layout.fragment_profile_new_user, container, false);
        RecyclerView mBooksRecycler = rootView.findViewById(R.id.profileNewRecyclerBooks);
        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(rootView.getContext(), NUM_COLUMNS));
        mAdapter = new BookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);


        String firstName = currentUser.getFirstName();
        String lastName = currentUser.getLastName();
        String username = currentUser.getUsername();
        String userEmail = currentUser.getEmail();

        // TODO: obtain user_photo from firebase
        ImageView signOut = rootView.findViewById(R.id.profileNewSignOut);
        ImageView profilePicture = rootView.findViewById(R.id.profileNewProfilePicture);
        TextView ProfileName = rootView.findViewById(R.id.profileNewFullName);
        TextView Email = rootView.findViewById(R.id.profileNewEmail);
        TextView UserName = rootView.findViewById(R.id.profileNewUsername);
        TextView layoutEditProfile = rootView.findViewById(R.id.profileNewEditBtn);

        ProfileName.setText(String.format("%s %s", firstName, lastName));
        UserName.setText(username);
        Email.setText(userEmail);

        int rowCount = (int) Math.ceil((double) itemCount / NUM_COLUMNS);
        int recyclerSizeInDp = (rowCount * (295 + 40));  // rows * (bookHeight + padding)

        DisplayMetrics displayMetrics = Objects
                .requireNonNull(getContext()).getResources().getDisplayMetrics();
        int recyclerSize = Math.round(recyclerSizeInDp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        mBooksRecycler.setMinimumHeight(recyclerSize);

//        Log.e("ADAPTER_COUNT", mAdapter.getItemCount() + " item: " + itemCount);
//        Log.e("RECYLCER_SIZE", 10000 + " recyclerSize: " + recyclerSize);

        signOut.setColorFilter(ContextCompat
                        .getColor(Objects.requireNonNull(getActivity()).getBaseContext(),
                                R.color.colorAccent),
                android.graphics.PorterDuff.Mode.SRC_IN);

        signOut.setOnClickListener(v1 -> {
            signOut.setClickable(false);
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
            Objects.requireNonNull(getActivity()).finishAffinity();
            signOut.setClickable(true);
        });

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(userProfilePicture)
                .circleCrop()
                .into(profilePicture);

        layoutEditProfile.setOnClickListener(v -> {
            layoutEditProfile.setClickable(false);
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
            layoutEditProfile.setClickable(true);
        });

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