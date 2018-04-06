/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.geekbrains.popular_movies_stage_1_udacity.data.databaseData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


public class MoviesContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TASKS, MOVIES);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        assert context != null;
        moviesDbHelper = new MoviesDbHelper(context.getApplicationContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase database = moviesDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES: {
                long id = database.insert(MoviesContract.MovieEntry.TABLE_NAME,
                        null, values);
                if (id > 0) {
                    returnUri =
                            ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIES: {
                cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int moviesDeleted;

        switch (match) {
            case MOVIES: {
                moviesDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
