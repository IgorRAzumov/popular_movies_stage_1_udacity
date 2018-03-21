package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;


public class Utils {
    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.getDisplayMetrics()));
    }
}
