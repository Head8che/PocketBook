package com.example.pocketbook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.adapter.ProfilePageAdapter;
import com.example.pocketbook.adapter.ViewMyBookPagerAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileExistingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileExistingFragment extends Fragment {
    private User currentUser;
    ListenerRegistration listenerRegistration;
    private boolean firstTimeFragLoads = true;
    private Fragment profileExistingFragment = this;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileExistingFragment() {
        // Required empty public constructor
    }

    public static ProfileExistingFragment newInstance(User user) {
        ProfileExistingFragment profileExistingFragment = new ProfileExistingFragment();
        Bundle args = new Bundle();
        args.putSerializable("PF_USER", user);
        profileExistingFragment.setArguments(args);
        return profileExistingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("PF_USER");
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getEmail());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("OWNER_LISTENER", "Listen failed.", e);
                return;
            }

            if ((snapshot != null) && snapshot.exists()) {
                currentUser = FirebaseIntegrity.getUserFromFirestore(snapshot);

                if (currentUser == null) {
                    return;
                }

                // TODO: Add isAdded to other listeners
                // if fragment can have a manager; tests crash without this line
                if ((!firstTimeFragLoads) && profileExistingFragment.isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ProfileExistingFragment.this)
                            .attach(ProfileExistingFragment.this)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    firstTimeFragLoads = false;
                }
            }
            else if (profileExistingFragment.isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .detach(ProfileExistingFragment.this).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View rootView = inflater.inflate(R.layout.fragment_profile_existing, container, false);

        StorageReference userProfilePicture = FirebaseIntegrity.getUserProfilePicture(currentUser);

        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        String user_Email = currentUser.getEmail();
        ImageView signOut = (ImageView) rootView.findViewById(R.id.profileExistingSignOut);
        ImageView profilePicture = (ImageView) rootView.findViewById(R.id.profileExistingProfilePicture);
        TextView ProfileName = (TextView) rootView.findViewById(R.id.profileExistingFullName);
        TextView UserName = (TextView) rootView.findViewById(R.id.profileExistingUsername);
        TextView Email = (TextView) rootView.findViewById(R.id.profileExistingEmail);
        TextView layoutEditProfile = rootView.findViewById(R.id.profileExistingEditBtn);
        ProfileName.setText(first_Name + ' ' + last_Name);
        UserName.setText(user_Name);
        Email.setText(user_Email);

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

        layoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });


        // access the layout materials
        TabLayout tabLayout = rootView.findViewById(R.id.profileExistingTabLayout);
        ViewPager viewPager = rootView.findViewById(R.id.profileExistingViewPager);
//
//
//        // set up the adapter
        ProfilePageAdapter profilePageAdapter =
                new ProfilePageAdapter(getChildFragmentManager(),
                        tabLayout.getTabCount(), currentUser);
//
        viewPager.setAdapter(profilePageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.e("Tab", String.valueOf(tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return rootView;

    }
}