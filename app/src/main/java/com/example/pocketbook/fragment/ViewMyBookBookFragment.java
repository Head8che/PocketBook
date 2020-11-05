package com.example.pocketbook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Objects;


public class ViewMyBookBookFragment extends Fragment {

    private Book book;
    private BookList catalogue;

    public ViewMyBookBookFragment() {
        // Required empty public constructor
    }


    public static ViewMyBookBookFragment newInstance(Book book, BookList catalogue) {
        ViewMyBookBookFragment fragment = new ViewMyBookBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("VMBPA_BOOK", book);
        args.putSerializable("VMBPA_CATALOGUE", catalogue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("VMBPA_BOOK");
            this.catalogue = (BookList) getArguments().getSerializable("VMBPA_CATALOGUE");
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("catalogue")
                .document(book.getId());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("VMBBF_LISTENER", "Listen failed.", e);
                    return;
                }

                if ((snapshot != null) && snapshot.exists()) {
                    book = FirebaseIntegrity.getBookFromFirestore(snapshot);

                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(ViewMyBookBookFragment.this)
                            .attach(ViewMyBookBookFragment.this)
                            .commitAllowingStateLoss();
                } else {
                    Objects.requireNonNull(getActivity()).getFragmentManager().popBackStack();
                }


            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_my_book_book, container, false);

        String bookTitle = book.getTitle();
        String bookAuthor = book.getAuthor();
        String bookISBN = book.getISBN();
        StorageReference bookCover = book.getBookCover();
        String bookStatus = book.getStatus();
        String bookCondition = book.getCondition();
        String bookComment = book.getComment();

        TextView layoutBookTitle = (TextView) rootView.findViewById(R.id.viewMyBookBookTitleTextView);
        TextView layoutBookAuthor = (TextView) rootView.findViewById(R.id.viewMyBookAuthorTextView);
        TextView layoutBookISBN = (TextView) rootView.findViewById(R.id.viewMyBookISBNTextView);
        ImageView layoutBookCover = (ImageView) rootView.findViewById(R.id.viewMyBookBookCoverImageView);
        ImageView layoutBookStatus = (ImageView) rootView.findViewById(R.id.viewBookBookStatusImageView);
        TextView layoutBookCondition = (TextView) rootView.findViewById(R.id.viewMyBookConditionTextView);
        TextView layoutBookComment = (TextView) rootView.findViewById(R.id.viewMyBookCommentTextView);

        layoutBookTitle.setText(bookTitle);
        layoutBookAuthor.setText(bookAuthor);
        layoutBookISBN.setText(getResources().getString(R.string.isbn_text, bookISBN));

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookCover)
                .into(layoutBookCover);

        switch(bookStatus) {
            case "accepted":
                layoutBookStatus.setImageResource(R.drawable.ic_accepted);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "borrowed":
                layoutBookStatus.setImageResource(R.drawable.ic_borrowed);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                layoutBookStatus.setImageResource(R.drawable.ic_available);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (bookCondition != null) {
            layoutBookCondition.setText(getResources().getString(R.string.condition_text,
                    bookCondition));
        } else {
            layoutBookCondition.setVisibility(View.GONE);
        }

        if (bookComment != null) {
            layoutBookComment.setText(getResources().getString(R.string.comment_text,
                    bookComment));
        } else {
            layoutBookComment.setVisibility(View.GONE);
        }

        Button editButton = (Button) rootView.findViewById(R.id.viewMyBookEditBtn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditBookActivity.class);
                intent.putExtra("VMBBF_BOOK", book);
                startActivity(intent);
            }
        });

        return rootView;
    }
}