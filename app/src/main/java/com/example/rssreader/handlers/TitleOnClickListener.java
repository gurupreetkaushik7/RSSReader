package com.example.rssreader.handlers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.rssreader.activities.WebViewActivity;

/**
 * Handle click on Title on Preview activity
 */
public class TitleOnClickListener implements View.OnClickListener{
    private final Activity parentActivity;
    private final String link;
    public TitleOnClickListener(Activity parentActivity, String link) {
        this.parentActivity = parentActivity;
        this.link = link;
    }

    // Start WebView activity with extras in intent
    public void onClick(View v) {
        Intent intent = new Intent(parentActivity, WebViewActivity.class);
        intent.putExtra("link", link);
        parentActivity.startActivity(intent);
    }
}
