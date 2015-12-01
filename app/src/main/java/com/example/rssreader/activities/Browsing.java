package com.example.rssreader.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rssreader.ListListener;
import com.example.rssreader.R;
import com.example.rssreader.model.RssItem;
import com.example.rssreader.model.RssReader;

import java.util.List;

public class Browsing extends AppCompatActivity {

    // A reference to the local object
    private Browsing localActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        localActivity = this; // setting reference to this activity

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {  // checking Internet connection
            GetRssDataTask rssReadingTask = new GetRssDataTask();
            rssReadingTask.execute("http://ria.ru/export/rss2/economy/index.xml");
        } else {
            // no inet connection
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browsing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens Settings Activity
     */
    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private class GetRssDataTask extends AsyncTask<String, Void, List<RssItem>> {
        @Override
        protected List<RssItem> doInBackground(String... urls) {
            try {
                RssReader rssReader = new RssReader(urls[0]);
                return rssReader.getItems();
            } catch (Exception e) {
                Log.e("RSSReader", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RssItem> rssItems) {
            ListView listView = (ListView)findViewById(R.id.listView);
            ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(localActivity,
                    android.R.layout.simple_list_item_1, rssItems);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new ListListener(rssItems, localActivity));
        }
    }

}
