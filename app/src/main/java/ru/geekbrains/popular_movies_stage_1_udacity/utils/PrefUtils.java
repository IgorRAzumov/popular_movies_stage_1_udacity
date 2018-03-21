package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.geekbrains.popular_movies_stage_1_udacity.R;


public class PrefUtils {
    public static void writeSortBySpinnerPositionToSharedPref(Context context, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.shared_pref_key_sort_by), position);
        editor.apply();
    }

    public static int readSortBySpinnerPositionFromSharedPref(Context context) {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getInt(context.getString(R.string.shared_pref_key_sort_by),
                context.getResources().getInteger(R.integer.movies_request_param_sort_by_top_rated_index_array));
    }

}
