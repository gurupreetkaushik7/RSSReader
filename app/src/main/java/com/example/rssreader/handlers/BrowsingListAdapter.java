package com.example.rssreader.handlers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.RssItem;

import java.util.List;

/**
 * Custom adapter for ListView in Browsing activity
 */
public class BrowsingListAdapter extends BaseAdapter {
    private final List<RssItem> data;
    private static LayoutInflater inflater = null;
    private final static int SHORT_DESCRIPTION_LENGTH = 50;

    public BrowsingListAdapter(Activity activity, List<RssItem> data) {
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.list_row, parent, false);
        }
        TextView title = (TextView)view.findViewById(R.id.feed_title);
        TextView description = (TextView)view.findViewById(R.id.description);
        ImageView thumbImage = (ImageView)view.findViewById(R.id.list_image);
        RssItem feedItem = data.get(position);
        title.setText(feedItem.getTitle());
        description.setText(feedItem.getDescription());
        thumbImage.setImageBitmap(feedItem.getImage());

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RssItem getItem(int position) {
        return data.get(position);
    }
}
