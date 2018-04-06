package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MoviesAsyncTaskDbLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MoviesAsyncTaskNetworkLoader;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableDataMovies;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.MoviesResultFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.DbMoviesUtils;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.PrefUtils;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks,
        MoviesResultFragment.OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {
    private final int MOVIES_NETWORK_LOADER_ID = 11;
    private final int MOVIES_DATABASE_LOADER_ID = 21;

    @BindView(R.id.bnv_main_activity_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.pb_activity_main_progress)
    ProgressBar progressBar;

    private boolean isBeforeSelectItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        if (savedInstanceState == null) {
            progressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getSupportLoaderManager();

            int savedSelectedItemId = PrefUtils.readBotNavSelectedItemSharedPref(this);
            if (savedSelectedItemId == R.id.menu_bt_nav_favorites) {
                loaderManager.initLoader(MOVIES_DATABASE_LOADER_ID, null, this);
            } else {
                loaderManager.initLoader(MOVIES_NETWORK_LOADER_ID, null, this);
            }
            bottomNavigationView.setSelectedItemId(savedSelectedItemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        progressBar.setVisibility(View.VISIBLE);
        LoaderManager loaderManager = getSupportLoaderManager();
        int itemId = item.getItemId();

        if (itemId == R.id.menu_bt_nav_favorites) {
            loaderManager.restartLoader(MOVIES_DATABASE_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(MOVIES_NETWORK_LOADER_ID, getLoaderBundle(itemId),
                    this);
        }

        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull final MenuItem item) {
        if (!isBeforeSelectItem) {
            onNavigationItemSelected(item);
            isBeforeSelectItem = !isBeforeSelectItem;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefUtils.writeBotNavSelectedItemToSharedPref(this,
                bottomNavigationView.getSelectedItemId());
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case MOVIES_NETWORK_LOADER_ID: {
                return new MoviesAsyncTaskNetworkLoader(this, args);
            }
            case MOVIES_DATABASE_LOADER_ID: {
                return new MoviesAsyncTaskDbLoader(this);
            }
            default:
                throw new RuntimeException(getString(R.string.eeror_loader_not_impl) + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        if (data == null) {
            errorDataLoad(loader.getId());
            return;
        }

        int loaderId = loader.getId();
        if (loaderId == MOVIES_NETWORK_LOADER_ID || loaderId == MOVIES_DATABASE_LOADER_ID) {
            completeDataLoad((DisplayableDataMovies) data);
        } else {
            throw new RuntimeException(getString(R.string.eeror_loader_not_impl) + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onMovieClick(DisplayableMovie movie) {
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra(getString(R.string.movie_intent_key), movie);
        startActivityForResult(intent, getResources()
                .getInteger(R.integer.main_activity_result_code_favorite_status));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources()
                .getInteger(R.integer.main_activity_result_code_favorite_status)) {
            String movieKey = getString(R.string.movie_intent_key);
            String movieIsFavoriteKey = getString(R.string.movie_is_favorite_key);

            if (data.hasExtra(movieIsFavoriteKey) && data.hasExtra(movieKey)) {
                updateFavoriteStatus((DisplayableMovie) data.getParcelableExtra(movieKey),
                        data.getBooleanExtra(movieIsFavoriteKey, false));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateFavoriteStatus(DisplayableMovie movie, boolean isFavorite) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fl_main_activity_container);
        if (fragment != null && fragment instanceof MoviesResultFragment) {
            ((MoviesResultFragment) fragment).updateFavoriteStatus(movie, isFavorite);
        }

    }

    @Override
    public void onFavoriteClick(DisplayableMovie movie) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fl_main_activity_container);
        if (fragment != null && fragment instanceof MoviesResultFragment) {
            if (movie.isFavorite()) {
                deleteMovieFromFavorites(fragment, movie);
            } else {
                addMovieToFavorites(fragment, movie);
            }
        }
    }

    private void addMovieToFavorites(Fragment fragment, DisplayableMovie movie) {
        if (DbMoviesUtils.addToFavorites(this, movie)) {
            ((MoviesResultFragment) fragment).addMovieToFavorite(movie);
        }
    }

    private void deleteMovieFromFavorites(Fragment fragment, DisplayableMovie movie) {
        if (DbMoviesUtils.deleteFromFavorite(this, movie.getMovieApiId())) {
            ((MoviesResultFragment) fragment).deleteMovieFromFavorite(movie,
                    bottomNavigationView.getSelectedItemId() == R.id.menu_bt_nav_favorites);
        }
    }


    private void completeDataLoad(DisplayableDataMovies data) {
        progressBar.setVisibility(View.INVISIBLE);
        List<DisplayableMovie> moviesList = data.getMovies();
        if (moviesList.size() == 0) {
            noSearchResult();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager
                    .findFragmentById(R.id.fl_main_activity_container);
            if (fragment != null) {
                ((MoviesResultFragment) fragment).setData(moviesList);
            } else {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fl_main_activity_container,
                        MoviesResultFragment.newInstance(this, new ArrayList<>(moviesList)))
                        .setTransition(TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }
    }


    private void noSearchResult() {
        String message = getString(
                bottomNavigationView.getSelectedItemId() == R.id.menu_bt_nav_favorites
                        ? R.string.no_network_search_result
                        : R.string.no_database_search_result);
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.fl_main_activity_container),
                message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void errorDataLoad(final int loaderId) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fl_main_activity_container),
                getString(R.string.error_load_data_message), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.main_activity_retry_load_data, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (loaderId) {
                    case MOVIES_NETWORK_LOADER_ID: {
                        getSupportLoaderManager().restartLoader(loaderId,
                                getLoaderBundle(bottomNavigationView.getSelectedItemId()),
                                MainActivity.this);
                        break;
                    }
                    case MOVIES_DATABASE_LOADER_ID: {
                        getSupportLoaderManager().restartLoader(loaderId,
                                null,
                                MainActivity.this);
                        break;
                    }
                    default:
                        throw new RuntimeException(getString(R.string.eeror_loader_not_impl)
                                + loaderId);
                }
            }
        });
        snackbar.show();
    }

    private Bundle getLoaderBundle(int selectedNavId) {
        String sortBy = (selectedNavId == R.id.menu_bt_nav_popular)
                ? getString(R.string.movies_request_param_sort_by_popular)
                : getString(R.string.movies_request_param_sort_by_top_rated);

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.sort_by_movies_bundle_key), sortBy);
        bundle.putString(getString(R.string.language_key), Resources.getSystem().getConfiguration()
                .locale.getLanguage());
        return bundle;
    }
}
