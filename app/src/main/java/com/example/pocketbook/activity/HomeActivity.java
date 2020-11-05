package com.example.pocketbook.activity;


import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.AddFragment;
import com.example.pocketbook.fragment.OwnerFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.fragment.ScanFragment;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.SearchFragment;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.pocketbook.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final int LIMIT = 20;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;
    private Button buttonUploadImg;
    private String name;
    private ImageView image;
    private ArrayList<String> pathArray;
    private int array_position;
    private FirebaseUser currentUser;
    String email;
    private User user;
    public int check = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("CURRENT_USER");
//        Toast.makeText(this,user.getFirstName(),Toast.LENGTH_SHORT).show();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(NavListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                HomeFragment.newInstance(user, new BookList())).commit();
    }

    @Override //temporary until we find a way to make the back button work properly
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                HomeFragment.newInstance(user, new BookList())).commit();
    }
  
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener NavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.bottom_nav_home:
                            selectedFragment = HomeFragment.newInstance(user, new BookList());
                            break;
                        case R.id.bottom_nav_search:
                            selectedFragment = SearchFragment.newInstance(user, new BookList());
                            break;
                        case R.id.bottom_nav_add:
                            selectedFragment = AddFragment.newInstance(user, new BookList());
                            break;
                        case R.id.bottom_nav_scan:
                            selectedFragment = new ScanFragment();
                            break;
                        case R.id.bottom_nav_profile:
                            mFirestore = FirebaseFirestore.getInstance();
                            mFirestore.collection("catalogue")
                                    .whereEqualTo("owner",user.getEmail())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.exists()) {
                                                        check = 1;
                                                    }
                                                }
                                            }
                                            Log.d("CHECKOne", String.valueOf(check));
                                        }
                                    });
//                            Log.d("Final_Check", String.valueOf(check));
//                            if (String.valueOf(check) == String.valueOf(0)){
//                                selectedFragment = ProfileFragment.newInstance(user);
//                            }
//                            else {
//                                selectedFragment = OwnerFragment.newInstance(user);
//                            }
                            selectedFragment = ProfileFragment.newInstance(user);
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();
                    return true;
                }
            };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}


