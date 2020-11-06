package com.example.pocketbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewMyBookRequestsFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.RequestList;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A {@link RecyclerView.Adapter<RequestAdapter.ViewHolder>} subclass.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    private Book mBook;
    private RequestList mRequestList;
    private User mRequester;
    private String username;

    /**
     * constructor for the RequestAdapter
     * @param mBook: the book for which the requests are being viewed
     */
    public RequestAdapter(Book mBook) {
        this.mBook = mBook;
        this.mRequestList = mBook.getRequestList();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {

        Request request = mRequestList.getRequestAtPosition(position);
        holder.bind(request);
    }
    
    @Override
    public int getItemCount() {
        return mRequestList.getSize();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private TextView date;
        private CircleImageView userProfile;
        private Button accept;
        private Button decline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.itemRequestUsernameTextView);
            date = itemView.findViewById(R.id.itemRequestDateTextView);
            userProfile = itemView.findViewById(R.id.itemRequestProfileImageView);
            accept = itemView.findViewById(R.id.itemRequestAcceptButton);
            decline = itemView.findViewById(R.id.itemRequestDeclineButton);
        }


        public void bind(Request request){
            String requesterEmail = request.getRequester();
            FirebaseFirestore.getInstance().collection("users").document(requesterEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                mRequester = new User(document.getString("firstName"),document.getString("lastName"),document.getString("email")
                                ,document.getString("username"),document.getString("password"),document.getString("photo"));
                                username.setText(mRequester.getUsername());
                                GlideApp.with(Objects.requireNonNull(itemView.getContext()))
                                        .load(mRequester.getProfilePicture())
                                        .into(userProfile);
                            }
                        }
                    });
            date.setText(request.getRequestDate());
            if (mBook.getStatus().equals("ACCEPTED")){
                accept.setText("Accepted");
                accept.setClickable(false);
            }

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBook.acceptRequest(request);
                    notifyDataSetChanged();
                    accept.setText("Accepted");
                    accept.setClickable(false);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mBook.declineRequest(request))
                        notifyDataSetChanged();
                }
            });

        }
    }
}
