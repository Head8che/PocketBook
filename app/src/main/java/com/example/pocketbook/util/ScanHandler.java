package com.example.pocketbook.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.example.pocketbook.R;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Objects;

public class ScanHandler {

    AlertDialog alertDialog;
    Activity activity;
    User currentUser;
    FragmentManager fragmentManager;
    public String userSelection;

    public ScanHandler(Activity activity, FragmentManager fragmentManager, User currentUser) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.currentUser = currentUser;
    }

    /**
     * Spinner Dialog that allows the user to choose what they want to scan for
     */
    public void showScanningSpinnerDialog() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.alert_dialog_scanning_spinner, null);

        // access the spinner text fields
        TextView descriptionOption = view.findViewById(R.id.spinnerDialogSeeBookDescriptionField);
        TextView lendOption = view.findViewById(R.id.spinnerDialogLendBookField);
        TextView borrowOption = view.findViewById(R.id.spinnerDialogBorrowBookField);
        TextView returnOption = view.findViewById(R.id.spinnerDialogReturnBookField);
        TextView receiveOption = view.findViewById(R.id.spinnerDialogReceiveBookField);

        // create the scanning dialogSpinner
        alertDialog = new AlertDialog.Builder(activity).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        // scan code appropriately based on the selected scanning dialog option
        descriptionOption.setOnClickListener(v -> {
            userSelection = "SEE_DESCRIPTION_CODE";
            scanCode();
        });

        // scan code appropriately based on the selected scanning dialog option
        lendOption.setOnClickListener(v -> {
            userSelection = "LEND_BOOK_CODE";
            scanCode();
        });

        // scan code appropriately based on the selected scanning dialog option
        borrowOption.setOnClickListener(v -> {
            userSelection = "BORROW_BOOK_CODE";
            scanCode();
        });

        // scan code appropriately based on the selected scanning dialog option
        returnOption.setOnClickListener(v -> {
            userSelection = "RETURN_BOOK_CODE";
            scanCode();
        });

        // scan code appropriately based on the selected scanning dialog option
        receiveOption.setOnClickListener(v -> {
            userSelection = "RECEIVE_BOOK_CODE";
            scanCode();
        });

    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public void handleSeeDescription(String scannedIsbn) {
        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .whereEqualTo("isbn", scannedIsbn).limit(1) // get only 1 book with given isbn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("SIZE", String.valueOf(task.getResult().size()));
                        if (task.getResult().size() == 0) {
                            Toast.makeText(activity, "No Results Found for ISBN: "
                                            + scannedIsbn, Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // retrieving the book
                                Book book = FirebaseIntegrity.getBookFromFirestore(document);

                                // if currentUser owns book, go to viewMyBookFrag
                                if (book.getOwner().equals(currentUser.getEmail())) {
                                    ViewMyBookFragment f = ViewMyBookFragment
                                            .newInstance(currentUser, book);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("VMBF_USER", currentUser);
                                    bundle.putSerializable("VMBF_BOOK", book);
                                    f.setArguments(bundle);
                                    alertDialog.dismiss();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.container, f)
                                            .addToBackStack(null).commit();
                                }

                                // currentUser doesn't own the book, go to viewBookFrag
                                else {
                                    // find book's owner
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(book.getOwner())
                                            .get().addOnCompleteListener(t -> {
                                        DocumentSnapshot d = t.getResult(); // book's owner

                                        // if an owner exists
                                        if ((d != null) && (d.exists())) {
                                            User bookOwner;
                                            bookOwner = FirebaseIntegrity.getUserFromFirestore(d);
                                            ViewBookFragment nextFrag = ViewBookFragment
                                                    .newInstance(currentUser, bookOwner, book);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("BA_CURRENTUSER", currentUser);
                                            bundle.putSerializable("BA_BOOK", book);
                                            bundle.putSerializable("BA_BOOKOWNER", bookOwner);
                                            nextFrag.setArguments(bundle);
                                            alertDialog.dismiss();
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.container, nextFrag)
                                                    .addToBackStack(null).commit();
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        Log.e("ScanActivity",
                                "Error getting documents: ", task.getException());
                    }

                });
    }
}
