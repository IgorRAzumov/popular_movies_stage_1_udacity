package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.ReviewsResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MovieReviewsAsyncTaskLoader extends BaseAsyncTaskLoader<ReviewsResponse> {
    private int movieId;
    private String language;

    public MovieReviewsAsyncTaskLoader(Context context, Bundle args) {
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
    public ReviewsResponse loadInBackground() {
        return NetworkUtils.getReviews(movieId, language);

    }
}
