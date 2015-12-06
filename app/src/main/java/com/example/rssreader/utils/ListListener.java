package com.example.rssreader.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.rssreader.activities.PreviewActivity;
import com.example.rssreader.model.RssItem;

import java.util.List;

/**
 * Handle taps on list item
 */
public class ListListener implements OnItemClickListener
{
    List<RssItem> listItems;
    Activity activity;

    public ListListener(List<RssItem> listItems, Activity activity)
    {
        this.listItems = listItems;
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra("dbIndex", listItems.get(position).getDbIndex());
        activity.startActivity(intent);
    }
}
