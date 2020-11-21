package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.LocationActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Exchange;
import com.example.pocketbook.model.MeetingDetails;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class SetLocationFragment extends Fragment {

    private Book book;
    private Request request;
    private TextInputEditText setLocation;
    private TextInputEditText setDate;
    private TextInputEditText setTime;
    private Button confirmBtn;
    private ImageView cover;
    private String selectedDate;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    private double invalidCoord = -999.0;
    private final Calendar myCalendar = Calendar.getInstance();
    private TimePickerDialog timePickerDialog;
    private final Calendar myTime = Calendar.getInstance();

    private double latitude;
    private double longitude;
    private String address;
    private String meetingDate;
    private String meetingTime;

    private boolean validLocation;
    private boolean validDate;
    private boolean validTime;

    String bookOwner;
    String bookRequester;

    public SetLocationFragment() {
        // Required empty public constructor
    }

    public static SetLocationFragment newInstance(Book book, Request request,
                                                  String bookOwner, String bookRequester) {
        SetLocationFragment setLocationFragment = new SetLocationFragment();
        Bundle args = new Bundle();
        args.putSerializable("SLF_BOOK", book);
        args.putSerializable("SLF_REQUEST", request);
        args.putSerializable("SLF_BOOK_OWNER", bookOwner);
        args.putSerializable("SLF_BOOK_REQUESTER", bookRequester);
        setLocationFragment.setArguments(args);
        return setLocationFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validLocation = false;
        validDate = false;
        validTime = false;

        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("SLF_BOOK");
            this.request = (Request) getArguments().getSerializable("SLF_REQUEST");
            this.bookOwner = (String) getArguments().getSerializable("SLF_BOOK_OWNER");
            this.bookRequester = (String) getArguments().getSerializable("SLF_BOOK_REQUESTER");
        }

        this.latitude = invalidCoord;
        this.longitude = invalidCoord;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_location, container, false);
        ImageView backButton = (ImageView) view.findViewById(R.id.setLocationBackBtn);
        setLocation = (TextInputEditText) view.findViewById(R.id.setPickup);
        setDate = (TextInputEditText) view.findViewById(R.id.setDate);
        setTime = (TextInputEditText) view.findViewById(R.id.setTime);
        confirmBtn = (Button) view.findViewById(R.id.confirmPickupBtn);

        TimePickerDialog.OnTimeSetListener time = (view1, hourOfDay, minute) -> {
//                setTime.setText(String.valueOf(hourOfDay)+"Hours "+String.valueOf(minute)+" minutes ");
            if (view1.isShown()) {
                String myFormat = "HH:mm"; // In which you need put here
                myTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myTime.set(Calendar.MINUTE, minute);
                @SuppressLint ("SimpleDateFormat") SimpleDateFormat dateFormatter
                        = new SimpleDateFormat(myFormat);
                setTime.setText(dateFormatter.format(myTime.getTime()));
                if ((setTime.getText() != null) && !(setTime.getText().toString().equals(""))) {
                    meetingTime = setTime.getText().toString();
                    validTime = true;
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


        confirmBtn.setOnClickListener(v -> {
            // TODO: Need to Notify the user of Accept and Decline all other Requests
            if (validLocation && validDate && validTime
                    && (latitude != invalidCoord) && (longitude != invalidCoord)) {
                String exchangeID = UUID.randomUUID().toString();
                MeetingDetails meetingDetails = new MeetingDetails(latitude,
                        longitude, address, meetingDate, meetingTime);

                String ownerBookStatus = (book.getStatus().equals("REQUESTED"))
                        ? "ACCEPTED" : book.getStatus();
                String borrowerBookStatus = (book.getStatus().equals("REQUESTED"))
                        ? "ACCEPTED" : book.getStatus();

                Exchange exchange = new Exchange(exchangeID, book.getId(), bookOwner,
                        bookRequester, ownerBookStatus, borrowerBookStatus, meetingDetails);

                if ((meetingDetails.getAddress() == null)
                        || (exchange.getExchangeId() == null)) {
                    Toast.makeText(getContext(), "Invalid Details",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Valid Details",
                            Toast.LENGTH_SHORT).show();
                    FirebaseIntegrity.pushNewExchangeToFirebase(exchange);
                    FirebaseIntegrity.acceptBookRequest(request);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });



        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                String myFormat = "yyyy/MM/dd";
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                setDate.setText(sdf.format(myCalendar.getTime()));

                if ((setDate.getText() != null) && !(setDate.getText().toString().equals(""))) {
                    meetingDate = setDate.getText().toString();
                    validDate = true;
                }
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
                startActivityForResult(intent,REQUEST_CODE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                latitude = (double) data.getSerializableExtra("Lat");
                longitude = (double) data.getSerializableExtra("Lng");
                address = (String) data.getSerializableExtra("Address");

                setLocation.setText(address);

                if ((setLocation.getText() != null)
                        && !(setLocation.getText().toString().equals(""))) {
                    validLocation = true;
                }

            }
        }
    }




}



