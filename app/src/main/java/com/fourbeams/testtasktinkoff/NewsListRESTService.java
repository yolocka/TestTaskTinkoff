package com.fourbeams.testtasktinkoff;

import com.fourbeams.testtasktinkoff.NewsListPOJO.News;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface for getting the full list of news from server
 *
 */
public interface NewsListRESTService {
    @GET("news")
    Call<News> getJSON();
}
