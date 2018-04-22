package ru.geekbrains.popular_movies_stage_1_udacity.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DisplayableMovie implements Parcelable {
    public static final Parcelable.Creator<DisplayableMovie> CREATOR = new Parcelable.Creator<DisplayableMovie>() {
        @Override
        public DisplayableMovie createFromParcel(Parcel source) {
            return new DisplayableMovie(source);
        }

        @Override
        public DisplayableMovie[] newArray(int size) {
            return new DisplayableMovie[size];
        }
    };
    private final Integer movieApiId;
    private final String movieName;
    private final Double movieRating;
    private final String movieOverview;
    private final String moviePosterPath;
    private final String movieReleaseDate;
    private boolean isFavorite;

    private DisplayableMovie(Parcel in) {
        this.movieApiId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.movieName = in.readString();
        this.movieRating = (Double) in.readValue(Double.class.getClassLoader());
        this.movieOverview = in.readString();
        this.moviePosterPath = in.readString();
        this.movieReleaseDate = in.readString();
        this.isFavorite = in.readByte() != 0;
    }

    public DisplayableMovie(Integer movieApiId, String movieName, String movieOverview,
                            Double movieRating, String moviePosterPath, String movieReleaseDate,
                            boolean isFavorite) {
        this.movieApiId = movieApiId;
        this.movieName = movieName;
        this.movieOverview = movieOverview;
        this.movieRating = movieRating;
        this.moviePosterPath = moviePosterPath;
        this.movieReleaseDate = movieReleaseDate;
        this.isFavorite = isFavorite;
    }

    public Integer getMovieApiId() {
        return movieApiId;
    }

    public String getMovieName() {
        return movieName;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.movieApiId);
        dest.writeString(this.movieName);
        dest.writeValue(this.movieRating);
        dest.writeString(this.movieOverview);
        dest.writeString(this.moviePosterPath);
        dest.writeString(this.movieReleaseDate);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
    }
}
