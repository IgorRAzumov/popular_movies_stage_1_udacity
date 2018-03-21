package ru.geekbrains.popular_movies_stage_1_udacity.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.Result;

public class DetailMovieFragment extends Fragment {
    private int backgroundColor;
    private int titleTextColor;
    private int bodyTextColor;
    private Result movie;

    private Bitmap posterBitmap;


    public DetailMovieFragment() {

    }

    public static DetailMovieFragment newInstance(Context context, int backgroundColor,
                                                  int titleTextColor, int bodyTextColor,
                                                  Bitmap bitmap, Result movie) {
        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.background_color_bundle_key),
                backgroundColor);
        bundle.putInt(context.getString(R.string.title_color_bundle_key),
                titleTextColor);
        bundle.putInt(context.getString(R.string.text_color_bundle_key),
                bodyTextColor);
        bundle.putParcelable(
                context.getString(R.string.poster_bitmap_bundle_key), bitmap);
        bundle.putParcelable(context.getString(R.string.movie_bundle_key), movie);

        DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
        detailMovieFragment.setArguments(bundle);
        return detailMovieFragment;
    }

    public static DetailMovieFragment newInstance(Context context, Bitmap bitmap, Result movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(
                context.getString(R.string.poster_bitmap_bundle_key), bitmap);
        bundle.putParcelable(context.getString(R.string.movie_bundle_key), movie);

        DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
        detailMovieFragment.setArguments(bundle);
        return detailMovieFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            backgroundColor = args.getInt(getString(R.string.background_color_bundle_key),
                    getResources().getInteger(R.integer.detail_movie_fragment_no_color_value));
            titleTextColor = args.getInt(getString(R.string.title_color_bundle_key),
                    getResources().getInteger(R.integer.detail_movie_fragment_no_color_value));
            bodyTextColor = args.getInt(getString(R.string.text_color_bundle_key),
                    getResources().getInteger(R.integer.detail_movie_fragment_no_color_value));
            posterBitmap = args.getParcelable(getString(R.string.poster_bitmap_bundle_key));
            movie = args.getParcelable(getString(R.string.movie_bundle_key));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        initToolbar(view);
        TextView movieOriginalNameTextView = view.findViewById(R.id.tv_fragment_detail_movies_original_name);
        TextView movieReleaseDateTextView = view.findViewById(R.id.tv_fragment_detail_movies_release_date);
        TextView movieVoteAverageTextView = view.findViewById(R.id.tv_fragment_detail_movies_vote_average);
        TextView moviePlotSynopsisTextView = view.findViewById(R.id.tv_fragment_detail_movies_plot_synopsis);
        TextView moviePlotSynopsisTextViewLabel = view.findViewById(R.id.tv_fragment_detail_movie_film_description_label);
        LinearLayout mainMovieCard = view.findViewById(R.id.lly_fragment_detail_movie_main_card);
        LinearLayout descriptionMovieCard = view.findViewById(R.id.lly_fragment_detail_movie_description_card);

        ImageView posterImage = view.findViewById(R.id.iv_fragment_detail_movie_poster_toolbar);

        int noColorValue = getResources().getInteger(R.integer.detail_movie_fragment_no_color_value);
        if (backgroundColor != noColorValue && bodyTextColor != noColorValue
                && titleTextColor != noColorValue) {
            mainMovieCard.setBackgroundColor(backgroundColor);
            descriptionMovieCard.setBackgroundColor(backgroundColor);

            movieOriginalNameTextView.setTextColor(titleTextColor);
            movieReleaseDateTextView.setTextColor(bodyTextColor);
            movieVoteAverageTextView.setTextColor(bodyTextColor);

            moviePlotSynopsisTextViewLabel.setTextColor(titleTextColor);
            moviePlotSynopsisTextView.setTextColor(bodyTextColor);
        }

        posterImage.setImageBitmap(posterBitmap);
        movieOriginalNameTextView.setText(movie.getTitle());
        movieReleaseDateTextView.append(movie.getReleaseDate());
        movieVoteAverageTextView.append(String.valueOf(movie.getVoteAverage()));
        moviePlotSynopsisTextView.setText(movie.getOverview());

        return view;
    }


    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.tb_activity_detail_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(getString(R.string.empty));
            }
        }
    }
}
