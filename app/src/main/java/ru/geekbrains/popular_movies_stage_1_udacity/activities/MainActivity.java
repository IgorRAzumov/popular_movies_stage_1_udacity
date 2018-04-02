package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MoviesAsyncTaskLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.MoviesResultFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.PrefUtils;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks,
        MoviesResultFragment.OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener,
        NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.bnv_main_activity_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.pb_activity_main_progress)
    ProgressBar progressBar;
    @BindView(R.id.nsv_main_activity_scroll_view)
    NestedScrollView nestedScrollView;

    @BindInt(R.integer.movies_async_task_loader_id)
    int moviesLoaderId;

    private boolean isNavigationHide;
    private boolean isBeforeSelectItem;
    //  private int lastSelectedIdItemBotNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        nestedScrollView.setOnScrollChangeListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        getSupportLoaderManager().initLoader(moviesLoaderId, null, this);

        int lastSelectedIdItemBotNav = PrefUtils.readBotNavSelectedItemSharedPref(this);
        bottomNavigationView.setSelectedItemId(lastSelectedIdItemBotNav);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        bottomNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_bt_nav_favorites: {
                        //return true;
                    }
                    default: {
                        String sortBy = getString(itemId == R.id.menu_bt_nav_popular
                                ? R.string.movies_request_param_sort_by_popular
                                : R.string.movies_request_param_sort_by_top_rated);
                        getSupportLoaderManager().restartLoader(moviesLoaderId, getLoaderBundle(sortBy),
                                MainActivity.this);

                    }
                }
            }
        }, 100);
        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull final MenuItem item) {
        bottomNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isBeforeSelectItem) {
                    int itemId = item.getItemId();
                    switch (itemId) {
                        default: {
                            String sortBy = getString(itemId == R.id.menu_bt_nav_popular
                                    ? R.string.movies_request_param_sort_by_popular
                                    : R.string.movies_request_param_sort_by_top_rated);
                            getSupportLoaderManager().restartLoader(moviesLoaderId, getLoaderBundle(sortBy),
                                    MainActivity.this);
                        }

                    }
                    isBeforeSelectItem = true;
                }
            }
        }, 300);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefUtils.writeBotNavSelectedItemToSharedPref(this,
                bottomNavigationView.getSelectedItemId());
    }

    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == moviesLoaderId) {
            return new MoviesAsyncTaskLoader(this, args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        if (loader.getId() == moviesLoaderId) {
            if (data != null && data instanceof MoviesResponse) {
                completeDataLoad((MoviesResponse) data);
            } else {
                errorDataLoad();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private void completeDataLoad(MoviesResponse moviesResponse) {
        progressBar.setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.fl_main_activity_container);

        if (fragment != null && fragment instanceof MoviesResultFragment) {
            ((MoviesResultFragment) fragment).setData(moviesResponse.getResults());
        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragment == null) {
                fragmentTransaction.add(R.id.fl_main_activity_container,
                        MoviesResultFragment.newInstance(this,
                                new ArrayList<>(moviesResponse.getResults())));
            } else {
                fragmentTransaction.replace(R.id.fl_main_activity_container,
                        MoviesResultFragment.newInstance(this,
                                new ArrayList<>(moviesResponse.getResults())));
            }
            fragmentTransaction.setTransition(TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }

    }

    private void errorDataLoad() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fl_main_activity_container),
                getString(R.string.error_load_data_message), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.main_activity_retry_load_data, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader loader = loaderManager
                        .getLoader(moviesLoaderId);
                if (loader == null) {
                    loaderManager.initLoader(moviesLoaderId, getLoaderBundle(
                            PrefUtils.readSortByTypeFromSharedPref(
                                    MainActivity.this)),
                            MainActivity.this);
                } else {
                    loaderManager.restartLoader(moviesLoaderId,
                            getLoaderBundle(PrefUtils.readSortByTypeFromSharedPref(
                                    MainActivity.this)), MainActivity.this);
                }
            }
        });
        snackbar.show();
    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) {
            return;
        }
        isNavigationHide = hide;
        int moveY = hide ? (2 * bottomNavigationView.getHeight()) : 0;
        bottomNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private Bundle getLoaderBundle(String sortBy) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.sort_by_movies_bundle_key), sortBy);
        bundle.putString(getString(R.string.language_key), Resources.getSystem().getConfiguration()
                .locale.getLanguage());
        return bundle;
    }

    @Override
    public void onMovieClick(MovieResult movie) {
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra(getString(R.string.movie_intent_key), (Parcelable) movie);
        startActivity(intent);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                               int oldScrollX, int oldScrollY) {
        if (scrollY < oldScrollY) { // up
            animateNavigation(false);
        }
        if (scrollY > oldScrollY) { // down
            animateNavigation(true);

        }
    }
}

