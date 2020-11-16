package com.example.pocketbook.fragment;

import android.content.Intent;
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
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.LocationActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.adapter.RequestAdapter;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NotificationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VMBF_BOOK = "VMBF_BOOK";


    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;
    private User currentUser;
    Button setLocation;



    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_set_location, container, false);
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.notificationBackBtn);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("USER","USERRRR");
                getActivity().onBackPressed();
            }
        });



        return view;
    }
}



