package com.example.rssreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.model.RssItem;

import java.util.List;

/**
 * Custom adapter for ListView in Browsing activity
 */
public class BrowsingListAdapter extends BaseAdapter {
    private Activity activity;
    private List<RssItem> data;
    private static LayoutInflater inflater = null;

    public BrowsingListAdapter(Activity activity, List<RssItem> data) {
        this.activity = activity;
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
            view = inflater.inflate(R.layout.list_row, null);
        }
        TextView title = (TextView)view.findViewById(R.id.feed_title);
        TextView description = (TextView)view.findViewById(R.id.description);
        ImageView thumbImage = (ImageView)view.findViewById(R.id.list_image);
        RssItem feedItem = data.get(position);
        title.setText(feedItem.getTitle());
        description.setText(feedItem.getDescription());

        /**
         * TODO : add image from url
         */
//        String uri = "@drawable/k";
//        int imageRes = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
//        Drawable draw = activity.getResources().getDrawable(imageRes);
//        thumbImage.setImageDrawable(draw);
        ///////////

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
