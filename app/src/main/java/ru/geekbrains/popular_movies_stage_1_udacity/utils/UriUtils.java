package ru.geekbrains.popular_movies_stage_1_udacity.utils;

import android.net.Uri;

public class UriUtils {
    private static final String WATCH_VIDEO_URL = "https://www.youtube.com/watch";
    private static final String WATCH_VIDEO_ID_VIDEO_PARAM = "v";
    private static final String VIDEO_THUMBNAIL_BASE_URL = "http://img.youtube.com/vi";
    private static final String VIDEO_THUMBNAIL_FIRST_FILE_PATH = "/0.jpg";
    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String POSTER_IMAGE_BIG_PATH = "w342";
    private static final String POSTER_IMAGE_BASE_PATH = "w185";

    public static Uri getVideoUri(String videoId) {
        return Uri
                .parse(WATCH_VIDEO_URL)
                .buildUpon()
                .appendQueryParameter(WATCH_VIDEO_ID_VIDEO_PARAM, videoId)
                .build();
    }

    public static Uri getPosterImageUri(String posterPath) {
        return Uri.parse(POSTER_IMAGE_BASE_URL)
                .buildUpon()
                .appendPath(POSTER_IMAGE_BASE_PATH)
                .appendEncodedPath(posterPath)
                .build();
    }

    public static Uri getBigPosterImageUri(String posterPath) {
        return Uri.parse(POSTER_IMAGE_BASE_URL)
                .buildUpon()
                .appendPath(POSTER_IMAGE_BIG_PATH)
                .appendEncodedPath(posterPath)
                .build();
    }

    public static Uri getYouTubeThumbnailUri(String videoId) {
        String path = String.format("%s" + VIDEO_THUMBNAIL_FIRST_FILE_PATH, videoId);
        return Uri
                .parse(VIDEO_THUMBNAIL_BASE_URL)
                .buildUpon()
                .appendEncodedPath(path)
                .build();
    }
}
