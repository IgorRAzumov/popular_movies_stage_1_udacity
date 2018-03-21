package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.Result;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.DetailMovieFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.ProgressBarFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

public class DetailMovieActivity extends AppCompatActivity implements Target {
    private Result movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        movie = getIntent().getParcelableExtra(getString(R.string.movie_intent_key));
        if (movie != null) {
            String posterUrl = getString(R.string.poster_movie_base_url_w342) + movie.getPosterPath();////////////UriBuilder
            NetworkUtils.LoadBigPosterImage(DetailMovieActivity.this, posterUrl, this);
        } else {
            throw new RuntimeException(this.getClass().getCanonicalName()
                    + getString(R.string.error_inbox_intent_data_movie));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_detail_activity_container);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fl_detail_activity_container, new ProgressBarFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
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


}
