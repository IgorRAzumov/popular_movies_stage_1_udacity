package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableDataMovies;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData.MoviesContract;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.DbMoviesUtils;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MoviesAsyncTaskNetworkLoader extends BaseAsyncTaskLoader<DisplayableDataMovies> {
    private String language;
    private String sortBy;

    public MoviesAsyncTaskNetworkLoader(Context context, Bundle args) {
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
    public DisplayableDataMovies loadInBackground() {
        MoviesResponse moviesResponse = NetworkUtils.getMovies(sortBy, language);
        if (moviesResponse == null) {
            return null;
        }

        List<DisplayableMovie> movies = new ArrayList<>();
        List<Integer> apiIdFavoriteList = new ArrayList<>();
        Cursor cursor = DbMoviesUtils.getAllFavoritesApiId(getContext());
        while (cursor.moveToNext()) {
            apiIdFavoriteList.add(
                    cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID)));
        }
        for (MovieResult movie : moviesResponse.getResults()) {
            int movieApiId = movie.getId();
            movies.add(new DisplayableMovie(movieApiId, movie.getTitle(),
                    movie.getOverview(), movie.getVoteAverage(), movie.getPosterPath(),
                    movie.getReleaseDate(),
                    apiIdFavoriteList.contains(movieApiId)));
        }

        return new DisplayableDataMovies(moviesResponse.getPage(), moviesResponse.getTotalPages(), movies);
    }
}
