package com.example.activity;

import android.os.Bundle;

import com.example.MediaPalyerControl;
import com.example.R;
import com.example.views.SurfaceAndProgressView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceAndProgressView surfacePgoress = findViewById(R.id.surface_progress);
        MediaPalyerControl     control      = new MediaPalyerControl(surfacePgoress, this);

    }
}
