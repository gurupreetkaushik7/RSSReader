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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rssreader.BrowsingListAdapter;
import com.example.rssreader.ListListener;
import com.example.rssreader.R;
import com.example.rssreader.data.DBHandler;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.model.RssItem;
import com.example.rssreader.model.RssReader;

import java.util.ArrayList;
import java.util.List;

public class Browsing extends AppCompatActivity {
    private Activity localActivity;
    private SharedPreferencesHandler prefHandler;
    private String rssUrl;
    private DBHandler databaseHandler;
    private ListView listView;
    private List<RssItem> rssItems;
    private BrowsingListAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);
        localActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView)findViewById(R.id.listView);
        prefHandler = new SharedPreferencesHandler(this);
        rssUrl = prefHandler.loadRssUrlFromPrefs();
        databaseHandler = new DBHandler(this);

        // Firstly, load database and set data to ListView
        initListView();

        //Secondly, trying to get new feed from url
        if (isOnline()) {
            GetRssDataTask rssReadingTask = new GetRssDataTask();
            try {
                rssReadingTask.execute(rssUrl);
            } catch(Exception e) {
                showToast("Exception while trying to read feed from url");
                Log.e("RSSReader", e.getMessage());
            }
        } else {
            showToast("You are offline");
        }
    }



    @Override
    // Gets result of SettingActivity and handle reloading if URL was changed
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra("changed", false)) {
                rssUrl = prefHandler.loadRssUrlFromPrefs();
                if (!reloadWithNewUrl()) {
                    showToast("Failure while reloading");
                }
            }
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
        }

        return super.onOptionsItemSelected(item);
    }

    // Reloads data with data from new URL
    private boolean reloadWithNewUrl() {
        databaseHandler.deleteAll();
        if (isOnline()) {
            GetRssDataTask rssReadingTask = new GetRssDataTask();
            try {
                rssReadingTask.execute(rssUrl);
                initListView();
            } catch (Exception e) {
                return false;
            }
        } else {
            showToast(getString(R.string.offline_msg));
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
    private class GetRssDataTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... urls) {
            try {
                RssReader rssReader = new RssReader(urls[0], localActivity);
                return rssReader.updateDB();
            } catch (Exception e) {
                Log.e("RSSReader", e.getMessage());
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer newItemsCount) {
            if (newItemsCount > 0) {
                showToast(getString(R.string.found_items) + " " + newItemsCount.toString() + " "
                        + getString(R.string.new_items));
                updateListView(newItemsCount);
                // TODO: use some marker to say 'pulltorefresh' that new items found and exec updateListView in pull to refresh handler
            }
        }
    }

    // Sets adapter to ListView filled with rows from database
    private void initListView() {
        rssItems = databaseHandler.getAllRssItems();
        adapter = new BrowsingListAdapter(localActivity, rssItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListListener(rssItems, localActivity));
    }

    private void showToast(String toastText) {
        Toast toast =  Toast.makeText(Browsing.this, toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void updateListView(int newItemsCount) {
        int itemsInDBCount = databaseHandler.length();
        for (int i = 1; i <= itemsInDBCount; i++) {
            rssItems.add(0, databaseHandler.getRssItem(i));
        }
        adapter.notifyDataSetChanged();
    }
}
