package com.example.rssreader.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.rssreader.BrowsingListAdapter;
import com.example.rssreader.ListListener;
import com.example.rssreader.R;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.model.RssItem;
import com.example.rssreader.model.RssReader;

import java.util.ArrayList;
import java.util.List;

public class Browsing extends AppCompatActivity {
    private Activity localActivity;
    private SharedPreferencesHandler prefHandler;
    private String rssUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefHandler = new SharedPreferencesHandler(this);
        rssUrl = prefHandler.loadRssUrlFromPrefs();

        localActivity = this; // set reference to this activity

        if (isOnline()) {
            GetRssDataTask rssReadingTask = new GetRssDataTask();
            try {
                rssReadingTask.execute(rssUrl);
            } catch(Exception e) {
                // do something
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra("changed", false)) {
                rssUrl = prefHandler.loadRssUrlFromPrefs();
                reloadWithNewUrl();
            }
        }
    }

    private void clearData() {

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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean reloadWithNewUrl() {
        if (isOnline()) {
            GetRssDataTask rssReadingTask = new GetRssDataTask();
            try {
                rssReadingTask.execute(rssUrl);
            } catch (Exception e) {
                return false;
            }
        } else {
            //offline
        }
        return true;
    }

    // checks Internet connection
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // Opens Settings Activity
    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    // On API 15+ network tasks needs to be executed async
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
            if (rssItems != null) {
                List<String> titles = new ArrayList<String>();
                for (int i = 0; i < rssItems.size(); i++) {
                    titles.add(rssItems.get(i).getTitle());
                }
                ListView listView = (ListView)findViewById(R.id.listView);
                BrowsingListAdapter adapter = new BrowsingListAdapter(localActivity, rssItems);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new ListListener(rssItems, localActivity));
            }
        }
    }
}
