package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.fragment_btn).setOnClickListener(this);
        findViewById(R.id.activity_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_btn:
                startActivity(new Intent(this,VideoActivity.class));
                 break;
            case R.id.fragment_btn:
                break;
            default:
                 break;
        }
    }
}
