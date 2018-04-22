package ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {
    public static final String AUTHORITY = "ru.geekbrains.popular_movies_stage_1_udacity";
    public static final String PATH_TASKS = "movies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_API_ID = "movieApiId";

        public static final String COLUMN_MOVIE_NAME = "movieName";

        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";

        public static final String COLUMN_MOVIE_POSTER = "moviePoster";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";

        public static final String COLUMN_MOVIE_RATING = "movieRating";

    }


}
