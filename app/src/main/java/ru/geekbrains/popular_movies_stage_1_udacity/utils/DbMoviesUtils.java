package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData.MoviesContract;


public class DbMoviesUtils {

    private final static String[] SELECTED_ALL_COLUMNS = {MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_NAME,
            MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RATING,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE};


    public static boolean addToFavorites(Context context,DisplayableMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID, movie.getMovieApiId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_NAME, movie.getMovieName());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getMovieOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getMoviePosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING, movie.getMovieRating());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getMovieReleaseDate());

        Uri uri = context.getContentResolver()
                .insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        return (uri != null);
    }

    public static boolean deleteFromFavorite(Context context, int movieApiId) {
        int isDeleted = context.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                context.getString(R.string.sql_selection_movie_api_id),
                new String[]{String.valueOf(movieApiId)});
        return (isDeleted != 0);
    }

    public static Cursor getAllFavorites(Context context) {
        return context.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                SELECTED_ALL_COLUMNS,
                null,
                null,
                null);
    }

    public static Cursor getAllFavoritesApiId(Context context) {
        return context.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID},
                null,
                null,
                null);
    }
}
