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
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Exchange;
import com.example.pocketbook.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class ViewLocationFragment extends Fragment {

    Exchange exchange;

    public ViewLocationFragment() {
        // Required empty public constructor
    }

    public static ViewLocationFragment newInstance(Exchange exchange) {

        ViewLocationFragment viewLocationFragment = new ViewLocationFragment();
        Bundle args = new Bundle();
        args.putSerializable("VBF_EXCHANGE", exchange);
        viewLocationFragment.setArguments(args);
        return viewLocationFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the arguments passed to the fragment as a bundle
        if (getArguments() != null) {
            this.exchange = (Exchange) getArguments().getSerializable("VBF_EXCHANGE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_location, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.viewLocationBackBtn);

        TextInputEditText layoutViewLocation = (TextInputEditText)
                view.findViewById(R.id.viewLocationField);
        TextInputEditText layoutViewDate = (TextInputEditText)
                view.findViewById(R.id.viewLocationDateField);
        TextInputEditText layoutViewTime = (TextInputEditText)
                view.findViewById(R.id.viewLocationTimeField);

        layoutViewLocation.setText(exchange.getMeetingDetails().getAddress());
        layoutViewDate.setText(exchange.getMeetingDetails().getMeetingDate());
        layoutViewTime.setText(exchange.getMeetingDetails().getMeetingTime());

//        layoutViewLocation.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), LocationActivity.class);
//            startActivity(intent);
//        });

        backButton.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

}
