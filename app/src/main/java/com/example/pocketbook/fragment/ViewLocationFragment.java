package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.type.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ViewLocationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;
    private User currentUser;
    private String date;
    private String time;
    private TextView viewPickupLocation;
    private TextView viewPickupDate;
    private ImageView cover;
    private String selectedDate;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    private final Calendar myCalendar = Calendar.getInstance();
    private TimePickerDialog timePickerDialog;
    private final Calendar myTime = Calendar.getInstance();





    public ViewLocationFragment() {
        // Required empty public constructor
    }

    public static ViewLocationFragment newInstance() {
        ViewLocationFragment fragment = new ViewLocationFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_location, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.setLocationBackBtn);
        viewPickupLocation = (TextView) view.findViewById(R.id.viewPickupLocation);
        viewPickupDate = (TextView) view.findViewById(R.id.viewPickupDateTime);

//        date  = getDate();
//        time = getTime();
//        viewPickupLocation.setText(getLocationName());
//        viewPickupDate.setText(date + '' + time);



        viewPickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        viewPickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivityForResult(intent,100);
//                setLocation.setText("LongLat");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }


}



