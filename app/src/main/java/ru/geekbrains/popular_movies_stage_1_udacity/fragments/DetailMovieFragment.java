package ru.geekbrains.popular_movies_stage_1_udacity.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MovieReviewsAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MovieVideosAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieReviewsAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieVideosAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.MovieVideosResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.ReviewResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.ReviewsResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.VideoResult;


public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks,
        MovieVideosAdapter.RecycleViewOnItemClickListener {
    private static final int VIDEOS_LOADER_ID = 21;
    private static final int REVIEWS_LOADER_ID = 22;

    @BindView(R.id.tb_activity_detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_fragment_detail_movie_poster_toolbar)
    ImageView posterImage;

    @BindView(R.id.rv_fragment_detail_movie_reviews)
    RecyclerView reviewsRecycler;
    private final ButterKnife.Action<View> SET_BACKGROUND_CARD = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setBackgroundColor(backgroundColor);
        }
    };
    @BindView(R.id.rv_fragment_detail_movie_videos)
    RecyclerView videosRecycler;
    @BindView(R.id.lly_fragment_detail_movie_videos_load_info)
    LinearLayout videosLoadInfoLayout;

    @BindView(R.id.tv_fragment_detail_movies_name)
    TextView movieOriginalNameText;
    @BindView(R.id.tv_fragment_detail_movies_release_date)
    TextView movieReleaseDateText;
    @BindView(R.id.tv_fragment_detail_movies_vote_average)
    TextView movieVoteAverageText;
    @BindView(R.id.lly_fragment_detail_movie_reviews_load_info)
    LinearLayout reviewsLoadInfoLayout;
    @BindView(R.id.tv_fragment_detail_movies_plot_synopsis)
    TextView moviePlotSynopsisText;
    @BindView(R.id.tv_fragment_detail_movie_videos_load_info)
    TextView videosLoadInfoText;
    @BindView(R.id.tv_fragment_detail_movie_review_load_info)
    TextView reviewsLoadInfoText;
    @BindView(R.id.pb_fragment_detail_movie_review_progress)
    ProgressBar reviewsProgressBar;
    @BindView(R.id.pb_fragment_detail_movie_videos_progress)
    ProgressBar videosProgressBar;
    @BindView(R.id.bt_fragment_detail_movie_review_retry_load)
    Button retryLoadReviewsButton;

    @BindViews({R.id.lly_fragment_detail_movie_description_card, R.id.lly_fragment_detail_movie_main_card})
    List<LinearLayout> cardsBackgroundLayouts;

    private int backgroundColor;
    @BindView(R.id.bt_fragment_detail_movie_videos_retry_load)
    Button retryLoadVideosButton;
    private int titleTextColor;
    private int bodyTextColor;
    private String language;
    private DisplayableMovie movie;
    private MovieReviewsAdapter reviewsAdapter;
    private MovieVideosAdapter videosAdapter;
    private Bitmap posterBitmap;
    private Unbinder unbinder;
    private OnFragmentInteractionListener interactionListener;


    public DetailMovieFragment() {

    }

    public static DetailMovieFragment newInstance(Context context, String language, Bitmap bitmap,
                                                  DisplayableMovie movie, int backgroundColor,
                                                  int titleTextColor, int bodyTextColor) {
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.language_key), language);
        bundle.putParcelable(
                context.getString(R.string.poster_bitmap_bundle_key), bitmap);
        bundle.putParcelable(context.getString(R.string.movie_bundle_key), movie);

        if (backgroundColor != 0 && bodyTextColor != 0 && titleTextColor != 0) {
            bundle.putInt(context.getString(R.string.background_color_bundle_key),
                    backgroundColor);
            bundle.putInt(context.getString(R.string.title_color_bundle_key),
                    titleTextColor);
            bundle.putInt(context.getString(R.string.text_color_bundle_key),
                    bodyTextColor);
        }

        DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
        detailMovieFragment.setArguments(bundle);
        return detailMovieFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LoaderManager loaderManager = activity.getSupportLoaderManager();
            loaderManager.initLoader(VIDEOS_LOADER_ID, getDefaultBundle(), this);
            loaderManager.initLoader(REVIEWS_LOADER_ID, getDefaultBundle(), this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getDataFromArguments(bundle);
        } else {
            throw new RuntimeException(getString(R.string.error_fragment_instance_bundle) +
                    DetailMovieFragment.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        initUi();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailMovieFragment.OnFragmentInteractionListener) {
            interactionListener = (DetailMovieFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.error_on_activity_implement_fragment_listener));
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @NonNull
    @Override
    public Loader onCreateLoader(final int id, @Nullable Bundle args) {
        switch (id) {
            case VIDEOS_LOADER_ID: {
                return new MovieVideosAsyncTaskLoader(getContext(), args);
            }
            case REVIEWS_LOADER_ID: {
                return new MovieReviewsAsyncTaskLoader(getContext(), args);
            }
            default: {
                throw new RuntimeException(getString(R.string.error_loader_not_impl) + id);
            }
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case VIDEOS_LOADER_ID: {
                if (data != null) {
                    completeLoadVideos(((MovieVideosResponse) data).getResults());
                } else {
                    errorLoadVideos();
                }
                break;
            }
            case REVIEWS_LOADER_ID: {
                if (data != null) {
                    completeLoadReviews(((ReviewsResponse) data).getResults());
                } else {
                    errorLoadReviews();
                }
                break;
            }
            default: {
                throw new RuntimeException(getString(R.string.error_loader_not_impl) + loader.getId());
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onItemVideoRecyclerClick(int position) {
        interactionListener.openVideo(videosAdapter.getVideo(position).getKey());
    }

    public void setData(Bitmap posterImage, DisplayableMovie movie, int backgroundColor,
                        int titleTextColor, int bodyTextColor) {
        posterBitmap = posterImage;
        this.movie = movie;
        this.backgroundColor = backgroundColor;
        this.titleTextColor = titleTextColor;
        this.bodyTextColor = bodyTextColor;
    }

    private Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.movie_id_bundle_key), movie.getMovieApiId());
        bundle.putString(getString(R.string.language_key), language);
        return bundle;
    }

    private void getDataFromArguments(@NonNull Bundle args) {
        Resources resources = getResources();
        backgroundColor = args.getInt(getString(R.string.background_color_bundle_key),
                resources.getInteger(R.integer.detail_movie_fragment_no_color_value));
        titleTextColor = args.getInt(getString(R.string.title_color_bundle_key),
                resources.getInteger(R.integer.detail_movie_fragment_no_color_value));
        bodyTextColor = args.getInt(getString(R.string.text_color_bundle_key),
                resources.getInteger(R.integer.detail_movie_fragment_no_color_value));
        language = args.getString(getString(R.string.language_key));
        posterBitmap = args.getParcelable(getString(R.string.poster_bitmap_bundle_key));
        movie = args.getParcelable(getString(R.string.movie_bundle_key));
    }

    private void initUi() {
        initToolbar();
        initRecyclers();
        initButtons();
        setColors();
        showMovieData();
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

    private void initRecyclers() {
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        reviewsRecycler.setHasFixedSize(true);

        videosRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        videosRecycler.setHasFixedSize(true);

    }

    private void initButtons() {
        retryLoadVideosButton.setOnClickListener(getRetryLoadOnClickListener());
        retryLoadReviewsButton.setOnClickListener(getRetryLoadOnClickListener());
    }

    @NonNull
    private View.OnClickListener getRetryLoadOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    int loaderId;
                    if (v.getId() == R.id.bt_fragment_detail_movie_videos_retry_load) {
                        loaderId = VIDEOS_LOADER_ID;
                        retryLoadVideosButton.setVisibility(View.GONE);
                        videosLoadInfoLayout.setVisibility(View.GONE);
                        videosProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        loaderId = REVIEWS_LOADER_ID;
                        retryLoadReviewsButton.setVisibility(View.GONE);
                        reviewsLoadInfoLayout.setVisibility(View.GONE);
                        reviewsProgressBar.setVisibility(View.VISIBLE);
                    }
                    activity.getSupportLoaderManager().restartLoader(loaderId, getDefaultBundle()
                            , DetailMovieFragment.this);
                }
            }
        };
    }

    private void setColors() {
        int noColorValue = getResources().getInteger(R.integer.detail_movie_fragment_no_color_value);
        if (backgroundColor != noColorValue && bodyTextColor != noColorValue
                && titleTextColor != noColorValue) {
            ButterKnife.apply(cardsBackgroundLayouts, SET_BACKGROUND_CARD);
            movieOriginalNameText.setTextColor(titleTextColor);
            movieReleaseDateText.setTextColor(bodyTextColor);
            movieVoteAverageText.setTextColor(bodyTextColor);
            moviePlotSynopsisText.setTextColor(bodyTextColor);
        }
    }

    private void showMovieData() {
        posterImage.setImageBitmap(posterBitmap);
        movieOriginalNameText.setText(movie.getMovieName());
        movieReleaseDateText.append(movie.getMovieReleaseDate());
        movieVoteAverageText.append(String.valueOf(movie.getMovieRating()));
        moviePlotSynopsisText.append(movie.getMovieOverview());
    }

    private void completeLoadVideos(List<VideoResult> results) {
        videosProgressBar.setVisibility(View.GONE);
        if (results != null) {
            if (results.size() == 0) {
                videosLoadInfoLayout.setVisibility(View.VISIBLE);
                videosLoadInfoText.setText(R.string.fragment_movie_detail_no_videos);
            } else {
                videosAdapter = new MovieVideosAdapter(results, this);
                videosRecycler.setAdapter(videosAdapter);
            }
        } else {
            errorLoadVideos();
        }
    }

    private void completeLoadReviews(@Nullable List<ReviewResult> results) {
        reviewsProgressBar.setVisibility(View.GONE);
        if (results != null) {
            if (results.size() == 0) {
                reviewsLoadInfoLayout.setVisibility(View.VISIBLE);
                reviewsLoadInfoText.setText(R.string.fragment_movie_detail_no_reviews);
            } else {
                reviewsAdapter = new MovieReviewsAdapter(results);
                reviewsRecycler.setAdapter(reviewsAdapter);
            }
        } else {
            errorLoadReviews();
        }
    }

    private void errorLoadVideos() {
        videosProgressBar.setVisibility(View.GONE);
        videosLoadInfoLayout.setVisibility(View.VISIBLE);
        retryLoadVideosButton.setVisibility(View.VISIBLE);
        videosLoadInfoText.setText(R.string.fragment_movie_detail_error_load_videos);
    }

    private void errorLoadReviews() {
        reviewsProgressBar.setVisibility(View.GONE);
        reviewsLoadInfoLayout.setVisibility(View.VISIBLE);
        retryLoadReviewsButton.setVisibility(View.VISIBLE);
        reviewsLoadInfoText.setText(R.string.fragment_movie_detail_error_load_reviews);
    }

    public interface OnFragmentInteractionListener {
        void openVideo(String url);

    }
}
