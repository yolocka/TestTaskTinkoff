package com.fourbeams.testtasktinkoff;

import com.fourbeams.testtasktinkoff.NewsItemPOJO.NewsItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for getting content of particular news for the given id
 *
 */
public interface NewsItemsRESTService {
    @GET("news_content")
    Call<NewsItem> getJSON(@Query("id") String id);
}
