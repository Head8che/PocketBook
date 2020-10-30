package com.example.pocketbook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.adapter.BookAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {
    private static final int numColumns = 2;
    private static final int LIMIT = 20;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private BookList ownedBooks = new BookList();
    private RecyclerView mBooksRecycler;
    private BookAdapter mAdapter;
    private TextView profileName, userName;
    private TextView editProfile;
    private static final String USERS = "users";
    private User currentUser;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    public ProfileFragment(){
        // Empty Constructor
    }

    public ProfileFragment(User currentUser){
        this.currentUser = currentUser;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
          container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mBooksRecycler = v.findViewById(R.id.recycler_books);
        mBooksRecycler.setLayoutManager(new GridLayoutManager(v.getContext(), numColumns));
        mAdapter = new BookAdapter(ownedBooks, getActivity());
        mBooksRecycler.setAdapter(mAdapter);

        String first_Name = currentUser.getFirstName();
        String last_Name = currentUser.getLastName();
        String user_Name = currentUser.getUsername();
        // TODO: obtain user_photo from firebase
        String user_Pic = currentUser.getPhoto();

        TextView ProfileName = (TextView) v.findViewById(R.id.profileName);
        TextView UserName = (TextView) v.findViewById(R.id.user_name);
        ProfileName.setText(first_Name + ' ' + last_Name);
        UserName.setText(user_Name);

        editProfile = v.findViewById(R.id.edit_profile_button);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        ownedBooks.addBook(book);
                    }
                    mAdapter.notifyDataSetChanged();
//                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);


                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            GridLayoutManager gridLayoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                            assert gridLayoutManager != null;
                            int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = gridLayoutManager.getChildCount();
                            int totalItemCount = gridLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;

                                if ((task.getResult().size() - 1) < (totalItemCount - 1)) {

                                    Query nextQuery = mFirestore.collection("books").startAfter(lastVisible).limit(LIMIT);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Book book = d.toObject(Book.class);
                                                    ownedBooks.addBook(book);
                                                }
                                                mAdapter.notifyDataSetChanged();
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                                if (t.getResult().size() < LIMIT) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    };
                    mBooksRecycler.addOnScrollListener(onScrollListener);
                }
            }
        });


        //
//        private FirebaseAuth mAuth;
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://am-d5edb.appspot.com").child("users").child(mAuth.getUid()+".jpg");
//
//        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Log.e("Tuts+", "uri: " + uri.toString());
//                DownloadLink = uri.toString();
//                CircleImageView iv = (CircleImageView) view.findViewById(R.id.profilePictureEditFragment);
//                Picasso.with(getContext()).load(uri.toString()).placeholder(R.drawable.ic_launcher3slanted).error(R.drawable.ic_launcher3slanted).into(iv);
//                //Handle whatever you're going to do with the URL here
//            }
//        });



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment nextFrag = new EditProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,nextFrag).commit();
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Query to retrieve all books
        mQuery = mFirestore.collection("books").whereEqualTo("owner",currentUser.getEmail()).limit(LIMIT);

    }



}