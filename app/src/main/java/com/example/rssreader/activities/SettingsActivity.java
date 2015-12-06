package com.example.rssreader.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rssreader.R;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.model.RssReader;

public class SettingsActivity extends AppCompatActivity {
    private EditText editText;
    private SharedPreferencesHandler prefHandler;
    private Activity localActivity;
    private String userInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefHandler = new SharedPreferencesHandler(this);
        Button applyButton = (Button)findViewById(R.id.buttonApply);
        editText = (EditText)findViewById(R.id.editText);
        editText.setText(prefHandler.loadRssUrlFromPrefs());
        localActivity = this;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // string changed?
                userInput = editText.getText().toString();
                if (!userInput.equals(prefHandler.loadRssUrlFromPrefs())) {
                    if (isOnline()) {
                        ValidationTask validationTask = new ValidationTask();
                        validationTask.execute(userInput);
                    } else {
                        showToast("You are offline");
                    }
                }
            }
        };
        applyButton.setOnClickListener(onClickListener);
    }

    private Boolean containsRssFeed(String url) {
        RssReader reader = new RssReader(url, localActivity);
        Boolean result = false;
        try {
            result = reader.isValid();
        } catch (Exception e){
            Log.e("Settings", e.getMessage());
        }
        return result;
    }

    private void showToast(String toastText) {
        Toast toast =  Toast.makeText(this, toastText, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // On API 15+ network tasks needs to be executed async
    private class ValidationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                RssReader rssReader = new RssReader(urls[0], localActivity);
                return rssReader.isValid();
            } catch (Exception e) {
                Log.e("Validator : ", e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValidRss) {
            if (!isValidRss){
                showToast(getString(R.string.invalid_url));
                return;
            } else if (!prefHandler.saveUrlToPrefs(userInput)) {
                showToast(getString(R.string.saving_failure));
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("changed", true);
            localActivity.setResult(RESULT_OK, intent);
            localActivity.finish();
        }
    }
}
