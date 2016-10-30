package com.fourbeams.testtasktinkoff;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Activity for displaying scrollable list of news headers.
 * </br> Implements LoaderCallbacks to obtain data from {@link TinkoffNewsContentProvider}.
 * </br> In case the app is first time running or swipe-to-refresh where performed, data loading from
 * </br> server initiates by  calling {@link #loadDataFromServer()}
 *
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks {

    private static final int NEWS_LIST_LOADER = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SimpleCursorAdapter newsSimpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        // The columns we want to display in the result
        String[] from = new String[] {TinkoffNewsContentProvider.TEXT};
        // The Corresponding layout elements where we want the columns to go
        int[] to = new int[] {android.R.id.text1};
        // Create a simple cursor adapter for the definitions and apply them to the ListView
        newsSimpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);

        listView = (ListView) findViewById(R.id.news_list);
        listView.setAdapter(newsSimpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String itemId = cursor.getString(cursor.getColumnIndexOrThrow(TinkoffNewsContentProvider.ITEM_ID));
                Intent intent = new Intent(MainActivity.this, ItemContentActivity.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(NEWS_LIST_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case (NEWS_LIST_LOADER):
                return new CursorLoader(
                        this,                                               // context
                        TinkoffNewsContentProvider.CONTENT_URI_NEWS_LIST,   // dataUri,
                        TinkoffNewsContentProvider.NEWS_LIST_PROJECTION,    // projection
                        null,                                               // selection
                        null,                                               // selectionArgs
                        TinkoffNewsContentProvider.PUBLICATION_DATE         // sort ordering
                );
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        Cursor cursor = (Cursor) o;
        if (cursor.moveToLast()){
            newsSimpleCursorAdapter.swapCursor(cursor);
        } else loadDataFromServer();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        newsSimpleCursorAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        loadDataFromServer();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadDataFromServer(){
        ServiceFacade.getInstance().runService(ServiceFacade.task.GET_NEW_NEWS_FROM_SERVER, getApplicationContext());
    }
}
