package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.geekbrains.popular_movies_stage_1_udacity.R;


public class PrefUtils {

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
