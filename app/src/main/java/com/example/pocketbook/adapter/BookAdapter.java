package com.example.pocketbook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.RequestList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class BookAdapter extends FirestoreRecyclerAdapter<Book, BookAdapter.BookHolder> {
    private BookList list;
    private User currentUser;
    private User bookOwner;
    private FragmentActivity activity;
    private FirebaseAuth mAuth;

    public BookAdapter(@NonNull FirestoreRecyclerOptions<Book> options, User currentUser,
                       BookList list, FragmentActivity activity) {
        super(options);
//        this.list = list;
        this.currentUser = currentUser;
        this.activity = activity;
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView bookAuthor;
        ImageView bookCoverImageView;
        ImageView statusImageView;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.itemBookTitle);
            bookAuthor = itemView.findViewById(R.id.itemBookAuthor);
            bookCoverImageView = itemView.findViewById(R.id.itemBookBookCoverImageView);
            statusImageView = itemView.findViewById(R.id.itemBookStatus);

        }
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BookHolder(inflater.inflate(R.layout.item_book, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder bookHolder,
                                    int position, @NonNull Book book) {

        bookHolder.bookTitle.setText(book.getTitle());
        bookHolder.bookAuthor.setText(book.getAuthor());

        GlideApp.with(Objects.requireNonNull(activity.getBaseContext()))
                .load(book.getBookCover())
                .into(bookHolder.bookCoverImageView);

        switch (book.getStatus().toUpperCase()) {

            // if the book is borrowed or accepted, it is not available for requesting
            case "BORROWED":
                bookHolder.statusImageView.setImageResource(R.drawable.ic_borrowed);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorBorrowed),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "ACCEPTED":
                bookHolder.statusImageView.setImageResource(R.drawable.ic_accepted);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAccepted),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            case "REQUESTED":
                // if the user has already requested the book, it is not available for requesting
                bookHolder.statusImageView.setImageResource(R.drawable.ic_requested);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorRequested),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                break;

            // default case is that the book is available
            default:
                bookHolder.statusImageView.setImageResource(R.drawable.ic_available);
                bookHolder.statusImageView.setColorFilter(ContextCompat
                                .getColor(activity.getBaseContext(), R.color.colorAvailable),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        }

        bookHolder.itemView.setOnClickListener(v -> {
            if (book.getOwner().equals(currentUser.getEmail())) {
                Log.e("OWNERIN", book.getOwner());
                ViewMyBookFragment nextFrag = ViewMyBookFragment
                        .newInstance(currentUser, book, list);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VMBF_USER", currentUser);
                bundle.putSerializable("VMBF_BOOK", book);
                bundle.putSerializable("VMBF_CATALOGUE", list);
                nextFrag.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(activity.findViewById(R.id.container).getId(), nextFrag)
                        .addToBackStack(null).commit();
            } else {
                FirebaseFirestore.getInstance().collection("users")
                        .document(book.getOwner())
                        .get().addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if ((document != null) && (document.exists())) {
                        bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
                        ViewBookFragment nextFrag = ViewBookFragment
                                .newInstance(currentUser, bookOwner, book);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BA_CURRENTUSER", currentUser);
                        bundle.putSerializable("BA_BOOK", book);
                        bundle.putSerializable("BA_BOOKOWNER", bookOwner);
                        nextFrag.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(activity.findViewById(R.id.container).getId(), nextFrag)
                                .addToBackStack(null).commit();
                    }
                });
            }

        });

    }
}

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Book book = list.getBookAtPosition(position);
//        holder.bind(book, activity.getBaseContext());
//
//        /* DETERMINE WHICH PAGE SHOULD BE SELECTED, BASED ON IF USER IS OWNER OF BOOK */
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (book.getOwner().equals(currentUser.getEmail())) {
//                    Log.e("OWNERIN", book.getOwner());
//                    ViewMyBookFragment nextFrag = ViewMyBookFragment.newInstance(currentUser, book, list);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("VMBF_USER", currentUser);
//                    bundle.putSerializable("VMBF_BOOK", book);
//                    bundle.putSerializable("VMBF_CATALOGUE", list);
//                    nextFrag.setArguments(bundle);
//                    activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
//                } else {
//                    FirebaseFirestore.getInstance().collection("users").document(book.getOwner())
//                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            DocumentSnapshot document = task.getResult();
//                            bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
//                            ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser,bookOwner, book);
//                            activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
//                        }
//                    });
//                }
//
//            }
//        });
//    }

//    @Override
//    public int getItemCount() {
//        return list.getSize();
//    }

//        public void bind(final Book book, Context context) {
//
//            bookTitle.setText(book.getTitle());
//            bookAuthor.setText(book.getAuthor());
//
//            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
//                    .load(book.getBookCover())
//                    .into(bookCoverImageView);
//
//
//            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();//the current's user email
//            switch (book.getStatus().toUpperCase()) {
//
//                //if the book is borrowed or accepted by another user, it is not available for requesting
//                case "BORROWED":
//                    statusImageView.setImageResource(R.drawable.ic_borrowed);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorBorrowed),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "ACCEPTED":
//                    statusImageView.setImageResource(R.drawable.ic_accepted);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccepted),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "REQUESTED":
//                    //if the book has been requested by this user before, it is not available for requesting again
//                    if (book.getRequestList().containsRequest(email)) {
//                        statusImageView.setImageResource(R.drawable.ic_requested);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorRequested),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    //if the book has no requests or hasn't been requested by the user yet, it is available for requesting
//                    else {
//                        statusImageView.setImageResource(R.drawable.ic_available);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    break;
//
//                //default case when the book is available
//                default:
//                    statusImageView.setImageResource(R.drawable.ic_available);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//            }
//        }


//    static class ViewHolder extends RecyclerView.ViewHolder{
//        TextView bookTitle;
//        TextView bookAuthor;
//        ImageView bookCoverImageView;
//        ImageView statusImageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            bookTitle = itemView.findViewById(R.id.itemBookTitle);
//            bookAuthor = itemView.findViewById(R.id.itemBookAuthor);
//            bookCoverImageView = itemView.findViewById(R.id.itemBookBookCoverImageView);
//            statusImageView = itemView.findViewById(R.id.itemBookStatus);
//
//        }
//
//        public void bind(final Book book, Context context){
//
//            bookTitle.setText(book.getTitle());
//            bookAuthor.setText(book.getAuthor());
//
//            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
//                    .load(book.getBookCover())
//                    .into(bookCoverImageView);
//
//
//            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();//the current's user email
//            switch(book.getStatus().toUpperCase()) {
//
//                //if the book is borrowed or accepted by another user, it is not available for requesting
//                case "BORROWED":
//                    statusImageView.setImageResource(R.drawable.ic_borrowed);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorBorrowed),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "ACCEPTED":
//                    statusImageView.setImageResource(R.drawable.ic_accepted);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccepted),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "REQUESTED":
//                    //if the book has been requested by this user before, it is not available for requesting again
//                    if (book.getRequestList().containsRequest(email)){
//                        statusImageView.setImageResource(R.drawable.ic_requested);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorRequested),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    //if the book has no requests or hasn't been requested by the user yet, it is available for requesting
//                    else {
//                        statusImageView.setImageResource(R.drawable.ic_available);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    break;
//
//                //default case when the book is available
//                default:
//                    statusImageView.setImageResource(R.drawable.ic_available);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//            }
//        }
//    }






//package com.example.pocketbook.adapter;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.pocketbook.GlideApp;
//import com.example.pocketbook.R;
//import com.example.pocketbook.fragment.ViewBookFragment;
//import com.example.pocketbook.fragment.ViewMyBookFragment;
//import com.example.pocketbook.model.Book;
//import com.example.pocketbook.model.BookList;
//import com.example.pocketbook.model.User;
//import com.example.pocketbook.util.FirebaseIntegrity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.Objects;
//
//public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
//    private BookList list;
//    private User currentUser;
//    private User bookOwner;
//    private FragmentActivity activity;
//    private FirebaseAuth mAuth;
//
//    public BookAdapter(User currentUser, BookList list, FragmentActivity activity) {
//        this.list = list;
//        this.currentUser = currentUser;
//        this.activity = activity;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        return new ViewHolder(inflater.inflate(R.layout.item_book, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Book book = list.getBookAtPosition(position);
//        holder.bind(book, activity.getBaseContext());
//
//        /* DETERMINE WHICH PAGE SHOULD BE SELECTED, BASED ON IF USER IS OWNER OF BOOK */
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (book.getOwner().equals(currentUser.getEmail())) {
//                    Log.e("OWNERIN", book.getOwner());
//                    ViewMyBookFragment nextFrag = ViewMyBookFragment.newInstance(currentUser, book, list);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("VMBF_USER", currentUser);
//                    bundle.putSerializable("VMBF_BOOK", book);
//                    bundle.putSerializable("VMBF_CATALOGUE", list);
//                    nextFrag.setArguments(bundle);
//                    activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
//                } else {
//                    FirebaseFirestore.getInstance().collection("users").document(book.getOwner())
//                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            DocumentSnapshot document = task.getResult();
//                            bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
//                            ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser,bookOwner, book);
//                            activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
//                        }
//                    });
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.getSize();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder{
//                TextView bookTitle;
//        TextView bookAuthor;
//        ImageView bookCoverImageView;
//        ImageView statusImageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            bookTitle = itemView.findViewById(R.id.itemBookTitle);
//            bookAuthor = itemView.findViewById(R.id.itemBookAuthor);
//            bookCoverImageView = itemView.findViewById(R.id.itemBookBookCoverImageView);
//            statusImageView = itemView.findViewById(R.id.itemBookStatus);
//
//        }
//
//        public void bind(final Book book, Context context){
//
//            bookTitle.setText(book.getTitle());
//            bookAuthor.setText(book.getAuthor());
//
//            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
//                    .load(book.getBookCover())
//                    .into(bookCoverImageView);
//
//
//            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();//the current's user email
//            switch(book.getStatus().toUpperCase()) {
//
//                //if the book is borrowed or accepted by another user, it is not available for requesting
//                case "BORROWED":
//                    statusImageView.setImageResource(R.drawable.ic_borrowed);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorBorrowed),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "ACCEPTED":
//                    statusImageView.setImageResource(R.drawable.ic_accepted);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccepted),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//                    break;
//
//                case "REQUESTED":
//                    //if the book has been requested by this user before, it is not available for requesting again
//                    if (book.getRequestList().containsRequest(email)){
//                        statusImageView.setImageResource(R.drawable.ic_requested);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorRequested),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    //if the book has no requests or hasn't been requested by the user yet, it is available for requesting
//                    else {
//                        statusImageView.setImageResource(R.drawable.ic_available);
//                        statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                                android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                    break;
//
//                //default case when the book is available
//                default:
//                    statusImageView.setImageResource(R.drawable.ic_available);
//                    statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAvailable),
//                            android.graphics.PorterDuff.Mode.SRC_IN);
//            }
//        }
//    }
//}
//
//
