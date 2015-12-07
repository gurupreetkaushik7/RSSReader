package com.example.rssreader.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.rssreader.R;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.handlers.SettingsApplyClickListener;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferencesHandler prefHandler = new SharedPreferencesHandler(this);
        Button applyButton = (Button)findViewById(R.id.buttonApply);
        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setText(prefHandler.loadRssUrlFromPrefs());

        SettingsApplyClickListener clickListener = new SettingsApplyClickListener(this, editText);
        applyButton.setOnClickListener(clickListener);
    }
}
