package com.fourbeams.testtasktinkoff;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Activity for displaying particular news.
 * </br> Implements LoaderCallbacks to obtain data from {@link TinkoffNewsContentProvider}.
 *
 */
public class ItemContentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    private static final int NEWS_ITEM_LOADER = 1;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 finish();
             }
         });
        itemId = getIntent().getStringExtra("itemId");
    }

    @Override
    protected void onPause (){
        super.onPause();
        getLoaderManager().destroyLoader(NEWS_ITEM_LOADER);
    }

    @Override
    protected void onResume (){
        super.onResume();
        getLoaderManager().initLoader(NEWS_ITEM_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case (NEWS_ITEM_LOADER):
                return new CursorLoader(
                        this,                                                       // context
                        TinkoffNewsContentProvider.CONTENT_URI_NEWS_ITEM_CONTENT,   // dataUri,
                        TinkoffNewsContentProvider.NEWS_ITEM_CONTENT_PROJECTION,    // projection
                        itemId,                                                     // selection
                        null,                                                       // selectionArgs
                        null                                                        // sort ordering
                );
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        Cursor cursor = (Cursor) o;
        if (cursor.moveToLast()){
            final String itemContentText = cursor.getString(cursor.getColumnIndex(TinkoffNewsContentProvider.ITEM_CONTENT));
            final TextView itemContentTextView = (TextView)findViewById(R.id.news_item_content);
            itemContentTextView.post(new Runnable() {
                public void run() {
                    itemContentTextView.setText(itemContentText);
                }
            });
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
