package com.example.rssreader.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rssreader.utils.BrowsingListAdapter;
import com.example.rssreader.utils.ListListener;
import com.example.rssreader.R;
import com.example.rssreader.data.DBHandler;
import com.example.rssreader.data.SharedPreferencesHandler;
import com.example.rssreader.model.RssItem;
import com.example.rssreader.model.RssReader;

import java.util.List;

public class Browsing extends AppCompatActivity {
    private Activity localActivity;
    private SharedPreferencesHandler prefHandler;
    private String rssUrl;
    private DBHandler databaseHandler;
    private ListView listView;
    private List<RssItem> rssItems;
    private BrowsingListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        initSwipeRefreshLayout();

        // Firstly, load database and set data to ListView
        initListView();

        //Secondly, trying to get new feed from url
        tryGetNewFeed();
    }

    private void tryGetNewFeed() {
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

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tryGetNewFeed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    // Gets result of SettingActivity and handle reloading if URL was changed
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra("changed", true)) {
                rssUrl = prefHandler.loadRssUrlFromPrefs();
                reloadWithNewUrl();
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
    private void reloadWithNewUrl() {
        databaseHandler.deleteAll();
        rssItems.clear();
        adapter.notifyDataSetChanged();
        tryGetNewFeed();
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
                // if new items pushed to database -> need to update ListView
                updateListView(newItemsCount);
            }
        }
    }

    // Sets adapter to ListView filled with rows from database
    private void initListView() {
        rssItems = databaseHandler.getAllRssItems();
        adapter = new BrowsingListAdapter(localActivity, rssItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListListener(rssItems, localActivity));
        // setting custom OnScrollListener for correct work (SwipeRefreshLayout + ListView)
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
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
