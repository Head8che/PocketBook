package com.example.pocketbook.activity;


import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.pocketbook.fragment.NotificationsFragment;
import com.example.pocketbook.fragment.ProfileExistingFragment;
import com.example.pocketbook.util.ScanHandler;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.fragment.ProfileNewFragment;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.SearchFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.pocketbook.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.pocketbook.util.FirebaseIntegrity.updateToken;

/**
 * Home Page Screen
 */
public class HomeActivity extends AppCompatActivity {
    private User currentUser;
    private BottomNavigationView bottomNav;
    private Bundle extras;
    private int LAUNCH_ADD_BOOK_CODE = 1234;
    private Fragment selectedFragment;
    private String FRAG_TAG;
    private ScanHandler scanHandler;

    public BottomNavigationView getBottomNav() {
        return bottomNav;
    }

    public String getFragTag() {
        return FRAG_TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        scanHandler = new ScanHandler(HomeActivity.this,
                getSupportFragmentManager(), currentUser);
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(NavListener);

        FRAG_TAG = "HOME_FRAGMENT";
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        extras = intent.getExtras();
        if ((extras != null) && (extras.containsKey("NOTI_FRAG"))) {
            if (extras.getBoolean("NOTI_FRAG")) {
                if (!(Objects.equals(Objects
                        .requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                        .getEmail(), currentUser.getEmail())))
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(currentUser.getEmail(),
                                    currentUser.getPassword());
                onNewIntent(intent);
            }
        }
        else {
            selectedFragment = HomeFragment.newInstance(currentUser);

            getSupportFragmentManager().beginTransaction().add(R.id.container,
                    selectedFragment, FRAG_TAG).addToBackStack(FRAG_TAG).commit();
        }
        updateToken(currentUser); //update the token for the user to send and receive notifications

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        selectedFragment = NotificationsFragment.newInstance(currentUser);
        fragmentTransaction.replace(R.id.container, selectedFragment).commit();


    }



    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.e("COUNT", count + "");

        if (count == 0) {
            if (bottomNav.getSelectedItemId() != R.id.bottom_nav_home
                    || (extras.containsKey("NOTI_FRAG") && extras.getBoolean("NOTI_FRAG")))  {
                bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        HomeFragment.newInstance(currentUser)).commit();
                return;
            }
            super.onBackPressed();
        } else if ((count == 1)  // if there's only one frag (the Home Frag)
                && (getSupportFragmentManager().getBackStackEntryCount() == 1)) {
            finish();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }

    }

    /**
     * Bottom navigation bar options
     */
    private BottomNavigationView.OnNavigationItemSelectedListener NavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    String CURRENT_TAG = FRAG_TAG;
                    switch (item.getItemId()){
                        case R.id.bottom_nav_home:
                            selectedFragment = HomeFragment.newInstance(currentUser);
                            FRAG_TAG = "HOME_FRAGMENT";
                            break;
                        case R.id.bottom_nav_search:
                            selectedFragment = SearchFragment.newInstance(currentUser);
                            FRAG_TAG = "SEARCH_FRAGMENT";
                            break;
                        case R.id.bottom_nav_add:
                            Intent intent = new Intent(getBaseContext(), AddBookActivity.class);
                            intent.putExtra("HA_USER", currentUser);
                            startActivityForResult(intent, LAUNCH_ADD_BOOK_CODE);
                            break;
                        case R.id.bottom_nav_scan:
                            scanHandler.showScanningSpinnerDialog();
                            break;
                    }
                    if (item.getItemId() ==  R.id.bottom_nav_profile) {
                        Fragment profileNewFragment = ProfileNewFragment.newInstance(currentUser);
                        ArrayList<String> a = new ArrayList<String>();
                        a.add("owen@gmail.com");
                        Fragment profileExistingFragment = ProfileExistingFragment.newInstance(currentUser);

                        FirebaseFirestore.getInstance()
                                .collection("catalogue")
                                .whereEqualTo("owner",currentUser.getEmail())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            FRAG_TAG = "PROFILE_FRAGMENT";
                                            if (!(CURRENT_TAG.equals(FRAG_TAG))) {
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container,
                                                                profileNewFragment,
                                                                FRAG_TAG)
                                                        .addToBackStack(FRAG_TAG)
                                                        .commit();
                                            }
                                        } else {
                                            FRAG_TAG = "OWNER_FRAGMENT";
                                            if (!(CURRENT_TAG.equals(FRAG_TAG))) {
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container, profileExistingFragment,
                                                                FRAG_TAG)
                                                        .addToBackStack(FRAG_TAG)
                                                        .commit();
                                            }
                                        }
                                    }
                                });
                    }
                    if ((selectedFragment != null)) {

                        if (!(CURRENT_TAG.equals(FRAG_TAG))
                                && (item.getItemId() !=  R.id.bottom_nav_profile)
                        ) {
                            // only change fragment if it is not the current fragment
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    selectedFragment, FRAG_TAG).addToBackStack(FRAG_TAG).commit();
                        }
                        // TODO: Scroll up selected fragment if it is current fragment
                    }
                    return true;
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String scannedIsbn = null;

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if ((result != null) && (result.getContents() != null)){
            scanHandler.dismissAlertDialog();
            scannedIsbn = result.getContents();
            Log.e("SCAN", scannedIsbn);
        }

        if (requestCode == LAUNCH_ADD_BOOK_CODE) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_home);

            if (resultCode == Activity.RESULT_OK) {

                Book book = (Book) data.getSerializableExtra("ABA_BOOK");

                ViewMyBookFragment nextFrag = ViewMyBookFragment.newInstance(currentUser, book);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VMBF_USER", currentUser);
                bundle.putSerializable("VMBF_BOOK", book);
                nextFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(findViewById(R.id.container)
                        .getId(), nextFrag).addToBackStack(null).commit();
            }
        }  else if (scanHandler.getUserSelection() != null) {
            switch (scanHandler.getUserSelection()) {
                case "SEE_DESCRIPTION_CODE":
                    if (scannedIsbn != null) {
                        scanHandler.handleSeeDescription(scannedIsbn);
                    }
                    break;
                case "LEND_BOOK_CODE":
                    if (scannedIsbn != null) {
                        scanHandler.handleLendBook(scannedIsbn);
                    }
                    break;
                case "BORROW_BOOK_CODE":
                    if (scannedIsbn != null) {
                        scanHandler.handleBorrowBook(scannedIsbn);
                    }
                    break;
                case "RETURN_BOOK_CODE":
                    if (scannedIsbn != null) {
                        scanHandler.handleReturnBook(scannedIsbn);
                    }
                    break;
                case "RECEIVE_BOOK_CODE":
                    if (scannedIsbn != null) {
                        scanHandler.handleReceiveBook(scannedIsbn);
                    }
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
