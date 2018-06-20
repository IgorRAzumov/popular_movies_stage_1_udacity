package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import ru.geekbrains.popular_movies_stage_1_udacity.R;


public class Utils {
    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.getDisplayMetrics()));
    }

    public static int calculateNoOfColumns(Context context, int scalingFactor) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int minNoOfColumns = context.getResources().getInteger(R.integer.min_count_grid_span);
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < minNoOfColumns) {
            noOfColumns = minNoOfColumns;
        }
        return noOfColumns;
    }
}
