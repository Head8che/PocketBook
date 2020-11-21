package com.example.pocketbook.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.GlideApp;
import com.example.pocketbook.R;
import com.example.pocketbook.activity.CaptureScanActivity;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.adapter.BookAdapter;
import com.example.pocketbook.fragment.ViewBookFragment;
import com.example.pocketbook.fragment.ViewMyBookFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.Objects;


public class ScanFragment extends Fragment  implements View.OnClickListener {
    private int LAUNCH_ADD_BOOK_CODE = 1234;
    private int SEE_DESCRIPTION_CODE = 1111;
    private int LEND_BOOK_CODE = 2222;
    private int BORROW_BOOK_CODE = 3333;
    private int RETURN_BOOK_CODE = 4444;
    private int RECEIVE_BOOK_CODE = 5555;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private Book book;
    private User currentUser;
    private AlertDialog alertDialog;

    public static ScanFragment newInstance(User user){
        ScanFragment scanFragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putSerializable("SA_USER",user);
        scanFragment.setArguments(args);
        return scanFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showScanningSpinnerDialog();
        mFirestore = FirebaseFirestore.getInstance();
        IntentIntegrator.forSupportFragment(this);

        if (getArguments() != null){
            this.currentUser = (User) getArguments().getSerializable("SA_USER");
        }
    }

    /**
     * Spinner Dialog that allows the user to choose what they want to scan for
     */
    private void showScanningSpinnerDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.alert_dialog_scanning_spinner, null);

        // access the spinner text fields
        TextView descriptionOption = view.findViewById(R.id.spinnerDialogSeeBookDescriptionField);
        TextView lendOption = view.findViewById(R.id.spinnerDialogLendBookField);
        TextView borrowOption = view.findViewById(R.id.spinnerDialogBorrowBookField);
        TextView returnOption = view.findViewById(R.id.spinnerDialogReturnBookField);
        TextView receiveOption = view.findViewById(R.id.spinnerDialogReceiveBookField);
        TextView selectedOption;

        // create the scanning dialogspinner
        alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        // start ScanActivity appropriately based on the selected scanning dialog option
        descriptionOption.setOnClickListener(this);


        // start ScanActivity appropriately based on the selected scanning dialog option
        lendOption.setOnClickListener(this);

        // start ScanActivity appropriately based on the selected scanning dialog option
        borrowOption.setOnClickListener(this);


        // start ScanActivity appropriately based on the selected scanning dialog option
        returnOption.setOnClickListener(this);


        // start ScanActivity appropriately based on the selected scanning dialog option
        receiveOption.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScanFragment.this);
        integrator.setCaptureActivity(CaptureScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);
//        ImageView backButton = (ImageView) view.findViewById(R.id.viewMyBookFragBackBtn);
//        backButton.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if (result != null){
            if (result.getContents() != null){
                mFirestore.collection("catalogue").whereEqualTo("isbn",  String.valueOf(result.getContents())).limit(1) // getting only 1 book with given ISBN
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("SIZE", String.valueOf(task.getResult().size()));
                            if (task.getResult().size() == 0) {
                                Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_SHORT).show();
                            } else {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // retrieving the book
                                    Book book = FirebaseIntegrity.getBookFromFirestore(document);
                                    Log.d("CHECK","POINT1");


                                    // if currentUser owns book, go to viewMyBookFrag
                                    //
                                    if (book.getOwner() == currentUser.getEmail()) {
                                        Log.d("CHECK","POINT2");
                                        ViewMyBookFragment f = ViewMyBookFragment.newInstance(currentUser, book);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("VMBF_USER", currentUser);
                                        bundle.putSerializable("VMBF_BOOK", book);
                                        f.setArguments(bundle);
                                        alertDialog.dismiss();
                                        getActivity().getSupportFragmentManager().beginTransaction()
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
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container, nextFrag)
                                                        .addToBackStack(null).commit();
                                            }
                                        });
                                    }

                                    Log.d("ScanActivity", document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.d("ScanActivity", "Error getting documents: ", task.getException());
                        }

                    }
                });

//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
            else{
                Toast.makeText(getActivity(), "Scanning has been canceled", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
