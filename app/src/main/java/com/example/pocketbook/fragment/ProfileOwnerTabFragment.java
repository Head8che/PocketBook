package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileOwnerTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public ProfileOwnerTabFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewMyBookBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewMyBookBookFragment newInstance(String param1, String param2) {
        ViewMyBookBookFragment fragment = new ViewMyBookBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set up a user, user = FirebaseAuth. setUser ...
        // see selected book's owner

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("default_images").child("no_book_cover_light.png");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_my_book_book, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.viewMyBookBookCoverImageView);

        GlideApp.with(getContext() /* context */)
                .load(storageReference)
                .into(imageView);

        return rootView;
    }

}
