package com.example.rssreader.handlers;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Custom OnScrollListener for correct work (SwipeRefreshLayout + ListView)
 */
public class CustomOnScrollListener implements AbsListView.OnScrollListener{
    private final ListView listView;
    private final SwipeRefreshLayout swipeRefreshLayout;

    public CustomOnScrollListener(ListView listView, SwipeRefreshLayout swipeRefreshLayout) {
        this.listView = listView;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    // Needs to be implemented
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    // Enable SwipeRefreshLayout work only if user see top item of ListView
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        int topRowVerticalPosition =
                (listView == null || listView.getChildCount() == 0) ?
                        0 : listView.getChildAt(0).getTop();
        swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }
}
