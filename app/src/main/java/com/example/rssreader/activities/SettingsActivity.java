package com.example.rssreader.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.rssreader.R;
import com.example.rssreader.data.SharedPreferencesHandler;

public class SettingsActivity extends AppCompatActivity {
    private EditText editText;
    private SharedPreferencesHandler prefHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefHandler = new SharedPreferencesHandler(this);
        Button applyButton = (Button)findViewById(R.id.buttonApply);
        editText = (EditText)findViewById(R.id.editText);
        editText.setText(prefHandler.loadRssUrlFromPrefs());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prefHandler.saveUrlToPrefs(editText.getText().toString())) {
                    // do something on cant save ;)
                }
            }
        };
        applyButton.setOnClickListener(onClickListener);
    }
}
