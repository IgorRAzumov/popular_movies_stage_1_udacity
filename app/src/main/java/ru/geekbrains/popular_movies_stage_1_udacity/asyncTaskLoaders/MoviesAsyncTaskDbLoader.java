package ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableDataMovies;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.DbMoviesUtils;

import static ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData.MoviesContract.MovieEntry;

public class MoviesAsyncTaskDbLoader extends BaseAsyncTaskLoader<DisplayableDataMovies> {

    public MoviesAsyncTaskDbLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        startLoad();
    }

    @Override
    public DisplayableDataMovies loadInBackground() {
        Cursor cursor = DbMoviesUtils.getAllFavorites(getContext().getApplicationContext());

        if (cursor != null) {
            List<DisplayableMovie> results = new ArrayList<>();
            while (cursor.moveToNext()) {
                results.add(new DisplayableMovie(
                        cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_API_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_NAME)),
                        cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW)),
                        cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING)),
                        cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER)),
                        cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE)),
                        true));
            }
            cursor.close();
            return new DisplayableDataMovies(results);
        }
        return null;
    }
}
