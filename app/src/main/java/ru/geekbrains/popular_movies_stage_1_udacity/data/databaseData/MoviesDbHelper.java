package ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData.MoviesContract.MovieEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WAITLIST_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieEntry.COLUMN_MOVIE_API_ID + " INTEGER NOT NULL UNIQUE, " +
                        MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_RATING + " DOUBLE, " +
                        MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +


                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_API_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}