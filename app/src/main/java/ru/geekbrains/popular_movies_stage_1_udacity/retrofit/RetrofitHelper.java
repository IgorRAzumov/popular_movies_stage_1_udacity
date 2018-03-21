package ru.geekbrains.popular_movies_stage_1_udacity.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitHelper {
    private final static RetrofitHelper RETROFIT_HELPER = new RetrofitHelper();
    private static final String POPULAR_MOVIES_SEARCH_URL = "https://api.themoviedb.org/3/";
    private static IPopularMoviesApi iPopularMoviesApi;
    private RetrofitHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(POPULAR_MOVIES_SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClientBuilderWithLogging().build())
                .build();

        iPopularMoviesApi = retrofit.create(IPopularMoviesApi.class);
    }

    public static RetrofitHelper getRetrofitHelper() {
        return RETROFIT_HELPER;
    }

    private OkHttpClient.Builder getHttpClientBuilderWithLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return httpClient;
    }

    public IPopularMoviesApi getIPopularMoviesApi() {
        return iPopularMoviesApi;
    }
}
