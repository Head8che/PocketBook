package com.example.pocketbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.LocationActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ViewLocationFragment extends Fragment {

    public ViewLocationFragment() {
        // Required empty public constructor
    }

    public static ViewLocationFragment newInstance() {
        return new ViewLocationFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_location, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.viewLocationBackBtn);
        TextInputEditText layoutSetLocation = (TextInputEditText)
                view.findViewById(R.id.viewLocationField);

        layoutSetLocation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LocationActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

}
