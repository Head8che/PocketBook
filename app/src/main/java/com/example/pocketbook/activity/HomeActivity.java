package com.example.pocketbook.activity;


import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.fragment.ScanFragment;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.SearchFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.pocketbook.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Home Page Screen
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    private FirebaseFirestore mFirestore;
    private User currentUser;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(NavListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                HomeFragment.newInstance(currentUser)).commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (bottomNav.getSelectedItemId() != R.id.bottom_nav_home) {
                bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        HomeFragment.newInstance(currentUser)).commit();
                return;
            }
            super.onBackPressed();
        } else {
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
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.bottom_nav_home:
                            selectedFragment = HomeFragment.newInstance(currentUser);
                            break;
                        case R.id.bottom_nav_search:
                            selectedFragment = SearchFragment.newInstance(currentUser);
                            break;
                        case R.id.bottom_nav_add:
                            Intent intent = new Intent(getBaseContext(), AddBookActivity.class);
                            intent.putExtra("HA_USER", currentUser);
                            startActivityForResult(intent, 1);
                            break;
                        case R.id.bottom_nav_scan:
                            selectedFragment = new ScanFragment();
                            break;

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
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container, profileFragment).commit();
                                            } else {
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container, ownerFragment).commit();
                                            }
                                        }
                                    }
                                });
                    }
                    if ((selectedFragment != null) && (item.getItemId() !=  R.id.bottom_nav_profile)) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
                    }
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_home);

            if (resultCode == Activity.RESULT_OK) {

                Book book = (Book) data.getSerializableExtra("ABA_BOOK");

                ViewMyBookFragment nextFrag = ViewMyBookFragment.newInstance(currentUser, book);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VMBF_USER", currentUser);
                bundle.putSerializable("VMBF_BOOK", book);
                nextFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
