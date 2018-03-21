package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MoviesAsyncTask extends AsyncTaskLoader<MoviesResponse> {
    private MoviesResponse moviesResponse;
    private String language;
    private String sortBy;

    public MoviesAsyncTask(Context context, Bundle args) {
        super(context);
        if (args != null) {
            language = args.getString(context.getString(R.string.language_bundle_key));
            sortBy = args.getString(context.getString(R.string.sort_by_movies_bundle_key));
        }
    }

    @Override
    protected void onStartLoading() {
        if (sortBy == null || TextUtils.isEmpty(sortBy)) {
            return;
        }

        if (moviesResponse != null && moviesResponse.getResults().size() != 0) {
            deliverResult(moviesResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    public MoviesResponse loadInBackground() {
        return NetworkUtils.getMovies(sortBy, language, getContext());
    }

    @Override
    public void deliverResult(MoviesResponse data) {
        moviesResponse = data;
        super.deliverResult(data);
    }
}
