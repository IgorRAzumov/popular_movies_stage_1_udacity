package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.DetailMovieFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.UriUtils;

public class DetailMovieActivity extends AppCompatActivity
        implements DetailMovieFragment.OnFragmentInteractionListener {
    @BindView(R.id.fab_detail_movie_activity)
    FloatingActionButton floatButton;
    @BindView(R.id.pb_activity_detail_movie_progress)
    ProgressBar progressBar;

    private boolean isFavoriteStatusChange;
    private boolean isPosterLoad;

    private int moviePosition;
    private String language;
    private DisplayableMovie movie;
    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);
        initData();

        if (savedInstanceState != null) {
            getDataFromInstanceState(savedInstanceState);
        } else {
            startPosterLoad();
        }

        initFloatButton();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                setActivityResult();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getString(R.string.movie_is_favorite_status_change_key),
                isFavoriteStatusChange);
        outState.putBoolean(getString(R.string.is_poster_load_key), isPosterLoad);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        setActivityResult();
    }

    @Override
    public void openVideo(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, UriUtils.getVideoUri(videoId));
        startActivity(intent);
    }


    @NonNull
    private Target getTarget() {
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                isPosterLoad = true;
                Palette
                        .from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch swatch = palette.getMutedSwatch();
                                completeDataLoad(bitmap, swatch);
                            }
                        });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }




    private void initFloatButton() {
        int idImageResource;
        if ((movie.isFavorite() && isFavoriteStatusChange) ||
                (!movie.isFavorite() && !isFavoriteStatusChange)) {
            idImageResource = R.drawable.ic_add_favorite;
        } else {
            idImageResource = R.drawable.ic_remove_favorite;
        }
        floatButton.setImageResource(idImageResource);
        floatButton.setTag(idImageResource);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFavoriteStatus();
            }
        });
    }

    private void initData() {
        target = getTarget();
        language = Resources.getSystem().getConfiguration().locale.getLanguage();

        Intent intent = getIntent();
        moviePosition = intent.getIntExtra(getString(R.string.movie_position_key),
                getResources().getInteger(R.integer.no_selected_position));
        movie = intent.getParcelableExtra(getString(R.string.movie_key));

        if (movie == null) {
            throw new RuntimeException(this.getClass().getCanonicalName()
                    + getString(R.string.error_inbox_intent_data_movie));
        }
    }

    private void getDataFromInstanceState(Bundle savedInstanceState) {
        isFavoriteStatusChange = savedInstanceState
                .getBoolean(getString(R.string.movie_is_favorite_status_change_key));
        isPosterLoad = savedInstanceState.getBoolean(getString(R.string.is_poster_load_key));

        if (!isPosterLoad) {
            startPosterLoad();
        }
    }

    private void startPosterLoad() {
        progressBar.setVisibility(View.VISIBLE);
        loadPosterImage();
    }

    private void loadPosterImage() {
        NetworkUtils.loadPosterImageInTarget(DetailMovieActivity.this, movie.getMoviePosterPath(),
                target, R.drawable.default_poster_image);
    }

    private void completeDataLoad(Bitmap posterImage, @Nullable Palette.Swatch swatch) {
        progressBar.setVisibility(View.INVISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_main_activity_container);

        if (fragment == null || !(fragment instanceof DetailMovieFragment)) {
            setFragment(posterImage, swatch, fragment);
        } else {
            setDataInFragment(posterImage, swatch, (DetailMovieFragment) fragment);
        }
    }

    private void setFragment(Bitmap posterImage, @Nullable Palette.Swatch swatch,
                             Fragment fragment) {
        DetailMovieFragment detailMovieFragment;
        if (swatch == null) {
            int noColor = getResources().getInteger(R.integer.no_selected_position);
            detailMovieFragment = DetailMovieFragment.newInstance(
                    DetailMovieActivity.this, language, posterImage, movie, noColor,
                    noColor, noColor);
        } else {
            detailMovieFragment = DetailMovieFragment.newInstance(
                    DetailMovieActivity.this, language, posterImage, movie, swatch.getRgb(),
                    swatch.getTitleTextColor(), swatch.getBodyTextColor());
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (fragment == null) {
            fragmentTransaction.add(R.id.fl_detail_activity_container, detailMovieFragment);
        } else {
            fragmentTransaction.replace(R.id.fl_detail_activity_container, detailMovieFragment);
        }
        fragmentTransaction.commit();
    }

    private void setDataInFragment(Bitmap posterImage, Palette.Swatch swatch,
                                   DetailMovieFragment detailMovieFragment) {
        if (swatch == null) {
            int noColor = getResources().getInteger(R.integer.no_selected_position);
            detailMovieFragment.setData(posterImage, movie, noColor, noColor, noColor);
        } else {
            detailMovieFragment.setData(posterImage, movie, swatch.getRgb(),
                    swatch.getTitleTextColor(), swatch.getBodyTextColor());
        }
    }

    private void onChangeFavoriteStatus() {
        isFavoriteStatusChange = !isFavoriteStatusChange;
        int imageResourceId = (int) floatButton.getTag() == R.drawable.ic_add_favorite
                ? R.drawable.ic_remove_favorite
                : R.drawable.ic_add_favorite;
        floatButton.setImageResource(imageResourceId);
        floatButton.setTag(imageResourceId);
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.movie_is_favorite_status_change_key), isFavoriteStatusChange);
        intent.putExtra(getString(R.string.movie_key), movie);
        intent.putExtra(getString(R.string.movie_position_key), moviePosition);
        setResult(RESULT_OK, intent);
        finish();
    }


}
