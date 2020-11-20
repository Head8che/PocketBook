

package com.example.pocketbook.activity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pocketbook.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


public class ScanActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Button buttonScan = (Button) findViewById(R.id.btnScan);
        buttonScan.setOnClickListener(this);

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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
//        if (result != null){
//            if (result.getContents() != null){
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(result.getContents());
//                builder.setTitle("Scanning Result");
//                builder.setPositiveButton("SCAN AGAIN", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        scanCode();
//                    }
//                }).setNegativeButton("Finished", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//            else{
//                Toast.makeText(this,"NO Results",Toast.LENGTH_LONG).show();
//            }
//        }
//        else{
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//
//    }
}
