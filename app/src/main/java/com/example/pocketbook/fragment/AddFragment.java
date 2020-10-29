package com.example.pocketbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketbook.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFragment extends Fragment {

    private EditText authorText;
    private EditText titleText;
    private EditText isbnText;
    private EditText commentText;
    private Button addButton;
    private FirebaseFirestore mFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get all the EditText fields
        authorText = (EditText) view.findViewById(R.id.editText_author);
        titleText = (EditText) view.findViewById(R.id.editText_title);
        isbnText = (EditText) view.findViewById(R.id.editText_isbn);
        commentText = (EditText) view.findViewById(R.id.editText_comment);

        // get the button and create the listener
        addButton = view.findViewById(R.id.button_addBook);
        addButton.setOnClickListener(new View.OnClickListener() {

            final String author = authorText.getText().toString();
            final String title = titleText.getText().toString();
            final String isbn = isbnText.getText().toString();
            final String comment = commentText.getText().toString();


            @Override
            public void onClick(View view) {
                // create Book object and add to Firestore

            }
        });
    }
}
