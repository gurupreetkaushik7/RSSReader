package com.example.rssreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.rssreader.R;

public class PreviewActivity extends AppCompatActivity {
    private String title;
    private String author;
    private String description;
    private String link;
    private String pubDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        loadElements();
    }

    /**
     * Loads from intent and sets data in elements
     * TODO : add image loading
     * TODO : handle parsing date by locale
     */
    private void loadElements() {
        final Activity activity = this;
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        description = getIntent().getStringExtra("description");
        link = getIntent().getStringExtra("link");
        pubDate = getIntent().getStringExtra("pubDate");
        TextView titleTextView = (TextView)findViewById(R.id.preview_title);
        TextView authorTextView = (TextView)findViewById(R.id.preview_author);
        TextView descriptionTextView = (TextView)findViewById(R.id.preview_description);
        TextView pubDateTextView = (TextView)findViewById(R.id.preview_dateTime);
        titleTextView.setText(title);
        authorTextView.setText(author);
        descriptionTextView.setText(description);
        pubDateTextView.setText(pubDate);

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(link));
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });
    }

}
