package com.example.pocketbook.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A {@link Fragment} subclass.
 * Use the {@link ViewBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewBookFragment extends androidx.fragment.app.Fragment {

    private Book book;
    private User currentUser;
    private User bookOwner;

    ListenerRegistration listenerRegistration;

    /**
     * create a new instance of the ViewBookFragment
     * @param currentUser: the user currently signed in the app
     * @param bookOwner: the owner of the book being viewed
     * @param book: the book being viewed
     * @return
     */
    public static ViewBookFragment newInstance(User currentUser, User bookOwner, Book book) {
        ViewBookFragment viewBookFragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("BA_CURRENTUSER", currentUser);
        args.putSerializable("BA_BOOK", book);
        args.putSerializable("BA_BOOKOWNER", bookOwner);
        viewBookFragment.setArguments(args);
        return viewBookFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the arguments passed to the fragment as a bundle
        if (getArguments() != null) {
            this.currentUser = (User) getArguments().getSerializable("BA_CURRENTUSER");
            this.book = (Book) getArguments().getSerializable("BA_BOOK");
            this.bookOwner = (User) getArguments().getSerializable("BA_BOOKOWNER");
        }

        listenerRegistration = FirebaseFirestore.getInstance()
                .collection("catalogue")
                .document(book.getId())
                .addSnapshotListener((document, error) -> {
                    if (error != null) {
                        Log.w("VBF_LISTENER", "Listen failed.", error);
                        return;
                    }

                    if ((document != null) && document.exists()) {
                        book = FirebaseIntegrity.getBookFromFirestore(document);

                        getParentFragmentManager()
                                .beginTransaction()
                                .detach(ViewBookFragment.this)
                                .attach(ViewBookFragment.this)
                                .commitAllowingStateLoss();
                    } else {
                        if ( getActivity() == null) {
                            getParentFragmentManager().beginTransaction()
                                    .detach(ViewBookFragment.this).commitAllowingStateLoss();
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }


                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // clear the container before starting the fragment
        if (container != null) {
            container.removeAllViews();
        }

        // inflate fragment in container
        View view = inflater.inflate(R.layout.fragment_view_book, container, true);

        // get the view fields by ids
        TextView bookTitleField = view.findViewById(R.id.viewBookTitle);
        TextView bookAuthorField = view.findViewById(R.id.viewBookAuthor);
        TextView isbnField = view.findViewById(R.id.viewBookISBN);
        TextView conditionField = view.findViewById(R.id.viewBookCondition);
        TextView commentField = view.findViewById(R.id.viewBookComment);
        Button requestButton = view.findViewById(R.id.viewBookRequestBtn);
        CircleImageView userProfilePicture = view.findViewById(R.id.viewBookUserProfile);
        TextView usernameField = view.findViewById(R.id.viewBookUsernameTextView);
        ImageView bookCoverImageView = view.findViewById(R.id.bookCover);
        ImageView bookStatusImage = view.findViewById(R.id.viewBookBookStatusImageView);

        ImageView backButton = view.findViewById(R.id.viewBookFragBackBtn);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // set the views' values to the values of the book being viewed
        bookTitleField.setText(book.getTitle());
        bookAuthorField.setText(book.getAuthor());
        isbnField.setText(getResources().getString(R.string.isbn_text, book.getISBN()));
        //display a comment if the owner optionally added one to the book
        if ((book.getComment() != null) && !(book.getComment().equals(""))) {
            commentField.setText(getResources().getString(R.string.comment_text,
                    book.getComment()));
        } else {
            commentField.setVisibility(View.GONE);
        }
        // display the book's condition
        if ((book.getCondition() != null) && !(book.getCondition().equals(""))) {
            conditionField.setText(getResources().getString(R.string.condition_text,
                    book.getCondition()));
        } else {
            conditionField.setVisibility(View.GONE);
        }
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(FirebaseIntegrity.getBookCover(book))
                .into(bookCoverImageView);

        // get the profile picture of the book's owner and set it in its field
        GlideApp.with(Objects.requireNonNull(getContext()))
                .load(FirebaseIntegrity.getUserProfilePicture(bookOwner))
                .circleCrop()
                .into(userProfilePicture);

        usernameField.setText(bookOwner.getUsername());

        Log.e("VB", "status:" + book.getStatus()
                + " " + "requesters:" + book.getRequesters());
        
        switch(book.getStatus().toUpperCase()) {

            // if the book is borrowed or accepted by another user, it cannot be requested
            case "BORROWED":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                        R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.colorBorrowed),android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "ACCEPTED":
                requestButton.setClickable(false);
                requestButton.setText("Not Available");
                requestButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                        R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_accepted);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.colorAccepted),android.graphics.PorterDuff.Mode.SRC_IN);
                break;


            case "REQUESTED":
                // if the book has any requesters
                if (book.getRequesters().size() > 0) {
                    bookStatusImage.setImageResource(R.drawable.ic_requested);
                    bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                            R.color.colorRequested), android.graphics.PorterDuff.Mode.SRC_IN);

                    // if the book has not been requested by this user before
                    if (book.getRequesters().contains(currentUser.getEmail())) {
                        requestButton.setText("Already Requested!");
                        requestButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                                R.color.notAvailable));
                    }
                }
                break;

            default:  // default case is that the book is available
                bookStatusImage.setImageResource(R.drawable.ic_available);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.colorAvailable), android.graphics.PorterDuff.Mode.SRC_IN);
        }



        String bookStatus = book.getStatus().toUpperCase();
        boolean currentUserRequested = book.getRequesters().contains(currentUser.getEmail());

        // if the book is available for requesting (i.e. the user hasn't requested it and it's
        // not accepted or borrowed), the user can tap the request button to request the book
        if ((bookStatus.equals("AVAILABLE"))
                || (bookStatus.equals("REQUESTED") && !currentUserRequested)) {
            requestButton.setOnClickListener(view1 -> {

                Request request = new Request(currentUser.getEmail(),
                        bookOwner.getEmail(), book, null);

                // NOTE: the local addRequest is purely for testing; FirebaseIntegrity will
                //  overwrite all locally set data with the appropriate Firebase data

//                    book.addRequest(request);

                // add the book request to Firebase
                FirebaseIntegrity.addBookRequest(request);

                // display a message confirming that the book has been requested
                new AlertDialog.Builder(getContext())
                        .setTitle("Request sent!")
                        .setMessage("You have requested "+book.getTitle()+"!" )
                        .show();

                // go back to the previous fragment
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }



}
