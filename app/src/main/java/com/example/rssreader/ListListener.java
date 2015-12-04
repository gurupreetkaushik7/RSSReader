package com.example.rssreader;

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
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.setData(Uri.parse(listItems.get(position).getLink()));
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra("title", listItems.get(position).getTitle());
        intent.putExtra("author", listItems.get(position).getAuthor());
        intent.putExtra("description", listItems.get(position).getDescription());
        intent.putExtra("link", listItems.get(position).getLink());
        intent.putExtra("pubDate", listItems.get(position).getPubDate());
        activity.startActivity(intent);
    }
}
