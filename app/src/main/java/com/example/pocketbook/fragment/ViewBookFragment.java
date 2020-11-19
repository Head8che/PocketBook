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
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.notifications.APIService;
import com.example.pocketbook.notifications.Client;
import com.example.pocketbook.notifications.Data;
import com.example.pocketbook.notifications.MyResponse;
import com.example.pocketbook.notifications.Sender;
import com.example.pocketbook.notifications.Token;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.pocketbook.util.FirebaseIntegrity.pushNewNotificationToFirebase;


/**
 * Allows users to view a book that they do not own.
 * A {@link Fragment} subclass.
 * Use the {@link ViewBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewBookFragment extends androidx.fragment.app.Fragment {

    private Book book;
    private User currentUser;
    private User bookOwner;

    ListenerRegistration listenerRegistration;

    APIService apiService;

    /**
     * create a new instance of the ViewBookFragment
     *
     * @param currentUser: the user currently signed in the app
     * @param bookOwner:   the owner of the book being viewed
     * @param book:        the book being viewed
     * @return a new instance of the ViewBookFragment
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
                        if (getActivity() == null) {
                            getParentFragmentManager().beginTransaction()
                                    .detach(ViewBookFragment.this).commitAllowingStateLoss();
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }


                });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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

        String bookStatus = book.getStatus().toUpperCase();

        // get the view fields by ids
        TextView bookTitleField = view.findViewById(R.id.viewBookTitle);
        TextView bookLocationField = view.findViewById(R.id.viewBookViewPickupLocation);
        TextView bookAuthorField = view.findViewById(R.id.viewBookAuthor);
        TextView isbnField = view.findViewById(R.id.viewBookISBN);
        TextView conditionField = view.findViewById(R.id.viewBookCondition);
        TextView commentField = view.findViewById(R.id.viewBookComment);
        Button requestButton = view.findViewById(R.id.viewBookRequestBtn);
        CircleImageView userProfilePicture = view.findViewById(R.id.viewBookUserProfile);
        TextView usernameField = view.findViewById(R.id.viewBookUsernameTextView);
        ImageView bookCoverImageView = view.findViewById(R.id.bookCover);
        ImageView bookStatusImage = view.findViewById(R.id.viewBookBookStatusImageView);

        if (!(bookStatus.equals("ACCEPTED"))) {
            bookLocationField.setVisibility(View.GONE);
        }

        ImageView backButton = view.findViewById(R.id.viewBookFragBackBtn);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // TODO: clicking on View Pickup Location should show pickup location page

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

        View.OnClickListener profileClickListener = view1 -> {
            FirebaseFirestore.getInstance().collection("users")
                    .document(book.getOwner())
                    .get().addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                if ((document != null) && (document.exists())) {
                    User bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
                    ViewProfileFragment nextFrag = ViewProfileFragment.newInstance(currentUser,
                            bookOwner);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VPF_CURRENT_USER", currentUser);
                    bundle.putSerializable("VPF_PROFILE_USER", bookOwner);
                    nextFrag.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(getActivity().findViewById(R.id.container).getId(), nextFrag)
                            .addToBackStack(null).commit();
                }
            });
        };

        userProfilePicture.setOnClickListener(profileClickListener);
        usernameField.setOnClickListener(profileClickListener);

        usernameField.setText(bookOwner.getUsername());

        switch (bookStatus) {

            // if the book is borrowed or accepted by another user, it cannot be requested
            case "BORROWED":
                requestButton.setClickable(false);
                requestButton.setText(R.string.notAvailable);
                requestButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                        R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_borrowed);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.colorBorrowed), android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "ACCEPTED":
                requestButton.setClickable(false);
                requestButton.setText(R.string.notAvailable);
                requestButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                        R.color.notAvailable));
                bookStatusImage.setImageResource(R.drawable.ic_accepted);
                bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.colorAccepted), android.graphics.PorterDuff.Mode.SRC_IN);
                break;


            case "REQUESTED":
                // if the book has any requesters
                if (book.getRequesters().size() > 0) {
                    bookStatusImage.setImageResource(R.drawable.ic_requested);
                    bookStatusImage.setColorFilter(ContextCompat.getColor(getContext(),
                            R.color.colorRequested), android.graphics.PorterDuff.Mode.SRC_IN);

                    // if the book has not been requested by this user before
                    if (book.getRequesters().contains(currentUser.getEmail())) {
                        requestButton.setText(R.string.alreadyRequested);
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

        boolean currentUserRequested = book.getRequesters().contains(currentUser.getEmail());

        // if the book is available for requesting (i.e. the user hasn't requested it and it's
        // not accepted or borrowed), the user can tap the request button to request the book
        if ((bookStatus.equals("AVAILABLE"))
                || (bookStatus.equals("REQUESTED") && !currentUserRequested)) {
            requestButton.setOnClickListener(view1 -> {

                Request request = new Request(currentUser.getEmail(),
                        bookOwner.getEmail(), book, null);

                // add the book request to Firebase
                FirebaseIntegrity.addBookRequest(request);

                // display a message confirming that the book has been requested
                new AlertDialog.Builder(getContext())
                        .setTitle("Request sent!")
                        .setMessage("You have requested " + book.getTitle() + "!")
                        .show();


                FirebaseFirestore.getInstance().collection("users").document(bookOwner.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    String userToken = task.getResult().get("token").toString();
                                    String msg = String.format("%s has requested %s", currentUser.getUsername(), book.getTitle());
                                    Data data = new Data(msg, "New Request");
                                    Notification notification = new Notification(msg, currentUser.getEmail(), bookOwner.getEmail(), book.getId(), false, "BOOK_REQUESTED");
                                    pushNewNotificationToFirebase(notification);
                                    sendNotification(userToken,data);
                                }
                            }
                        });

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


    private void sendNotification(String token,Data data) {

        Sender sender = new Sender(data, token);
        apiService.sendNotification(sender).enqueue((new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        }));
    }
}
