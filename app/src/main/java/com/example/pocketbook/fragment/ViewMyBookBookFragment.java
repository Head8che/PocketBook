package com.example.pocketbook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.ViewMyBookActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMyBookBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMyBookBookFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  String user_email;
    private  String bookID;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewMyBookBookFragment() {
        // Required empty public constructor
    }

    public ViewMyBookBookFragment(String user_email, String bookID) {
        this.user_email = user_email;
        this.bookID = bookID;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewMyBookBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewMyBookBookFragment newInstance(String param1, String param2) {
        ViewMyBookBookFragment fragment = new ViewMyBookBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_my_book_book, container, false);

        StorageReference defaultBookCover = FirebaseStorage.getInstance().getReference().child("default_images").child("no_book_cover_light.png");

        ImageView layoutBookCover = (ImageView) rootView.findViewById(R.id.viewMyBookBookCoverImageView);
        ImageView layoutBookStatus = (ImageView) rootView.findViewById(R.id.viewMyBookStatusImageView);
        TextView layoutBookTitle = (TextView) rootView.findViewById(R.id.viewMyBookBookTitleTextView);
        TextView layoutBookAuthor = (TextView) rootView.findViewById(R.id.viewMyBookAuthorTextView);
        TextView layoutBookISBN = (TextView) rootView.findViewById(R.id.viewMyBookISBNTextView);
        TextView layoutBookCondition = (TextView) rootView.findViewById(R.id.viewMyBookConditionTextView);
        TextView layoutBookComment = (TextView) rootView.findViewById(R.id.viewMyBookCommentTextView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookRef = db.collection("books").document(bookID);

        bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        String docTitle = document.getString("title");
                        String docAuthor = document.getString("author");
                        String docISBN = document.getString("isbn");
                        String docCondition = document.getString("condition");
                        String docComment = document.getString("comment");
                        String docPhoto = document.getString("photo");
                        StorageReference docBookCover;

                        // still need to handle this with the changing icons
                        String docStatus = document.getString("status");

                        if (docPhoto == null || docPhoto.trim().equals("")) {
                            docBookCover = FirebaseStorage.getInstance().getReference().child("default_images").child("no_book_cover_light.png");
                        } else {
                            docBookCover = FirebaseStorage.getInstance().getReference().child("book_covers").child(docPhoto);
                        }

                        GlideApp.with(Objects.requireNonNull(getContext()))
                            .load(docBookCover)
                            .into(layoutBookCover);

                        assert docISBN != null;

                        layoutBookTitle.setText(docTitle);
                        layoutBookAuthor.setText(docAuthor);
                        layoutBookISBN.setText(getResources().getString(R.string.isbn_text, docISBN.trim()));

                        if (docCondition == null || docCondition.trim().equals("")) {
                            layoutBookCondition.setVisibility(View.GONE);
                        } else {
                            layoutBookCondition.setText(getResources().getString(R.string.condition_text,
                                    docCondition.trim()));
                        }

                        if (docComment == null || docComment.trim().equals("")) {
                            layoutBookComment.setVisibility(View.GONE);
                        } else {
                            layoutBookComment.setText(getResources().getString(R.string.comment_text,
                                    docComment.trim()));
                        }
                    }
                }
            }
        });

        return rootView;
    }
}