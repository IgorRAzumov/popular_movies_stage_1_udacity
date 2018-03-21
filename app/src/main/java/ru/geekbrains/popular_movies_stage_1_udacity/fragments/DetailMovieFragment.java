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

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.Result;

public class DetailMovieFragment extends Fragment {
    private int backgroundColor;
    private int titleTextColor;
    private int bodyTextColor;
    private Result movie;

    private Bitmap posterBitmap;

    @BindView(R.id.tv_fragment_detail_movies_original_name)
    TextView movieOriginalNameTextView;
    @BindView(R.id.tv_fragment_detail_movies_release_date)
    TextView movieReleaseDateTextView;
    @BindView(R.id.tv_fragment_detail_movies_vote_average)
    TextView movieVoteAverageTextView;
    @BindView(R.id.tv_fragment_detail_movies_plot_synopsis)
    TextView moviePlotSynopsisTextView;
    @BindView(R.id.tv_fragment_detail_movie_film_description_label)
    TextView moviePlotSynopsisTextViewLabel;
    @BindView(R.id.iv_fragment_detail_movie_poster_toolbar)
    ImageView posterImage;
    @BindView(R.id.tb_activity_detail_toolbar)
    Toolbar toolbar;
    @BindViews({R.id.lly_fragment_detail_movie_description_card, R.id.lly_fragment_detail_movie_main_card})
    List<LinearLayout> cardsBackgroundLayouts;

    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this, view);
        initToolbar();


        int noColorValue = getResources().getInteger(R.integer.detail_movie_fragment_no_color_value);
        if (backgroundColor != noColorValue && bodyTextColor != noColorValue
                && titleTextColor != noColorValue) {
            ButterKnife.apply(cardsBackgroundLayouts, SET_BACKGROUND_CARD);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initToolbar() {
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

    final ButterKnife.Action<View> SET_BACKGROUND_CARD = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setBackgroundColor(backgroundColor);
        }
    };
}
