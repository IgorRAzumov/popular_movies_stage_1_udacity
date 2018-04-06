package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.DetailMovieFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.ProgressBarFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.DbMoviesUtils;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

public class DetailMovieActivity extends AppCompatActivity implements Target {
    @BindView(R.id.fab_detail_movie_activity)
    FloatingActionButton floatButton;

    private String language;
    private DisplayableMovie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fl_detail_activity_container);
            if (fragment == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.fl_detail_activity_container, new ProgressBarFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }

            getMovieFromIntent();
            initFloatButton();
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

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(getString(R.string.movie_intent_key),movie);
        data.putExtra(getString(R.string.movie_is_favorite_key), movie.isFavorite());
        setResult(RESULT_OK, data);

        super.onBackPressed();
    }


    private void initFloatButton() {
        floatButton.setImageResource((movie.isFavorite())
                ? R.drawable.ic_remove_favorite
                : R.drawable.ic_add_favorite);

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.isFavorite()) {
                    deleteFromFavorite();
                    floatButton.setImageResource(R.drawable.ic_add_favorite);
                } else {
                    addToFavorites();
                    floatButton.setImageResource(R.drawable.ic_remove_favorite);
                }
            }
        });
    }

    private void getMovieFromIntent() {
        movie = getIntent().getParcelableExtra(getString(R.string.movie_intent_key));
        language = Resources.getSystem().getConfiguration().locale.getLanguage();

        if (movie != null || language == null) {
            String posterUrl = getString(R.string.poster_movie_base_url_w342) + movie.getMoviePosterPath();
            NetworkUtils.loadPosterImageInTarget(DetailMovieActivity.this, posterUrl, this);
        } else {
            throw new RuntimeException(this.getClass().getCanonicalName()
                    + getString(R.string.error_inbox_intent_data_movie));
        }
    }

    private void switchProgressFragment(Palette.Swatch swatch, Bitmap bitmap) {
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance(
                DetailMovieActivity.this, swatch.getRgb(), swatch.getTitleTextColor(),
                swatch.getBodyTextColor(), language, bitmap, movie);
        switchProgressToMovieDetail(detailMovieFragment);
    }

    private void switchProgressFragment(Bitmap bitmap) {
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance(
                DetailMovieActivity.this, language, bitmap, movie);
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

    public void addToFavorites() {
        if (DbMoviesUtils.addToFavorites(getApplicationContext(), movie)) {
            movie.setFavorite(true);
        }

    }

    public void deleteFromFavorite() {
        if (DbMoviesUtils.deleteFromFavorite(getApplicationContext(), movie.getMovieApiId())) {
            movie.setFavorite(false);
        }
    }
}
