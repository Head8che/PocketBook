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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.CaptureActivityPortrait;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.fragment.SetLocationFragment;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Objects;

public class ScanHandler {

    private AlertDialog alertDialog;
    private Activity activity;
    private User currentUser;
    private FragmentManager fragmentManager;
    private String userSelection;

    public ScanHandler(Activity activity, FragmentManager fragmentManager, User currentUser) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.currentUser = currentUser;
    }

    /**
     * dismisses Alert dialog
     */
    public void dismissAlertDialog() {
        if (this.alertDialog != null) {
            this.alertDialog.dismiss();
        }
    }

    /**
     * Getter method for userSelection
     * @return userSelection as String
     */
    public String getUserSelection() {
        return userSelection;
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

        alertDialog.setOnDismissListener(dialog -> {
            if (activity.getClass().equals(HomeActivity.class)) {

                HomeActivity homeActivity = ((HomeActivity) activity);

                switch (homeActivity.getFragTag()) {
                    case "HOME_FRAGMENT":
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_home);
                        break;
                    case "SEARCH_FRAGMENT":
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_search);
                        break;
                    default:
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_profile);
                        break;
                }
            }
            dismissAlertDialog();
        });

        alertDialog.setOnCancelListener(dialog -> {
            if (activity.getClass().equals(HomeActivity.class)) {

                HomeActivity homeActivity = ((HomeActivity) activity);

                switch (homeActivity.getFragTag()) {
                    case "HOME_FRAGMENT":
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_home);
                        break;
                    case "SEARCH_FRAGMENT":
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_search);
                        break;
                    default:
                        homeActivity.getBottomNav().setSelectedItemId(R.id.bottom_nav_profile);
                        break;
                }
            }
            dismissAlertDialog();
        });

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
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
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
                                    dismissAlertDialog();
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
                                            dismissAlertDialog();
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.container, nextFrag)
                                                    .addToBackStack(null).commit();
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        Log.e("ScanHandler",
                                "Error getting documents: ", task.getException());
                    }

                });
    }

    public void handleLendBook(String scannedIsbn) {
        // exchange, owner=currentUser, isbn=isbn, ownerBookStatus=ACCEPTED
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
                            dismissAlertDialog();
                        } else {

                            dismissAlertDialog();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // retrieving the book
                                Book book = FirebaseIntegrity.getBookFromFirestore(document);

                                if (book != null) {

                                    if (!book.getStatus().equals("ACCEPTED")) {
                                        Toast.makeText(activity, scannedIsbn
                                                        + " is not an accepted book!",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        return;
                                    }

                                    String bookOwner = book.getOwner();
                                    Fragment nextFrag = new SetLocationFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("SLF_BOOK", book);
                                    bundle.putSerializable("SLF_BOOK_OWNER", bookOwner);
                                    bundle.putSerializable("SLF_CURRENT_USER", currentUser);
                                    nextFrag.setArguments(bundle);
                                    FragmentTransaction transaction = fragmentManager
                                            .beginTransaction();
                                    transaction.replace(R.id.container, nextFrag);
                                    // container id in first param
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    Toast.makeText(activity, "Please re-scan the ISBN",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    } else {
                        Log.e("ScanHandler",
                                "Error getting documents: ", task.getException());
                    }

                });

    }

    public void handleBorrowBook(String scannedIsbn) {
        // exchange, borrower=currentUser, owner=book.getOwner(),isbn=isbn,ownerBookStatus=BORROWED

        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .whereEqualTo("isbn", scannedIsbn).limit(1) // get only 1 book with given isbn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("SIZE", String.valueOf(task.getResult().size()));
                        if (task.getResult().size() == 0) {
                            dismissAlertDialog();
                            Toast.makeText(activity, "No Results Found for ISBN: "
                                    + scannedIsbn, Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            dismissAlertDialog();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // retrieving the book
                                Book book = FirebaseIntegrity.getBookFromFirestore(document);

                                if (book.getStatus().equals("BORROWED")) {
                                    Log.e("BORROW_BOOK", "book is not borrowed");
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("exchange")
                                        .whereEqualTo("borrower", currentUser.getEmail())
                                        .whereEqualTo("owner", book.getOwner())
                                        .whereEqualTo("relatedBook", book.getId())
                                        .whereEqualTo("ownerBookStatus", "BORROWED")
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.e("SIZE",
                                                        String.valueOf(task1.getResult().size()));
                                                if (task1.getResult().size() == 0) {
                                                    Toast.makeText(activity,
                                                            "No Results Found for ISBN: "
                                                            + scannedIsbn, Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {

                                                    for (QueryDocumentSnapshot document1
                                                            : task1.getResult()) {

                                                        FirebaseFirestore
                                                                .getInstance()
                                                                .collection("exchange")
                                                                .document(document1.getId())
                                                                .update("borrowerBookStatus",
                                                                        "BORROWED");

                                                            FirebaseIntegrity
                                                                    .setBookStatusFirebase(book,
                                                                    "BORROWED");

                                                    }
                                                }
                                            } else {
                                                Log.e("ScanHandler",
                                                        "Error getting documents: ",
                                                        task1.getException());
                                            }

                                        });

                            }
                        }
                    } else {
                        Log.e("ScanHandler",
                                "Error getting documents: ", task.getException());
                    }

                });

    }

    public void handleReturnBook(String scannedIsbn) {
        // exchange,borrower=currentUser,owner=book.getOwner(),isbn=isbn,borrowerBookStatus=BORROWED
        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .whereEqualTo("isbn", scannedIsbn).limit(1) // get only 1 book with given isbn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dismissAlertDialog();
                        Log.e("SIZE", String.valueOf(task.getResult().size()));
                        if (task.getResult().size() == 0) {
                            Toast.makeText(activity, "No Results Found for ISBN: "
                                    + scannedIsbn, Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            dismissAlertDialog();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // retrieving the book
                                Book book = FirebaseIntegrity.getBookFromFirestore(document);

                                FirebaseFirestore.getInstance()
                                        .collection("exchange")
                                        .whereEqualTo("borrower", currentUser.getEmail())
                                        .whereEqualTo("owner", book.getOwner())
                                        .whereEqualTo("relatedBook", book.getId())
                                        .whereEqualTo("borrowerBookStatus", "BORROWED")
                                        .get() // get only 1 book with given isbn
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.e("SIZE",
                                                        String.valueOf(task1.getResult().size()));
                                                if (task1.getResult().size() == 0) {
                                                    Toast.makeText(activity,
                                                            "No Results Found for ISBN: "
                                                                    + scannedIsbn, Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {

                                                    for (QueryDocumentSnapshot document1
                                                            : task1.getResult()) {

                                                        FirebaseFirestore.getInstance()
                                                                .collection("exchange")
                                                                .document(document1.getId())
                                                                .update("borrowerBookStatus",
                                                                        "AVAILABLE");

                                                        FirebaseIntegrity
                                                                .deleteBookRequest(book.getId(),
                                                                        currentUser.getEmail());

                                                        FirebaseIntegrity
                                                                .setBookStatusFirebase(book,
                                                                        "AVAILABLE");

                                                    }
                                                }
                                            } else {
                                                Log.e("ScanHandler",
                                                        "Error getting documents: ",
                                                        task1.getException());
                                            }

                                        });
                            }
                        }
                    } else {
                        Log.e("ScanHandler",
                                "Error getting documents: ", task.getException());
                    }

                });

    }

    public void handleReceiveBook(String scannedIsbn) {
        // exchange, owner=currentUser, isbn=isbn, borrowerBookStatus=AVAILABLE

        FirebaseFirestore.getInstance()
                .collection("catalogue")
                .whereEqualTo("isbn", scannedIsbn).limit(1) // get only 1 book with given isbn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dismissAlertDialog();
                        Log.e("SIZE", String.valueOf(task.getResult().size()));
                        if (task.getResult().size() == 0) {
                            Toast.makeText(activity, "No Results Found for ISBN: "
                                    + scannedIsbn, Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            dismissAlertDialog();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // retrieving the book
                                Book book = FirebaseIntegrity.getBookFromFirestore(document);

                                if (book.getStatus().equals("AVAILABLE")) {
                                    Log.e("RETURN_BOOK", "book is not available");
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("exchange")
                                        .whereEqualTo("owner", currentUser.getEmail())
                                        .whereEqualTo("relatedBook", book.getId())
                                        .whereEqualTo("borrowerBookStatus", "AVAILABLE")
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.e("SIZE",
                                                        String.valueOf(task1.getResult().size()));
                                                if (task1.getResult().size() == 0) {
                                                    Toast.makeText(activity,
                                                            "No Results Found for ISBN: "
                                                                    + scannedIsbn,
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {

                                                    for (QueryDocumentSnapshot document1
                                                            : task1.getResult()) {

                                                        FirebaseFirestore
                                                                .getInstance()
                                                                .collection("exchange")
                                                                .document(document1.getId())
                                                                .delete();

                                                        FirebaseIntegrity
                                                                .setBookStatusFirebase(book,
                                                                        "AVAILABLE");

                                                    }
                                                }
                                            } else {
                                                Log.e("ScanHandler",
                                                        "Error getting documents: ",
                                                        task1.getException());
                                            }

                                        });

                            }
                        }
                    } else {
                        Log.e("ScanHandler",
                                "Error getting documents: ", task.getException());
                    }

                });

    }
}
