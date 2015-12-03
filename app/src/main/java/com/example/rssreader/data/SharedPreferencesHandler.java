package com.example.rssreader.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.rssreader.R;

/**
 * Shared prefs handler for save and load prefs
 */
public class SharedPreferencesHandler {
    private Activity localActivity;
    private String urlInPrefName;
    private SharedPreferences preferences;

    public SharedPreferencesHandler(Activity activity) {
        this.localActivity = activity;
        urlInPrefName = localActivity.getString(R.string.rss_url_pref);
        preferences = PreferenceManager.getDefaultSharedPreferences(localActivity);
    }

    public Boolean saveUrlToPrefs(String urlString) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(urlInPrefName, urlString);
        return editor.commit();
    }

    public String loadRssUrlFromPrefs()
    {
        return preferences.getString(urlInPrefName, localActivity.getString(R.string.standard_rss));
    }
}
