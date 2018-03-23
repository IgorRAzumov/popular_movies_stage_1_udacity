package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

import butterknife.BindInt;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieReviewsAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MovieVideosAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieVideosResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.ReviewsResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.DetailMovieFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.ProgressBarFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

import static ru.geekbrains.popular_movies_stage_1_udacity.activities.DetailMovieActivity.DetailActivityHandler.COMPLETE_REVIEWS_LOAD_HANDLER_CODE;
import static ru.geekbrains.popular_movies_stage_1_udacity.activities.DetailMovieActivity.DetailActivityHandler.COMPLETE_VIDEOS_LOAD_HANDLER_CODE;
import static ru.geekbrains.popular_movies_stage_1_udacity.activities.DetailMovieActivity.DetailActivityHandler.ERROR_REVIEWS_LOAD_HANDLER_CODE;
import static ru.geekbrains.popular_movies_stage_1_udacity.activities.DetailMovieActivity.DetailActivityHandler.ERROR_VIDEOS_LOAD_HANDLER_CODE;

public class DetailMovieActivity extends AppCompatActivity implements Target,
        LoaderManager.LoaderCallbacks {
    private String language;
    private MovieResult movie;
    private DetailActivityHandler detailActivityHandler;

    @BindInt(R.integer.movie_videos_async_task_loader_id)
    int videosLoaderAsyncTaskId;

    @BindInt(R.integer.movie_reviews_async_task_loader_id)
    int reviewsLoaderAsyncTaskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_detail_activity_container);
        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fl_detail_activity_container, new ProgressBarFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        detailActivityHandler = new DetailActivityHandler(
                new WeakReference<>(DetailMovieActivity.this));
        getMovieFromIntent();


        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(videosLoaderAsyncTaskId, getDefaultBundle(), this);
        loaderManager.initLoader(reviewsLoaderAsyncTaskId, getDefaultBundle(), this);
    }

    private Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.movie_id_bundle_key), movie.getId());
        bundle.putString(getString(R.string.language_key), language);
        return bundle;
    }

    private void getMovieFromIntent() {
        movie = getIntent().getParcelableExtra(getString(R.string.movie_intent_key));
        language = getIntent().getStringExtra(getString(R.string.language_key));
        if (movie != null || language == null) {
            String posterUrl = getString(R.string.poster_movie_base_url_w342) + movie.getPosterPath();
            NetworkUtils.LoadPosterImageInTarget(DetailMovieActivity.this, posterUrl, this);
        } else {
            throw new RuntimeException(this.getClass().getCanonicalName()
                    + getString(R.string.error_inbox_intent_data_movie));
        }
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        final Palette.Swatch[] swatch = new Palette.Swatch[1];
        Palette
                .from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                              @Override
                              public void onGenerated(@NonNull Palette palette) {
                                  swatch[0] = palette.getMutedSwatch();
                                  if (swatch[0] == null) {
                                      switchProgressFragment(bitmap);
                                  } else {
                                      switchProgressFragment(swatch[0], bitmap);
                                  }
                              }
                          }
                );
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    private void switchProgressFragment(Palette.Swatch swatch, Bitmap bitmap) {
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance(
                DetailMovieActivity.this, swatch.getRgb(), swatch.getTitleTextColor(),
                swatch.getBodyTextColor(), bitmap, movie);
        switchProgressToMovieDetail(detailMovieFragment);
    }

    private void switchProgressFragment(Bitmap bitmap) {
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance(
                DetailMovieActivity.this, bitmap, movie);
        switchProgressToMovieDetail(detailMovieFragment);
    }

    private void switchProgressToMovieDetail(DetailMovieFragment detailMovieFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_detail_activity_container);

        if (!(fragment instanceof DetailMovieFragment)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_detail_activity_container, detailMovieFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }


    @NonNull
    @Override
    public Loader onCreateLoader(final int id, @Nullable Bundle args) {
        if (id == videosLoaderAsyncTaskId) {
            return new MovieVideosAsyncTaskLoader(this, args);
        } else if (id == reviewsLoaderAsyncTaskId) {
            return new MovieReviewsAsyncTaskLoader(this, args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        Message message;
        if (data != null) {
            message = detailActivityHandler
                    .obtainMessage(
                            loader.getId() == videosLoaderAsyncTaskId
                                    ? COMPLETE_VIDEOS_LOAD_HANDLER_CODE
                                    : COMPLETE_REVIEWS_LOAD_HANDLER_CODE
                            , data);
        } else {
            message = detailActivityHandler
                    .obtainMessage(loader.getId() == videosLoaderAsyncTaskId
                            ? ERROR_VIDEOS_LOAD_HANDLER_CODE
                            : ERROR_REVIEWS_LOAD_HANDLER_CODE);
        }
        detailActivityHandler.sendMessage(message);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    private void completeReviewsLoad(ReviewsResponse obj) {
    }

    private void completeVideosLoad(MovieVideosResponse obj) {

    }

    private void errorLoadMovieReviews() {

    }

    private void errorLoadMovieVideos() {
    }

    static class DetailActivityHandler extends Handler {
        static final int COMPLETE_VIDEOS_LOAD_HANDLER_CODE = 301;
        static final int COMPLETE_REVIEWS_LOAD_HANDLER_CODE = 302;
        static final int ERROR_VIDEOS_LOAD_HANDLER_CODE = 303;
        static final int ERROR_REVIEWS_LOAD_HANDLER_CODE = 304;

        private final WeakReference<DetailMovieActivity> reference;

        private DetailActivityHandler(WeakReference<DetailMovieActivity> reference) {
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE_VIDEOS_LOAD_HANDLER_CODE: {
                    DetailMovieActivity detailMovieActivity = reference.get();
                    if (detailMovieActivity != null) {
                        detailMovieActivity.completeVideosLoad((MovieVideosResponse) msg.obj);
                    }
                    break;
                }
                case COMPLETE_REVIEWS_LOAD_HANDLER_CODE: {
                    DetailMovieActivity detailMovieActivity = reference.get();
                    if (detailMovieActivity != null) {
                        detailMovieActivity.completeReviewsLoad((ReviewsResponse) msg.obj);
                    }
                    break;
                }
                case ERROR_VIDEOS_LOAD_HANDLER_CODE: {
                    DetailMovieActivity detailMovieActivity = reference.get();
                    if (detailMovieActivity != null) {
                        detailMovieActivity.errorLoadMovieVideos();
                        break;
                    }
                }
                case ERROR_REVIEWS_LOAD_HANDLER_CODE: {
                    DetailMovieActivity detailMovieActivity = reference.get();
                    if (detailMovieActivity != null) {
                        detailMovieActivity.errorLoadMovieReviews();
                        break;
                    }
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }

    }
}
