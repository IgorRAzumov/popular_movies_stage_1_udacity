package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData.MoviesContract.MovieEntry;


public class DbMoviesUtils {

    private final static String[] SELECTED_ALL_COLUMNS = {MovieEntry.COLUMN_MOVIE_API_ID,
            MovieEntry.COLUMN_MOVIE_NAME,
            MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieEntry.COLUMN_MOVIE_POSTER,
            MovieEntry.COLUMN_MOVIE_RATING,
            MovieEntry.COLUMN_MOVIE_RELEASE_DATE};


    public static void addToFavorites(Context context, DisplayableMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_API_ID, movie.getMovieApiId());
        contentValues.put(MovieEntry.COLUMN_MOVIE_NAME, movie.getMovieName());
        contentValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getMovieOverview());
        contentValues.put(MovieEntry.COLUMN_MOVIE_POSTER, movie.getMoviePosterPath());
        contentValues.put(MovieEntry.COLUMN_MOVIE_RATING, movie.getMovieRating());
        contentValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getMovieReleaseDate());

        context.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, contentValues);
    }

    public static void deleteFromFavoriteByApiId(Context context, int movieApiId) {
        context.getContentResolver().delete(MovieEntry.CONTENT_URI,
                context.getString(R.string.sql_selection_movie_api_id),
                new String[]{String.valueOf(movieApiId)});
    }

    public static Cursor getAllFavorites(Context context) {
        return context.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                SELECTED_ALL_COLUMNS,
                null,
                null,
                null);
    }

    public static Cursor getAllFavoritesApiId(Context context) {
        return context.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[]{MovieEntry.COLUMN_MOVIE_API_ID},
                null,
                null,
                null);
    }
}
