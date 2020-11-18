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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SetLocationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView requestsRecycler;
    private RequestAdapter requestAdapter;
    private Book book;
    private User currentUser;
    TextInputEditText setLocation;
    TextInputEditText setDate;
    TextInputEditText setTime;
    ImageView cover;
    String selectedDate;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    final Calendar myCalendar = Calendar.getInstance();
    private TimePickerDialog timePickerDialog;
    final Calendar myTime = Calendar.getInstance();
    String date_time = "";
    String latEiffelTower = "48.858235";
    String lngEiffelTower = "2.294571";
    String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=200x200&sensor=false&key=AIzaSyAuHw4wNX8nFZ5IVahC5J4lYEX1s5Msey8";



    public SetLocationFragment() {
        // Required empty public constructor
    }

    public static SetLocationFragment newInstance() {
        SetLocationFragment fragment = new SetLocationFragment();
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
        setLocation = (TextInputEditText) view.findViewById(R.id.setPickup);
        setDate = (TextInputEditText) view.findViewById(R.id.setDate);
        setTime = (TextInputEditText) view.findViewById(R.id.setTime);







        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                setTime.setText(String.valueOf(hourOfDay)+"Hours "+String.valueOf(minute)+" minutes ");
                if (view.isShown()) {
                    String myFormat = "HH:MM:k"; //In which you need put here
                    myTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myTime.set(Calendar.MINUTE, minute);
                    @SuppressLint
                            ("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a ");
                    setTime.setText(dateFormatter.format(myTime.getTime()));
                }
            }
        };

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                new TimePickerDialog(getContext(), time, myTime
                        .get(Calendar.HOUR_OF_DAY), myTime.get(Calendar.MINUTE), true).show();
            }
        });



        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                String myFormat = "MM/dd/yy";
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                setDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });





        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                setLocation.setText("LongLat");
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



