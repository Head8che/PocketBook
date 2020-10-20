package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pocketbook.R;

public class MainActivity extends AppCompatActivity {

    Button viewMyBookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewMyBookBtn = findViewById(R.id.viewMyBookBtn);

        viewMyBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMyBookActivity.class);
                startActivity(intent);
            }
        });
    }
}
