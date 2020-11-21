

package com.example.pocketbook.activity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.Objects;


public class ScanActivity extends AppCompatActivity implements View.OnClickListener{
    private int LAUNCH_ADD_BOOK_CODE = 1234;
    private int SEE_DESCRIPTION_CODE = 1111;
    private int LEND_BOOK_CODE = 2222;
    private int BORROW_BOOK_CODE = 3333;
    private int RETURN_BOOK_CODE = 4444;
    private int RECEIVE_BOOK_CODE = 5555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        showScanningSpinnerDialog();
        setContentView(R.layout.activity_scan);

    }

    /**
     * Spinner Dialog that allows the user to choose what they want to scan for
     */
    private void showScanningSpinnerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog_scanning_spinner, null);

        // access the spinner text fields
        TextView descriptionOption = view.findViewById(R.id.spinnerDialogSeeBookDescriptionField);
        TextView lendOption = view.findViewById(R.id.spinnerDialogLendBookField);
        TextView borrowOption = view.findViewById(R.id.spinnerDialogBorrowBookField);
        TextView returnOption = view.findViewById(R.id.spinnerDialogReturnBookField);
        TextView receiveOption = view.findViewById(R.id.spinnerDialogReceiveBookField);
        TextView selectedOption;

        // create the scanning dialogspinner
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
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
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("SCAN AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("Finished", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this,"NO Results",Toast.LENGTH_LONG).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
