package com.example.rssreader.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.data.DBHandler;
import com.example.rssreader.handlers.TitleOnClickListener;
import com.example.rssreader.model.RssItem;

import java.util.Date;

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
        pubDateTextView.setText(getLocalizedDateTime(item.getPubDate()));
        imageView.setImageBitmap(item.getImage());

        TitleOnClickListener onClickListener = new TitleOnClickListener(this, item.getLink());
        titleTextView.setOnClickListener(onClickListener);
    }

    // Returns date and time formatted by locale settings
    private String getLocalizedDateTime(String oldDateTime) {
        Date date = new Date(oldDateTime);
        return DateUtils.formatDateTime(this, date.getTime(),
                DateUtils.FORMAT_SHOW_DATE) + " " + getString(R.string.at_time) + " " +
                DateUtils.formatDateTime(this, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
    }

}
