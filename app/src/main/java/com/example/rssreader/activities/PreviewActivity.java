package com.example.rssreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.data.DBHandler;
import com.example.rssreader.model.RssItem;

import java.text.SimpleDateFormat;

public class PreviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        loadElements();
    }

    // Gets SQLite db row (via index) from intent and attach data to activity elements
    private void loadElements() {
        DBHandler databaseHandler = new DBHandler(this);
        final Activity activity = this;
        int dbIndex = getIntent().getIntExtra("dbIndex", 1);
        final RssItem item = databaseHandler.getRssItem(dbIndex);

        TextView titleTextView = (TextView)findViewById(R.id.preview_title);
        TextView authorTextView = (TextView)findViewById(R.id.preview_author);
        TextView descriptionTextView = (TextView)findViewById(R.id.preview_description);
        TextView pubDateTextView = (TextView)findViewById(R.id.preview_dateTime);
        ImageView imageView = (ImageView)findViewById(R.id.preview_image);

        titleTextView.setText(item.getTitle());
        authorTextView.setText(item.getAuthor());
        descriptionTextView.setText(item.getDescription());
        pubDateTextView.setText(item.getPubDate());
        imageView.setImageBitmap(item.getImage());

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("link", item.getLink());
                startActivity(intent);
            }
        });
    }

}
