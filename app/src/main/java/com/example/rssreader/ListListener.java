package com.example.rssreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.rssreader.model.RssItem;

import java.util.List;

/**
 * Created by Никита on 28.11.2015.
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(listItems.get(position).getLink()));
        activity.startActivity(intent);
    }
}
