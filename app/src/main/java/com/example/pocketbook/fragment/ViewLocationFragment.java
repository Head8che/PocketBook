package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Exchange;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;

public class ViewLocationFragment extends Fragment implements OnMapReadyCallback {

    Exchange exchange;
    GoogleMap googleMap = null;
    Marker marker;
    SupportMapFragment mapFrag;

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
        View view = inflater.inflate(R.layout.fragment_view_location,
                container, false);
        ImageView backButton = view.findViewById(R.id.viewLocationBackBtn);

        TextInputEditText layoutViewLocation = view.findViewById(R.id.viewLocationField);
        TextInputEditText layoutViewDate = view.findViewById(R.id.viewLocationDateField);
        TextInputEditText layoutViewTime = view.findViewById(R.id.viewLocationTimeField);

        TextView viewLocationTitle = view.findViewById(R.id.viewLocationTitle);
        if ((exchange.getBorrowerBookStatus().equals("BORROWED"))) {
            viewLocationTitle.setText(R.string.viewReturnLocation);
        }

        layoutViewLocation.setText(exchange.getMeetingDetails().getAddress());
        layoutViewDate.setText(exchange.getMeetingDetails().getMeetingDate());
        layoutViewTime.setText(exchange.getMeetingDetails().getMeetingTime());

        if (this.googleMap == null) {
            mapFrag = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.viewLocationFragMap);
            if (mapFrag != null) {
                mapFrag.getMapAsync(this);
            }
        }

        // go back when backButton is clicked
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                backButton.setClickable(false);
                getActivity().onBackPressed();
                backButton.setClickable(true);
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {

            String address = exchange.getMeetingDetails().getAddress();
            double latitude = exchange.getMeetingDetails().getLatitude();
            double longitude = exchange.getMeetingDetails().getLongitude();

            Log.e("ADDRESS", address + " " + latitude + " " + longitude);

            if (marker != null) {
                marker.remove();
            }
            LatLng mPinnedMap = new LatLng(latitude, longitude);

            MarkerOptions options = new MarkerOptions()
                    .draggable(true)
                    .title(address)
                    .position(mPinnedMap);

            if (this.googleMap != null) {
                marker = this.googleMap.addMarker(options);
            }

            marker.setTitle(address);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPinnedMap,15));
        }
    }
}
