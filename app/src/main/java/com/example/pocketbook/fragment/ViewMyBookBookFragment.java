package com.example.pocketbook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditBookActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Exchange;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;


public class ViewMyBookBookFragment extends Fragment {

    private Book book;

    ListenerRegistration listenerRegistration;

    public ViewMyBookBookFragment() {
        // Required empty public constructor
    }


    public static ViewMyBookBookFragment newInstance(Book book) {
        ViewMyBookBookFragment fragment = new ViewMyBookBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("VMBPA_BOOK", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable("VMBPA_BOOK");
        }

        if ((book == null) || (book.getId() == null)) {
            return;
        }

        listenerRegistration = FirebaseFirestore.getInstance().collection("catalogue")
                .document(book.getId()).addSnapshotListener((snapshot, e) -> {
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
                        if ( getActivity() == null) {
                            getParentFragmentManager().beginTransaction()
                                    .detach(ViewMyBookBookFragment.this).commitAllowingStateLoss();
                        } else {
                            getActivity().getFragmentManager().popBackStack();
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
        StorageReference bookCover = FirebaseIntegrity.getBookCover(book);
        String bookStatus = book.getStatus();
        String bookCondition = book.getCondition();
        String bookComment = book.getComment();

        TextView layoutBookTitle = (TextView) rootView.findViewById(R.id.viewMyBookBookTitleTextView);
        TextView bookLocationField = (TextView) rootView.findViewById(R.id.viewMyBookViewPickupLocation);
        TextView layoutBookAuthor = (TextView) rootView.findViewById(R.id.viewMyBookAuthorTextView);
        TextView layoutBookISBN = (TextView) rootView.findViewById(R.id.viewMyBookISBNTextView);
        ImageView layoutBookCover = (ImageView) rootView.findViewById(R.id.viewMyBookBookCoverImageView);
        ImageView layoutBookStatus = (ImageView) rootView.findViewById(R.id.viewBookBookStatusImageView);
        TextView layoutBookCondition = (TextView) rootView.findViewById(R.id.viewMyBookConditionTextView);
        TextView layoutBookComment = (TextView) rootView.findViewById(R.id.viewMyBookCommentTextView);

        if ((book.getStatus().equals("ACCEPTED") || book.getStatus().equals("BORROWED"))) {

            if (book.getStatus().equals("BORROWED")) {
                bookLocationField.setText(R.string.viewReturnLocation);
            }

            bookLocationField.setVisibility(View.VISIBLE);
            bookLocationField.setOnClickListener(v -> {
                if (getActivity() != null) {
                    bookLocationField.setClickable(false);

                    FirebaseFirestore.getInstance()
                            .collection("catalogue")
                            .document(book.getId())
                            .collection("requests")
                            .get()
                            .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                                if (task.isSuccessful()) {
                                    // get 1st request; there should only be one request
                                    String requester = task.getResult().getDocuments().get(0).getId();

                                    FirebaseFirestore.getInstance()
                                            .collection("exchange")
                                            .whereEqualTo("relatedBook", book.getId())
                                            .whereEqualTo("owner", book.getOwner())
                                            .whereEqualTo("borrower", requester)
                                            .get().addOnCompleteListener(task1 -> {
                                                if (!(task1.isSuccessful())) {
                                                    Log.e("VIEW_BOOK_EXCHANGE",
                                                            "Error getting exchange document!");
                                                } else {
                                                    List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                                                    DocumentSnapshot document = documents.get(0);

                                                    Exchange exchange = FirebaseIntegrity
                                                            .getExchangeFromFirestore(document);

                                                    if (exchange != null) {
                                                        ViewLocationFragment nextFrag
                                                                = ViewLocationFragment.newInstance(exchange);

                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("VBF_EXCHANGE", exchange);
                                                        nextFrag.setArguments(bundle);

                                                        getActivity().getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .replace(getActivity()
                                                                        .findViewById(R.id.container)
                                                                        .getId(), nextFrag)
                                                                .addToBackStack(null).commit();
                                                    }

                                                }
                                                bookLocationField.setClickable(true);
                                    });
                                }
                            });
                }
            });
        } else {
            bookLocationField.setVisibility(View.GONE);
        }

        layoutBookTitle.setText(bookTitle);
        layoutBookAuthor.setText(bookAuthor);
        layoutBookISBN.setText(getResources().getString(R.string.isbn_text, bookISBN));

        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(bookCover)
                .into(layoutBookCover);

        switch(bookStatus.toUpperCase()) {
            case "REQUESTED":
                layoutBookStatus.setImageResource(R.drawable.ic_requested);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRequested),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "ACCEPTED":
                layoutBookStatus.setImageResource(R.drawable.ic_accepted);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case "BORROWED":
                layoutBookStatus.setImageResource(R.drawable.ic_borrowed);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                layoutBookStatus.setImageResource(R.drawable.ic_available);
                layoutBookStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if ((bookCondition != null) && (!bookCondition.equals(""))) {
            layoutBookCondition.setText(getResources().getString(R.string.condition_text,
                    bookCondition));
        } else {
            layoutBookCondition.setVisibility(View.GONE);
        }

        if ((bookComment != null) && (!bookComment.equals(""))) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }
}