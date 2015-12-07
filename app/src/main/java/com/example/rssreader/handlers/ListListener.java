package com.example.rssreader.handlers;

import android.app.Activity;
import android.content.Intent;
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
    private final List<RssItem> listItems;
    private final Activity activity;

    public ListListener(List<RssItem> listItems, Activity activity)
    {
        this.listItems = listItems;
        this.activity = activity;
    }

    // Starting Preview activity with extras in intent on item click
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra("dbIndex", listItems.get(position).getDbIndex());
        activity.startActivity(intent);
    }
}
