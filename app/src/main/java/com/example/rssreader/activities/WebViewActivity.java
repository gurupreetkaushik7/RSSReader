package com.example.rssreader.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.rssreader.R;

public class WebViewActivity extends AppCompatActivity {
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        load();
    }

    private void load() {
        link = getIntent().getStringExtra("link");
        WebView webView = (WebView)findViewById(R.id.webView);
        //WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(link);

    }
}
