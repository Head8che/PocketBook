package com.example.pocketbook.activity;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import com.example.pocketbook.fragment.NotificationsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ScanFragment;
import com.example.pocketbook.fragment.SearchFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.pocketbook.model.User;
import static com.example.pocketbook.util.FirebaseIntegrity.getUserFromFirestore;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import static com.example.pocketbook.util.FirebaseIntegrity.updateToken;

/**
 * Home Page Screen
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    private FirebaseFirestore mFirestore;
    private User currentUser;
    private BottomNavigationView bottomNav;
    private Bundle extras;

    private int LAUNCH_ADD_BOOK_CODE = 1234;
    private int SEE_DESCRIPTION_CODE = 1111;
    private int LEND_BOOK_CODE = 2222;
    private int BORROW_BOOK_CODE = 3333;
    private int RETURN_BOOK_CODE = 4444;
    private int RECEIVE_BOOK_CODE = 5555;

    Fragment selectedFragment;
    String FRAG_TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(NavListener);

        FRAG_TAG = "HOME_FRAGMENT";
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        extras = intent.getExtras();
        if (extras.containsKey("NOTI_FRAG") ) {
            if (extras.getBoolean("NOTI_FRAG")) {
                if (!(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(currentUser.getEmail())))
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPassword());
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

        if (count == 0) {
            if (bottomNav.getSelectedItemId() != R.id.bottom_nav_home
                    || (extras.containsKey("NOTI_FRAG") && extras.getBoolean("NOTI_FRAG")))  {
                bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        HomeFragment.newInstance(currentUser)).commit();
                return;
            }
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }

    }

    /**
     * Greeting message displayed on the screen open successful logging in.
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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
                            selectedFragment = ScanFragment.newInstance(currentUser);
                            ScanFragment nextFrag = ScanFragment.newInstance(currentUser);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("SCAN_USER", currentUser);
                            nextFrag.setArguments(bundle);
                            FRAG_TAG = "SCAN_FRAGMENT";
                            break;
                        // showSpinnerDialog when layoutBookCondition is clicked
                        //ISSUE need to manually set camera permissions. Android manifest permission not working
                        //ISSUE how to test using actual isbn?
                        //TODO get information from the isbn
//                            Intent scanIntent = new Intent(getBaseContext(), ScanActivity.class);
//                            scanIntent.putExtra("SA_USER", currentUser);
//                            startActivityForResult(scanIntent, SEE_DESCRIPTION_CODE);

//                            startActivity(new Intent(HomeActivity.this, ScanActivity.class));

                    }
                    if (item.getItemId() ==  R.id.bottom_nav_profile) {
                        Fragment profileFragment = ProfileFragment.newInstance(currentUser);
                        Fragment ownerFragment = OwnerFragment.newInstance(currentUser);

                        mFirestore = FirebaseFirestore.getInstance();
                        mFirestore.collection("catalogue")
                                .whereEqualTo("owner",currentUser.getEmail())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().isEmpty()) {
                                                FRAG_TAG = "PROFILE_FRAGMENT";
                                                if (!(CURRENT_TAG.equals(FRAG_TAG))) {
                                                    getSupportFragmentManager().beginTransaction()
                                                            .replace(R.id.container,
                                                                    profileFragment,
                                                                    FRAG_TAG)
                                                            .addToBackStack(FRAG_TAG)
                                                            .commit();
                                                }
                                            } else {
                                                FRAG_TAG = "OWNER_FRAGMENT";
                                                if (!(CURRENT_TAG.equals(FRAG_TAG))) {
                                                    getSupportFragmentManager().beginTransaction()
                                                            .replace(R.id.container, ownerFragment,
                                                                    FRAG_TAG)
                                                            .addToBackStack(FRAG_TAG)
                                                            .commit();
                                                }
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
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
