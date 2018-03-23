package ru.geekbrains.popular_movies_stage_1_udacity.utils;


import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import retrofit2.Response;
import ru.geekbrains.popular_movies_stage_1_udacity.BuildConfig;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.ReviewsResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieVideosResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.retrofit.RetrofitHelper;

public class NetworkUtils {
    private static final String API_KEY = BuildConfig.API_KEY;

    public static MoviesResponse getMovies(String sortBy, String language) {
        MoviesResponse moviesResponse = null;
        try {
            Response<MoviesResponse> response = RetrofitHelper.getRetrofitHelper()
                    .getIPopularMoviesApi()
                    .getMovies(sortBy, API_KEY, language)
                    .execute();

            if (response.isSuccessful()) {
                moviesResponse = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moviesResponse;
    }

    public static void LoadPosterImage(Context context, ImageView imageView, String posterUrl) {
        Picasso
                .with(context)
                .load(posterUrl)
                .into(imageView);
    }

    public static void LoadPosterImageInTarget(Context context, String posterUrl, Target target) {
        Picasso
                .with(context)
                .load(posterUrl)
                .into(target);

    }

    public static MovieVideosResponse getVideos(int movieId, String language) {
        MovieVideosResponse movieVideosResponse = null;
        try {
            Response<MovieVideosResponse> response = RetrofitHelper.getRetrofitHelper()
                    .getIPopularMoviesApi()
                    .getVideos(movieId,  API_KEY, language)
                    .execute();

            if (response.isSuccessful()) {
                movieVideosResponse = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieVideosResponse;
    }

    public static ReviewsResponse getReviews(int movieId, String language) {
        ReviewsResponse reviewsResponse = null;
        try {
            Response<ReviewsResponse> response = RetrofitHelper.getRetrofitHelper()
                    .getIPopularMoviesApi()
                    .getReviews(movieId, API_KEY, language)
                    .execute();

            if (response.isSuccessful()) {
                reviewsResponse = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviewsResponse;
    }


}
