package com.example.pocketbook.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.SetLocationFragment;

import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;
import com.example.pocketbook.fragment.ViewProfileFragment;

import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Notification;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.notifications.APIService;
import com.example.pocketbook.notifications.Client;
import com.example.pocketbook.notifications.Data;
import com.example.pocketbook.notifications.Response;
import com.example.pocketbook.notifications.Sender;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.example.pocketbook.notifications.NotificationHandler.sendNotificationRequestDeclined;
import static com.example.pocketbook.util.FirebaseIntegrity.pushNewNotificationToFirebase;

public class RequestAdapter extends FirestoreRecyclerAdapter<Request,
        RequestAdapter.RequestHolder> {
    private Book mBook;
    private User mRequester;
    private User mRequestee;
    private User currentUser;
    private FragmentActivity activity;
    APIService apiService;

    public RequestAdapter(@NonNull FirestoreRecyclerOptions<Request> options,
                          Book mBook, FragmentActivity activity) {
        super(options);
        this.mBook = mBook;
        this.mBook = mBook;
        this.activity = activity;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    static class RequestHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView date;
        private CircleImageView userProfile;
        private Button accept;
        private Button decline;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            //get the views' ids
            username = itemView.findViewById(R.id.itemNotiUsernameTextView);
            date = itemView.findViewById(R.id.itemRequestDateTextView);
            userProfile = itemView.findViewById(R.id.itemRequestProfileImageView);
            accept = itemView.findViewById(R.id.itemRequestAcceptButton);
            decline = itemView.findViewById(R.id.itemRequestDeclineButton);

        }
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RequestHolder(inflater.inflate(R.layout.item_request,
                parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder requestHolder,
                                    int position, @NonNull Request request) {

        //get the requester's info from Firestore to display it to the owner
        String requesterEmail = request.getRequester();

        // TODO: if request is accepted:
        //  - change tab title from REQUESTS to ACCEPTED
        //  - hide decline button
        //  - set test to You Accepted Username's Request
        //  - feat: add Cancel Accept feature to requests & cancel request to ViewBookFrag

        FirebaseFirestore.getInstance().collection("users").document(requesterEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            mRequester = FirebaseIntegrity.getUserFromFirestore(document);
                            if (mRequester != null) {
                                requestHolder.username.setText(mRequester.getUsername());
                                GlideApp.with(Objects.requireNonNull(
                                        requestHolder.itemView.getContext()))
                                        .load(FirebaseIntegrity.getUserProfilePicture(mRequester))
                                        .into(requestHolder.userProfile);
                            }
                        }
                    }
                });
        requestHolder.date.setText(request.getRequestDate());

        // if the user already accepted a request, they can't accept or decline that request
        if (mBook.getStatus().equals("BORROWED")) {
            requestHolder.accept.setVisibility(View.GONE);
            requestHolder.accept.setClickable(false);
            requestHolder.decline.setVisibility(View.GONE);
            requestHolder.decline.setClickable(false);
        }

        // if the user already accepted a request, they can't accept or decline that request
        if (mBook.getStatus().equals("ACCEPTED")){
            requestHolder.accept.setVisibility(View.VISIBLE);
            requestHolder.accept.setText(R.string.cancelAccept);
            requestHolder.decline.setVisibility(View.GONE);
            requestHolder.decline.setClickable(false);

            requestHolder.accept.setOnClickListener(view -> {
                requestHolder.accept.setClickable(false);

                FirebaseFirestore.getInstance()
                        .collection("exchange")
                        .whereEqualTo("relatedBook", mBook.getId())
                        .whereEqualTo("owner", mBook.getOwner())
                        .whereEqualTo("borrower", mRequester.getEmail())
                        .get().addOnCompleteListener(task1 -> {
                    if (!(task1.isSuccessful())) {
                        Log.e("VIEW_BOOK_EXCHANGE",
                                "Error getting exchange document!");
                    } else {
                        List<DocumentSnapshot> documents = task1.getResult().getDocuments();

                        String docID = documents.get(0).getId();

                        FirebaseFirestore.getInstance()
                                .collection("exchange")
                                .document(docID)
                                .delete();

                        FirebaseIntegrity.deleteBookRequest(mBook.getId(),
                                mRequester.getEmail());

                        requestHolder.accept.setClickable(true);
                    }
                });
            });
        }

        // when the user taps on the accept button for a request,
        // the request is accepted and they can't decline that request
        if (mBook.getStatus().equals("REQUESTED")) {
            requestHolder.accept.setVisibility(View.VISIBLE);
            requestHolder.decline.setVisibility(View.VISIBLE);

            requestHolder.accept.setText(R.string.accept);

            requestHolder.accept.setOnClickListener(view -> {

                String bookOwner = mBook.getOwner();
                Fragment nextFrag = new SetLocationFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SLF_BOOK", mBook);
                bundle.putSerializable("SLF_REQUEST", request);
                bundle.putSerializable("SLF_BOOK_OWNER", bookOwner);
                bundle.putSerializable("SLF_BOOK_REQUESTER", mRequester.getEmail());
                nextFrag.setArguments(bundle);
                FragmentTransaction transaction = activity
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, nextFrag); // give your fragment
                // container id in first param
                transaction.addToBackStack(null);  // add transaction to backstack
                transaction.commit();
            });
            requestHolder.decline.setOnClickListener(view -> {

                // decline a book request in Firebase
                FirebaseIntegrity.declineBookRequest(request);
                sendNotificationRequestDeclined( request,  mBook);

            });
        }

        View.OnClickListener profileClickListener = view -> {
            FirebaseFirestore.getInstance().collection("users")
                    .document(mBook.getOwner())
                    .get().addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                if ((document != null) && (document.exists())) {
                    User bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
                    ViewProfileFragment nextFrag = ViewProfileFragment.newInstance(bookOwner,
                            mRequester);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VPF_CURRENT_USER", bookOwner);
                    bundle.putSerializable("VPF_PROFILE_USER", mRequester);
                    nextFrag.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(activity.findViewById(R.id.container).getId(), nextFrag)
                            .addToBackStack(null).commit();
                }
            });
        };

        requestHolder.userProfile.setOnClickListener(profileClickListener);
        requestHolder.username.setOnClickListener(profileClickListener);

    }

}