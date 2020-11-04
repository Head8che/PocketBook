package com.example.pocketbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class EditBookActivity extends AppCompatActivity {

    private String bookTitle;
    private String bookAuthor;
    private String bookISBN;
    private String bookCondition;
    private String bookComment;

    TextInputEditText layoutBookTitle;
    TextInputEditText layoutBookAuthor;
    TextInputEditText layoutBookISBN;
    ImageView layoutBookCover;
    TextInputEditText layoutBookCondition;
    TextInputEditText layoutBookComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        Intent intent = getIntent();
        Book book = (Book) intent.getSerializableExtra("VMBBF_BOOK");
        StorageReference bookCover = book.getBookCover();

        bookTitle = book.getTitle();
        bookAuthor = book.getAuthor();
        bookISBN = book.getISBN();
        bookCondition = book.getCondition();
        bookComment = book.getComment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.editBookToolbar);
        ImageView cancelButton = (ImageView) findViewById(R.id.editBookCancelBtn);
        TextView saveButton = (TextView) findViewById(R.id.editBookSaveBtn);

        layoutBookTitle = (TextInputEditText) findViewById(R.id.editBookTitleField);
        layoutBookAuthor = (TextInputEditText) findViewById(R.id.editBookAuthorField);
        layoutBookISBN = (TextInputEditText) findViewById(R.id.editBookISBNField);
        layoutBookCover = (ImageView) findViewById(R.id.editBookBookCoverField);
        layoutBookCondition = (TextInputEditText) findViewById(R.id.editBookConditionField);
        layoutBookComment = (TextInputEditText) findViewById(R.id.editBookCommentField);

        layoutBookTitle.setText(bookTitle);
        layoutBookAuthor.setText(bookAuthor);
        layoutBookISBN.setText(bookISBN);
        layoutBookCondition.setText(bookCondition);
        layoutBookComment.setText(bookComment);

        GlideApp.with(Objects.requireNonNull(getApplicationContext()))
                .load(bookCover)
                .into(layoutBookCover);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (noChanges()) {
            finish();
        } else {
            showCancelDialog();
        }
    }

    private void showCancelDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_discard_changes, null);

        Button keepEditingBtn = view.findViewById(R.id.keepEditingBtn);
        TextView discardBtn = view.findViewById(R.id.discardBtn);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        keepEditingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SystemClock.sleep(300);
                finish();
            }
        });
    }

    private boolean noChanges() {
        return bookTitle.equals(layoutBookTitle.getText().toString())
                && bookAuthor.equals(layoutBookAuthor.getText().toString())
                && bookISBN.equals(layoutBookISBN.getText().toString())
                && bookCondition.equals(layoutBookCondition.getText().toString())
                && bookComment.equals(layoutBookComment.getText().toString())
                ;
    }
}