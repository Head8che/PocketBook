package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.R;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity
    implements ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
        private int mCameraId = -1;

        @Override
        protected void onCreate(Bundle state) {
            super.onCreate(state);
            setContentView(R.layout.activity_scan);
            ViewGroup contentFrame = findViewById(R.id.content_frame);
            mScannerView = new ZXingScannerView(this);
            contentFrame.addView(mScannerView);
            Log.d("DEBUG:", "Starting SCAN Activity");
        }

        @Override
        public void onResume() {
            super.onResume();
            mScannerView.setResultHandler(this);
            mScannerView.startCamera(mCameraId);
            //to set flash
//        mScannerView.setFlash(true);
            //to set autoFocus
//        mScannerView.setAutoFocus(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mScannerView.stopCamera();           // Stop camera on pause
        }

        @Override
        public void handleResult(Result rawResult) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                Toast.makeText(this, "Scan Completed \n" + rawResult.getText() + "", Toast.LENGTH_SHORT)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}