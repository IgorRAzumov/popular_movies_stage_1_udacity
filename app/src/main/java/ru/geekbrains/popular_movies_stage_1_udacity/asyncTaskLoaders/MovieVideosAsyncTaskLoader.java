package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieVideosResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MovieVideosAsyncTaskLoader extends BaseAsyncTaskLoader<MovieVideosResponse> {
    private int movieId;
    private String language;

    public MovieVideosAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            movieId = args.getInt(context.getString(R.string.movie_id_bundle_key));
            language = args.getString(context.getString(R.string.language_key));
        }
    }

    @Override
    protected void onStartLoading() {
        if (movieId == 0 || language == null || TextUtils.isEmpty(language)) {
            return;
        }
        startLoad();
    }

    @Override
    public MovieVideosResponse loadInBackground() {
        return NetworkUtils.getVideos(movieId, language);
    }
}
