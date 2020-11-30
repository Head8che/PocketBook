package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.LocationActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Exchange;
import com.example.pocketbook.model.MeetingDetails;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.example.pocketbook.util.Parser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.example.pocketbook.notifications
        .NotificationHandler.sendNotificationRequestAccepted;

public class SetLocationFragment extends Fragment implements OnMapReadyCallback {

    private Book book;
    private Request request;
    private TextInputEditText layoutSetLocation;
    private TextInputEditText layoutSetDate;
    private TextInputEditText layoutSetTime;

    private TextInputLayout layoutSetLocationContainer;
    private TextInputLayout layoutSetDateContainer;
    private TextInputLayout layoutSetTimeContainer;

    public static final int REQUEST_CODE = 11; // Used to identify the result
    private double invalidCoord = -999.0;
    private final Calendar myCalendar = Calendar.getInstance();
    private final Calendar myTime = Calendar.getInstance();

    private double latitude;
    private double longitude;
    private String address;
    private String meetingDate;
    private String meetingTime;

    private boolean validLocation;
    private boolean validDate;
    private boolean validTime;

    private String bookOwner;
    private String bookRequester;
    private User currentUser;

    private GoogleMap googleMap = null;
    private Marker marker;
    private SupportMapFragment mapFrag;

    public SetLocationFragment() {
        // Required empty public constructor
    }

    public static SetLocationFragment newInstance(Book book, Request request,
                                                  String bookOwner, String bookRequester,
                                                  User currentUser) {
        SetLocationFragment layoutSetLocationFragment = new SetLocationFragment();
        Bundle args = new Bundle();
        args.putSerializable("SLF_BOOK", book);
        args.putSerializable("SLF_REQUEST", request);
        args.putSerializable("SLF_BOOK_OWNER", bookOwner);
        args.putSerializable("SLF_CURRENT_USER", currentUser);
        args.putSerializable("SLF_BOOK_REQUESTER", bookRequester);
        layoutSetLocationFragment.setArguments(args);
        return layoutSetLocationFragment;
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
            this.currentUser = (User) getArguments().getSerializable("SLF_CURRENT_USER");
            this.bookRequester = (String) getArguments().getSerializable("SLF_BOOK_REQUESTER");
        }

        this.latitude = invalidCoord;
        this.longitude = invalidCoord;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_location,
                container, false);
        ImageView backButton = view.findViewById(R.id.setLocationBackBtn);
        layoutSetLocation = view.findViewById(R.id.setLocationField);
        layoutSetDate = view.findViewById(R.id.setDateField);
        layoutSetTime = view.findViewById(R.id.setTimeField);
        Button confirmBtn = view.findViewById(R.id.confirmPickupBtn);

        TextView setLocationTitle = view.findViewById(R.id.setLocationTitle);
        if (currentUser != null) {
            setLocationTitle.setText(R.string.setReturnLocation);
        }

        // access the layout text containers
        layoutSetLocationContainer = view.findViewById(R.id.setLocationContainer);
        layoutSetDateContainer = view.findViewById(R.id.setDateContainer);
        layoutSetTimeContainer = view.findViewById(R.id.setTimeContainer);

        if (this.googleMap == null) {
            mapFrag = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.setLocationFragMap);
            if (mapFrag != null) {
                mapFrag.getMapAsync(this);
            }
        }

        TimePickerDialog.OnTimeSetListener time = (view1, hourOfDay, minute) -> {
            if (view1.isShown()) {
                String myFormat = "HH:mm"; // In which you need put here
                myTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myTime.set(Calendar.MINUTE, minute);
                @SuppressLint ("SimpleDateFormat") SimpleDateFormat dateFormatter
                        = new SimpleDateFormat(myFormat);
                layoutSetTime.setText(dateFormatter.format(myTime.getTime()));
                if ((layoutSetTime.getText() != null)
                        && !(layoutSetTime.getText().toString().equals(""))) {
                    meetingTime = layoutSetTime.getText().toString();
                }
            }
        };

        layoutSetTime.setOnClickListener(v -> {
            layoutSetTime.setClickable(false);
            new TimePickerDialog(getContext(), time, myTime
                    .get(Calendar.HOUR_OF_DAY), myTime.get(Calendar.MINUTE),
                    true).show();
            layoutSetTime.setClickable(true);
        });

        // add a text field listener that validates the inputted text
        layoutSetTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidMeetingTime(meetingDate, s.toString()))) {
                    // if the inputted text is empty
                    if (s.toString().equals("")) {
                        layoutSetTime.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutSetTime.setError("Invalid Time");
                    }
                    layoutSetTimeContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validTime = true;
                    layoutSetTime.setError(null);
                    layoutSetTimeContainer.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });


        confirmBtn.setOnClickListener(v -> {
            // TODO: Need to Notify the user of Accept and Decline all other Requests
            if (validLocation && validDate && validTime
                    && (latitude != invalidCoord) && (longitude != invalidCoord)) {

                if (book.getStatus().equals("REQUESTED")) {
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

                        // notify user
                        sendNotificationRequestAccepted(request, book);
                    }
                } else if (book.getStatus().equals("ACCEPTED")) {
                    if (currentUser != null) {

                        FirebaseFirestore.getInstance()
                                .collection("exchange")
                                .whereEqualTo("owner", currentUser.getEmail())
                                .whereEqualTo("relatedBook", book.getId())
                                .whereEqualTo("ownerBookStatus", "ACCEPTED")
                                .get() // get only 1 book with given isbn
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.e("SIZE",
                                                String.valueOf(task1.getResult().size()));
                                        if (task1.getResult().size() == 0) {
                                            Toast.makeText(getContext(),
                                                    "No Results Found for ISBN: "
                                                            + book.getISBN(), Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {

                                            for (QueryDocumentSnapshot document1
                                                    : task1.getResult()) {

                                                MeetingDetails meetingDetails
                                                        = new MeetingDetails(latitude,
                                                        longitude, address,
                                                        meetingDate, meetingTime);

                                                if (meetingDetails.getAddress() == null) {
                                                    Toast.makeText(getContext(),
                                                            "Invalid Details",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(),
                                                            "Valid Details",
                                                            Toast.LENGTH_SHORT).show();

                                                    Map<String, Object> docData = new HashMap<>();
                                                    docData.put("address",
                                                            meetingDetails.getAddress());
                                                    docData.put("latitude",
                                                            meetingDetails.getLatitude());
                                                    docData.put("longitude",
                                                            meetingDetails.getLongitude());
                                                    docData.put("meetingDate",
                                                            meetingDetails.getMeetingDate());
                                                    docData.put("meetingTime",
                                                            meetingDetails.getMeetingTime());

                                                    FirebaseFirestore.getInstance()
                                                            .collection("exchange")
                                                            .document(document1.getId())
                                                            .update("ownerBookStatus",
                                                                    "BORROWED",
                                                                    "meetingDetails",
                                                                    docData);

                                                    FirebaseIntegrity
                                                            .setBookStatusFirebase(book,
                                                                    "BORROWED");

                                                    Objects.requireNonNull(getActivity())
                                                            .onBackPressed();

                                                }

                                            }
                                        }
                                    }
                                });
                    }

                }
            } else {  // if not all fields are valid
                if (!validLocation) {
                    String locationString = Objects.requireNonNull(layoutSetLocation
                            .getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (locationString.equals("")) {
                        layoutSetLocation.setError("Input Required");
                    } else {
                        layoutSetLocation.setError("Invalid Location");
                    }
                    layoutSetLocationContainer.setErrorEnabled(true);
                    layoutSetLocation.requestFocus();
                } else if (!validDate) {
                    String dateString = Objects.requireNonNull(layoutSetDate
                            .getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (dateString.equals("")) {
                        layoutSetDate.setError("Input Required");
                    } else {
                        layoutSetDate.setError("Invalid Date");
                    }
                    layoutSetDateContainer.setErrorEnabled(true);
                    layoutSetDate.requestFocus();
                } else {
                    String timeString = Objects.requireNonNull(layoutSetTime
                            .getText()).toString();

                    // set an error and focus the app on the erroneous field
                    if (timeString.equals("")) {
                        layoutSetTime.setError("Input Required");
                    } else {
                        layoutSetTime.setError("Invalid Time");
                    }
                    layoutSetTimeContainer.setErrorEnabled(true);
                    layoutSetTime.requestFocus();
                }
            }
        });



        DatePickerDialog.OnDateSetListener date = (view12, year, monthOfYear, dayOfMonth) -> {
            String myFormat = "yyyy/MM/dd";
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            layoutSetDate.setText(sdf.format(myCalendar.getTime()));

            if ((layoutSetDate.getText() != null)
                    && !(layoutSetDate.getText().toString().equals(""))) {
                meetingDate = layoutSetDate.getText().toString();
                validDate = true;
            }
        };

        layoutSetDate.setOnClickListener(v -> {
            layoutSetDate.setClickable(false);
            if (getActivity() != null) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            layoutSetDate.setClickable(true);
        });

        // add a text field listener that validates the inputted text
        layoutSetDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidMeetingDate(s.toString()))) {
                    // if the inputted text is empty
                    if (s.toString().equals("")) {
                        layoutSetDate.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutSetDate.setError("Invalid Date");
                    }
                    layoutSetDateContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validDate = true;
                    layoutSetDate.setError(null);
                    layoutSetDateContainer.setErrorEnabled(false);

                    if ((meetingTime != null)
                            && (Parser.isValidMeetingTime(s.toString(), meetingTime))) {
                        validTime = true;
                        layoutSetTime.setError(null);
                        layoutSetTimeContainer.setErrorEnabled(false);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutSetLocation.setOnClickListener(v -> {
            layoutSetLocation.setClickable(false);
            Intent intent = new Intent(getContext(), LocationActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
            layoutSetLocation.setClickable(true);
        });

        // add a text field listener that validates the inputted text
        layoutSetLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the inputted text is invalid
                if (!(Parser.isValidMeetingAddress(s.toString()))) {
                    // if the inputted text is empty
                    if (s.toString().equals("")) {
                        layoutSetLocation.setError("Input required");
                    } else {  // if the inputted text is otherwise invalid
                        layoutSetLocation.setError("Invalid Location");
                    }
                    layoutSetLocationContainer.setErrorEnabled(true);
                } else {  // if the inputted text is valid
                    validLocation = true;
                    layoutSetLocation.setError(null);
                    layoutSetLocationContainer.setErrorEnabled(false);
                    onMapReady(googleMap);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

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

            LatLng mPinnedMap;
            if ((address != null) && (latitude != invalidCoord) && (longitude != invalidCoord)) {

                Log.e("ADDRESS", address + " " + latitude + " " + longitude);

                if (marker != null) {
                    marker.remove();
                }
                mPinnedMap = new LatLng(latitude, longitude);

                MarkerOptions options = new MarkerOptions()
                        .draggable(true)
                        .title(address)
                        .position(mPinnedMap);

                if (this.googleMap != null) {
                    marker = this.googleMap.addMarker(options);
                }

                marker.setTitle(address);

            } else {

                String antarctica = "Antarctica";
                double antLat = -82.862755;
                double antLng = 135.0;

                Log.e("ADDRESS", "Antarctica" + " " + antLat + " " + antLng);

                if (marker != null) {
                    marker.remove();
                }
                mPinnedMap = new LatLng(antLat, antLng);

                MarkerOptions options = new MarkerOptions()
                        .draggable(true)
                        .title(antarctica)
                        .position(mPinnedMap);

                if (this.googleMap != null) {
                    marker = this.googleMap.addMarker(options);
                }

                marker.setTitle(antarctica);

            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPinnedMap,15));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                latitude = (double) data.getSerializableExtra("Lat");
                longitude = (double) data.getSerializableExtra("Lng");
                address = (String) data.getSerializableExtra("Address");

                layoutSetLocation.setText(address);

                if ((layoutSetLocation.getText() != null)
                        && !(layoutSetLocation.getText().toString().equals(""))) {
                    validLocation = true;
                }

            }
        }
    }




}



