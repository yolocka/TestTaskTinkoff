package com.fourbeams.testtasktinkoff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;

import com.fourbeams.testtasktinkoff.NewsItemPOJO.NewsItem;
import com.fourbeams.testtasktinkoff.NewsListPOJO.News;
import com.fourbeams.testtasktinkoff.NewsListPOJO.Payload;

import java.io.IOException;
import retrofit2.Call;

/**
 * Starts processor to obtain and process data from server through REST Service.
 * </br>Obtains list of news from server, for each news checks its presence at {@link TinkoffNewsContentProvider}
 * </br>If there is NO particular news at content provider, processor saves it
 * </br> by calling {@link #putItemInNewsList(Payload)} and {@link #putItemContent(Payload, com.fourbeams.testtasktinkoff.NewsItemPOJO.Payload)}.
 * </br>In case API is not available the IOException  is catched and new RuntimeException() re-throws.
 */
public class Processor {

    private Context context;

    Processor(Context context) {
        this.context = context;
    }

    void startGetProcessor(){
        try {
            getNewsList();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void getNewsList() throws IOException {
        // Create a REST adapters which points API endpoint
        NewsListRESTService newsListRESTService = RESTServiceGenerator.createService(NewsListRESTService.class);
        NewsItemsRESTService newsItemsRESTService = RESTServiceGenerator.createService(NewsItemsRESTService.class);
        // Fetch a list of news
        final Call<News> call = newsListRESTService.getJSON();
        // call.execute() method throws exception if API not available or the timeout values are reached
        News newsList = call.execute().body();
        Cursor cursor;
        for (Object payload : newsList.getPayload()) {
            Payload newsItem = (Payload) payload;
            Call<NewsItem> callNewsItem = newsItemsRESTService.getJSON(newsItem.getId());
            NewsItem newsContentItem = callNewsItem.execute().body();
            com.fourbeams.testtasktinkoff.NewsItemPOJO.Payload payloadNewsItem = newsContentItem.getPayload();
            cursor = context.getContentResolver().query(TinkoffNewsContentProvider.CONTENT_URI_NEWS_LIST_ITEM, TinkoffNewsContentProvider.NEWS_LIST_PROJECTION, newsItem.getId(), null, null);
            if (!cursor.moveToLast()) {
                putItemInNewsList(newsItem);
                putItemContent(newsItem, payloadNewsItem);
            }
            cursor.close();
        }
    }

    private void putItemInNewsList(Payload newsItem) {
        ContentValues contentValuesNewsList = new ContentValues();
        contentValuesNewsList.put(TinkoffNewsContentProvider.ITEM_ID, newsItem.getId());
        String textHtml = newsItem.getText();
        String text = Html.fromHtml(textHtml).toString();
        contentValuesNewsList.put(TinkoffNewsContentProvider.TEXT, text);
        contentValuesNewsList.put(TinkoffNewsContentProvider.PUBLICATION_DATE, newsItem.getPublicationDate().getMilliseconds());
        context.getContentResolver().insert(TinkoffNewsContentProvider.CONTENT_URI_NEWS_LIST, contentValuesNewsList);
        contentValuesNewsList.clear();
    }

    private void putItemContent(Payload newsItem, com.fourbeams.testtasktinkoff.NewsItemPOJO.Payload payloadNewsItem) {
        ContentValues contentValuesNewsItem = new ContentValues();
        contentValuesNewsItem.put(TinkoffNewsContentProvider.ITEM_ID, newsItem.getId());
        String contHtml = payloadNewsItem.getContent();
        String cont = Html.fromHtml(contHtml).toString();
        contentValuesNewsItem.put(TinkoffNewsContentProvider.ITEM_CONTENT, cont);
        context.getContentResolver().insert(TinkoffNewsContentProvider.CONTENT_URI_NEWS_ITEM_CONTENT, contentValuesNewsItem);
        contentValuesNewsItem.clear();
    }
}
