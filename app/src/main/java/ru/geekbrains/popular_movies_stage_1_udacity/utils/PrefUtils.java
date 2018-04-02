package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.geekbrains.popular_movies_stage_1_udacity.R;


public class PrefUtils {
    public static void writeSortByTypeToSharedPref(Context context, String sortBy) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.shared_pref_key_sort_by), sortBy);
        editor.apply();
    }

    public static String readSortByTypeFromSharedPref(Context context) {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getString(context.getString(R.string.shared_pref_key_sort_by),
                context.getString(R.string.movies_request_param_sort_by_top_rated));
    }

    public static void writeBotNavSelectedItemToSharedPref(Context context, int id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.shared_pref_key_bot_nav_selected_id), id);
        editor.apply();
    }

    public static int readBotNavSelectedItemSharedPref(Context context) {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getInt(context.getString(R.string.shared_pref_key_bot_nav_selected_id),
                R.id.menu_bt_nav_popular);
    }

}
