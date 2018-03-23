package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MoviesAsyncTaskLoader extends BaseAsyncTaskLoader<MoviesResponse> {
    private String language;
    private String sortBy;

    public MoviesAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            language = args.getString(context.getString(R.string.language_key));
            sortBy = args.getString(context.getString(R.string.sort_by_movies_bundle_key));
        }
    }

    @Override
    protected void onStartLoading() {
        if (sortBy == null || TextUtils.isEmpty(sortBy) || language == null
                || TextUtils.isEmpty(language)) {
            return;
        }
        startLoad();
    }

    @Override
    public MoviesResponse loadInBackground() {
        return NetworkUtils.getMovies(sortBy, language);
    }
}
