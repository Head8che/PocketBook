package com.example.pocketbook.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends FirestoreRecyclerAdapter<Request, RequestAdapter.RequestHolder> {
    private Book mBook;
    private User mRequester;

    public RequestAdapter(@NonNull FirestoreRecyclerOptions<Request> options, Book mBook) {
        super(options);
        this.mBook = mBook;
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
            username = itemView.findViewById(R.id.itemRequestUsernameTextView);
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
        return new RequestHolder(inflater.inflate(R.layout.item_request, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder requestHolder,
                                    int position, @NonNull Request request) {

        //get the requester's info from Firestore to display it to the owner
        String requesterEmail = request.getRequester();

        FirebaseFirestore.getInstance().collection("users").document(requesterEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            mRequester = FirebaseIntegrity.getUserFromFirestore(document);
                            requestHolder.username.setText(mRequester.getUsername());
                            GlideApp.with(Objects.requireNonNull(requestHolder.itemView.getContext()))
                                    .load(FirebaseIntegrity.getProfilePicture(mRequester))
                                    .into(requestHolder.userProfile);
                        }
                    }
                });
        requestHolder.date.setText(request.getRequestDate());

        //if the user already accepted a request, they can't accept or decline that request
        if (mBook.getStatus().equals("ACCEPTED")){
            requestHolder.accept.setText("Accepted");
            requestHolder.accept.setEnabled(false);
            requestHolder.decline.setEnabled(false);
        }

        //when the user taps on the accept button for a request, the request is accepted and they can't decline that request
        requestHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NOTE: the local acceptRequest is purely for testing; FirebaseIntegrity will
                //  overwrite all locally set data with the appropriate Firebase data

//                mBook.acceptRequest(request);

                // accept a book request in Firebase
                FirebaseIntegrity.acceptBookRequest(request);

                notifyDataSetChanged();
                requestHolder.accept.setText("Accepted");
                requestHolder.accept.setEnabled(false);
                requestHolder.decline.setEnabled(false);
            }
        });

        //when the user taps on the decline button for a request, that request is declined
        requestHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NOTE: the local declineRequest is purely for testing; FirebaseIntegrity will
                //  overwrite all locally set data with the appropriate Firebase data

//                mBook.declineRequest(request);

                // decline a book request in Firebase
                FirebaseIntegrity.declineBookRequest(request);
            }
        });

    }
}

///**
// * A {@link RecyclerView.Adapter<RequestAdapter.ViewHolder>} subclass.
// */
//public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{
//
//    private Book mBook;
//    private RequestList mRequestList;
//    private User mRequester;
//
//    /**
//     * constructor for the RequestAdapter
//     * @param mBook: the book for which the requests are being viewed
//     */
//    public RequestAdapter(Book mBook) {
//        this.mBook = mBook;
//        this.mRequestList = mBook.getRequestList();
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        return new ViewHolder(inflater.inflate(R.layout.item_request, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
//
//        Request request = mRequestList.getRequestAtPosition(position);
//        holder.bind(request);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mRequestList.getSize();
//    }
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        private TextView username;
//        private TextView date;
//        private CircleImageView userProfile;
//        private Button accept;
//        private Button decline;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            //get the views' ids
//            username = itemView.findViewById(R.id.itemRequestUsernameTextView);
//            date = itemView.findViewById(R.id.itemRequestDateTextView);
//            userProfile = itemView.findViewById(R.id.itemRequestProfileImageView);
//            accept = itemView.findViewById(R.id.itemRequestAcceptButton);
//            decline = itemView.findViewById(R.id.itemRequestDeclineButton);
//        }
//
//
//        public void bind(Request request){
//
//            //get the requester's info from Firestore to display it to the owner
//            String requesterEmail = request.getRequester();
//            FirebaseFirestore.getInstance().collection("users").document(requesterEmail)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                mRequester = FirebaseIntegrity.getUserFromFirestore(document);
//                                username.setText(mRequester.getUsername());
//                                GlideApp.with(Objects.requireNonNull(itemView.getContext()))
//                                        .load(mRequester.getProfilePicture())
//                                        .into(userProfile);
//                            }
//                        }
//                    });
//            date.setText(request.getRequestDate());
//
//            //if the user already accepted a request, they can't accept or decline that request
//            if (mBook.getStatus().equals("ACCEPTED")){
//                accept.setText("Accepted");
//                accept.setEnabled(false);
//                decline.setEnabled(false);
//            }
//
//            //when the user taps on the accept button for a request, the request is accepted and they can't decline that request
//            accept.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mBook.acceptRequest(request);
//                    notifyDataSetChanged();
//                    accept.setText("Accepted");
//                    accept.setEnabled(false);
//                    decline.setEnabled(false);
//                }
//            });
//
//            //when the user taps on the decline button for a request, that request is declined
//            decline.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(mBook.declineRequest(request))
//                        notifyDataSetChanged();
//                }
//            });
//        }
//    }
//}
