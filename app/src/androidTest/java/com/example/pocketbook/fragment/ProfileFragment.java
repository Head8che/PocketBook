package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pocketbook.activity.EditBookActivity;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.ScrollUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private BookList ownedBooks = new BookList();
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private TextView profileName, userName;
    private TextView editProfile;
    private static final String USERS = "users";
    private User currentUser;
    private ScrollUpdate scrollUpdate;


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
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Query to retrieve all books
        mQuery = mFirestore.collection("catalogue").limit(LIMIT);


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

                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ProfileFragment.this)
                            .attach(ProfileFragment.this)
                            .commitAllowingStateLoss();
                } else {
                    if ( getActivity() == null) {
                        getParentFragmentManager().beginTransaction()
                                .detach(ProfileFragment.this).commitAllowingStateLoss();
                    } else {
                        getActivity().getFragmentManager().popBackStack();
                    }
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
        mBooksRecycler = v.findViewById(R.id.recycler_books);
        StorageReference userProfilePicture = currentUser.getProfilePicture();
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), NUM_COLUMNS));
        mAdapter = new BookAdapter(currentUser, ownedBooks, getActivity());
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

        scrollUpdate = new ScrollUpdate(ownedBooks, mQuery, mAdapter, mBooksRecycler);
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

}