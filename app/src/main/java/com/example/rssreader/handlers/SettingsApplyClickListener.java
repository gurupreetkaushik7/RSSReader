package com.example.rssreader.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rssreader.R;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.model.RssReader;

/**
 * Handle OnClick on Apply button in Settings activity
 */
public class SettingsApplyClickListener implements View.OnClickListener {
    private final Activity parentActivity;
    private final SharedPreferencesHandler prefHandler;
    private final EditText editText;
    private String userInput;

    public SettingsApplyClickListener(Activity parentActivity, EditText editText) {
        this.parentActivity = parentActivity;
        this.prefHandler = new SharedPreferencesHandler(parentActivity);
        this.editText = editText;
    }

    // Launch async task that checks that user input is valid Rss-feed
    public void onClick(View v) {
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

    // Shows popup message
    private void showToast(String toastText) {
        Toast toast =  Toast.makeText(parentActivity, toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    // Checks that device have internet connection
    private boolean isOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager)parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // On API 15+ network tasks needs to be executed async
    private class ValidationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                RssReader rssReader = new RssReader(urls[0], parentActivity);
                return rssReader.isValid();
            } catch (Exception e) {
                Log.e("Validator : ", e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValidRss) {
            if (!isValidRss){
                showToast(parentActivity.getString(R.string.invalid_url));
                return;
            } else if (!prefHandler.saveUrlToPrefs(userInput)) {
                showToast(parentActivity.getString(R.string.saving_failure));
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("changed", true);
            parentActivity.setResult(Activity.RESULT_OK, intent);
            parentActivity.finish();
        }
    }
}
