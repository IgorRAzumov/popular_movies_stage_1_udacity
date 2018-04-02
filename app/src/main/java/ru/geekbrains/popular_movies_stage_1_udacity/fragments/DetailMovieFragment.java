package ru.geekbrains.popular_movies_stage_1_udacity.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MovieReviewsAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MovieVideosAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieReviewsAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieVideosAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieVideosResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.RecyclerViewOnClickListener;
import ru.geekbrains.popular_movies_stage_1_udacity.data.ReviewResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.ReviewsResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.VideoResult;

public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks,
        RecyclerViewOnClickListener {
    private int backgroundColor;
    private int titleTextColor;
    private int bodyTextColor;
    private String language;
    private MovieResult movie;
    private MovieReviewsAdapter reviewsAdapter;
    private MovieVideosAdapter videosAdapter;
    private Bitmap posterBitmap;

    @BindInt(R.integer.movie_videos_async_task_loader_id)
    int videosLoaderAsyncTaskId;

    @BindInt(R.integer.movie_reviews_async_task_loader_id)
    int reviewsLoaderAsyncTaskId;

    @BindView(R.id.tb_activity_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_fragment_detail_movie_poster_toolbar)
    ImageView posterImage;
    @BindView(R.id.rv_fragment_detail_movie_reviews)
    RecyclerView reviewsRecycler;
    @BindView(R.id.tv_fragment_detail_movies_name)
    TextView movieOriginalNameText;
    @BindView(R.id.tv_fragment_detail_movies_release_date)
    TextView movieReleaseDateText;
    @BindView(R.id.tv_fragment_detail_movies_vote_average)
    TextView movieVoteAverageText;
    @BindView(R.id.tv_fragment_detail_movies_plot_synopsis)
    TextView moviePlotSynopsisText;

    @BindView(R.id.pb_fragment_detail_movie_review_progress)
    ProgressBar reviewsProgressBar;
    @BindView(R.id.tv_fragment_detail_movie_review_result_load_info)
    TextView movieReviewsLoadInfoText;
    @BindView(R.id.bt_fragment_detail_movie_review_retry_load)
    Button retryLoadReviewsButton;
    @BindView(R.id.rv_fragment_detail_movie_videos)
    RecyclerView videosRecycler;
    @BindViews({R.id.lly_fragment_detail_movie_description_card, R.id.lly_fragment_detail_movie_main_card})
    List<LinearLayout> cardsBackgroundLayouts;

    private Unbinder unbinder;

    public DetailMovieFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(videosLoaderAsyncTaskId, getDefaultBundle(), this);
        loaderManager.initLoader(reviewsLoaderAsyncTaskId, getDefaultBundle(), this);
    }

    public static DetailMovieFragment newInstance(Context context, int backgroundColor,
                                                  int titleTextColor, int bodyTextColor,
                                                  String language, Bitmap bitmap,
                                                  MovieResult movie) {
        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.background_color_bundle_key),
                backgroundColor);
        bundle.putInt(context.getString(R.string.title_color_bundle_key),
                titleTextColor);
        bundle.putInt(context.getString(R.string.text_color_bundle_key),
                bodyTextColor);
        bundle.putString(context.getString(R.string.language_key), language);
        bundle.putParcelable(
                context.getString(R.string.poster_bitmap_bundle_key), bitmap);
        bundle.putParcelable(context.getString(R.string.movie_bundle_key), movie);

        DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
        detailMovieFragment.setArguments(bundle);
        return detailMovieFragment;
    }

    public static DetailMovieFragment newInstance(Context context, String language,
                                                  Bitmap bitmap, MovieResult movie) {
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.language_key), language);
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
            language = args.getString(getString(R.string.language_key));
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
           setBackgroundColors();
        }

        posterImage.setImageBitmap(posterBitmap);
        movieOriginalNameText.setText(movie.getTitle());
        movieReleaseDateText.append(movie.getReleaseDate());
        movieVoteAverageText.append(String.valueOf(movie.getVoteAverage()));
        moviePlotSynopsisText.append(movie.getOverview());

        reviewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        reviewsRecycler.setHasFixedSize(true);

        videosRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        videosRecycler.setHasFixedSize(true);
        return view;
    }

    private void setBackgroundColors() {
        ButterKnife.apply(cardsBackgroundLayouts, SET_BACKGROUND_CARD);
        movieOriginalNameText.setTextColor(titleTextColor);
        movieReleaseDateText.setTextColor(bodyTextColor);
        movieVoteAverageText.setTextColor(bodyTextColor);
        moviePlotSynopsisText.setTextColor(bodyTextColor);
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


    @NonNull
    @Override
    public Loader onCreateLoader(final int id, @Nullable Bundle args) {
        if (id == videosLoaderAsyncTaskId) {
            return new MovieVideosAsyncTaskLoader(getContext(), args);
        } else if (id == reviewsLoaderAsyncTaskId) {
            return new MovieReviewsAsyncTaskLoader(getContext(), args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        if (loader.getId() == videosLoaderAsyncTaskId) {
            if (data != null && data instanceof MovieVideosResponse) {
                completeLoadVideos(((MovieVideosResponse) data).getResults());
            } else {
                errorLoadVideos();
            }
        } else if (loader.getId() == reviewsLoaderAsyncTaskId) {
            if (data != null && data instanceof ReviewsResponse) {
                completeReviewsLoad(((ReviewsResponse) data).getResults());
            } else {
                errorLoadReviews();
            }
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.movie_id_bundle_key), movie.getId());
        bundle.putString(getString(R.string.language_key), language);
        return bundle;
    }

    final ButterKnife.Action<View> SET_BACKGROUND_CARD = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setBackgroundColor(backgroundColor);
        }
    };


    private void completeLoadVideos(List<VideoResult> results) {
        if (results != null) {
            if (results.size() == 0) {

            } else {
                videosAdapter = new MovieVideosAdapter(results, this);
                videosRecycler.setAdapter(videosAdapter);
            }
        } else {

        }
    }

    public void completeReviewsLoad(@Nullable List<ReviewResult> results) {
        reviewsProgressBar.setVisibility(View.GONE);
        if (results != null) {
            if (results.size() == 0) {
                movieReviewsLoadInfoText.setVisibility(View.VISIBLE);
                movieReviewsLoadInfoText.setText(R.string.fragment_movie_detail_no_reviews);
            } else {
                reviewsAdapter = new MovieReviewsAdapter(results);
                reviewsRecycler.setAdapter(reviewsAdapter);

            }
        } else {
            showErrorReviewsLoadMessage();
        }
    }



    private void errorLoadVideos() {

    }

    public void errorLoadReviews() {
        reviewsProgressBar.setVisibility(View.GONE);
        showErrorReviewsLoadMessage();
    }

    public void showErrorReviewsLoadMessage() {
        movieReviewsLoadInfoText.setVisibility(View.VISIBLE);
        retryLoadReviewsButton.setVisibility(View.VISIBLE);
        movieReviewsLoadInfoText.setText(R.string.fragment_movie_detail_error_load_reviews);
    }

    @Override
    public void onItemRecyclerClick(int position) {

    }
}
