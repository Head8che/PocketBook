package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pocketbook.activity.EditBookActivity;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.ScrollUpdate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

/**
 * Profile Page fragment that contains the user Profile (Books/Info)
 */
public class ProfileFragment extends Fragment {
    private static final int NUM_COLUMNS = 2;
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
    private Fragment profileFragment = this;

    private int itemCount;

    FirestoreRecyclerOptions<Book> options;
    ListenerRegistration listenerRegistration;

    /**
     * Profile fragment instance that bundles the user information to be accessible/displayed
     * @param user
     * @return
     */
    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        profileFragment.setArguments(args);
        return profileFragment;
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
            this.itemCount = 0;
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Query to retrieve all books
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
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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
                                .detach(ProfileFragment.this)
                                .attach(ProfileFragment.this)
                                .commitAllowingStateLoss();
                    }
                } else if (profileFragment.isAdded()) {
                    getParentFragmentManager().beginTransaction()
                            .detach(ProfileFragment.this).commitAllowingStateLoss();
                }
            }

        });

    }

    /**
     * Inflates the layout/container in the respectful fields and fills the fields that require the user information
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
        View v = inflater.inflate(R.layout.fragment_profile_new_user, container, false);
        mBooksRecycler = v.findViewById(R.id.profileNewRecyclerBooks);
        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), NUM_COLUMNS));
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        mAdapter = new BookAdapter(options, currentUser, getActivity());
        mBooksRecycler.setAdapter(mAdapter);


        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        String user_Email = currentUser.getEmail();

        // TODO: obtain user_photo from firebase
        ImageView signOut = (ImageView) v.findViewById(R.id.profileNewSignOut);
        ImageView profilePicture = (ImageView) v.findViewById(R.id.profileNewProfilePicture);
        TextView ProfileName = (TextView) v.findViewById(R.id.profileNewFullName);
        TextView Email = (TextView) v.findViewById(R.id.profileNewEmail);
        TextView UserName = (TextView) v.findViewById(R.id.profileNewUsername);
        ProfileName.setText(first_Name + ' ' + last_Name);
        UserName.setText(user_Name);
        Email.setText(user_Email);

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
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
            Objects.requireNonNull(getActivity()).finishAffinity();
        });

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(userProfilePicture)
                .circleCrop()
                .into(profilePicture);

        editProfile = v.findViewById(R.id.profileNewEditBtn);

//        scrollUpdate = new ScrollUpdate(ownedBooks, mQuery, mAdapter, mBooksRecycler);
//        scrollUpdate.load();

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