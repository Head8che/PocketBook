package com.example.pocketbook.adapter;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private BookList list;
    private User currentUser;
    private User bookOwner;
    private FragmentActivity activity;

    public BookAdapter(User currentUser, BookList list, FragmentActivity activity) {
        this.list = list;
        this.currentUser = currentUser;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = list.getBookAtPosition(position);
        holder.bind(book);

        /* DETERMINE WHICH PAGE SHOULD BE SELECTED, BASED ON IF USER IS OWNER OF BOOK */

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.getOwner().equals(currentUser.getEmail())) {
                    Log.e("OWNERIN", book.getOwner());
                    ViewMyBookFragment nextFrag = ViewMyBookFragment.newInstance(currentUser, book, list);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VMBF_USER", currentUser);
                    bundle.putSerializable("VMBF_BOOK", book);
                    bundle.putSerializable("VMBF_CATALOGUE", list);
                    nextFrag.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
                } else {
                    FirebaseFirestore.getInstance().collection("users").document(book.getOwner())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            bookOwner = FirebaseIntegrity.getUserFromFirestore(document);
                            ViewBookFragment nextFrag = ViewBookFragment.newInstance(currentUser,bookOwner, book);
                            activity.getSupportFragmentManager().beginTransaction().replace(activity.findViewById(R.id.container).getId(), nextFrag).addToBackStack(null).commit();
                        }
                    });
                }
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.getSize();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        //        TextView bookTitle;
//        TextView bookAuthor;
        ImageView bookCoverImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            bookTitle = itemView.findViewById(R.id.view_title);
//            bookAuthor = itemView.findViewById(R.id.view_author);
            bookCoverImageView = itemView.findViewById(R.id.itemBookBookCoverImageView);
        }

        public void bind(final Book book){

//            bookTitle.setText(book.getTitle());
//            bookAuthor.setText(book.getAuthor());

            GlideApp.with(Objects.requireNonNull(itemView.getContext()))
                    .load(book.getBookCover())
                    .into(bookCoverImageView);
        }
    }
}


