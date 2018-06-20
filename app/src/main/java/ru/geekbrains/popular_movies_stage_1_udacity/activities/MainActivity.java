package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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

import static android.support.design.widget.BottomNavigationView.OnNavigationItemReselectedListener;
import static android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        MoviesResultFragment.OnFragmentInteractionListener {
    private final int MOVIES_NETWORK_LOADER_ID = 11;
    private final int MOVIES_DATABASE_LOADER_ID = 21;

    @BindView(R.id.bnv_main_activity_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.pb_activity_main_progress)
    ProgressBar progressBar;
    @BindView(R.id.cl_main_activity)
    CoordinatorLayout coordinatorLayout;

    private boolean isBeforeSelectedNavItem;
    private int selectedNavItemId;

    private OnNavigationItemSelectedListener navItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        selectedNavItemId = PrefUtils.readBotNavSelectedItemSharedPref(MainActivity.this);
        unitUi();

        if (savedInstanceState == null) {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefUtils.writeBotNavSelectedItemToSharedPref(this, selectedNavItemId);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case MOVIES_NETWORK_LOADER_ID:
                return new MoviesAsyncTaskNetworkLoader(this, args);
            case MOVIES_DATABASE_LOADER_ID:
                return new MoviesAsyncTaskDbLoader(this);
            default:
                throw new RuntimeException(getString(R.string.error_loader_not_impl) + id);
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
            getSupportLoaderManager().destroyLoader(loaderId);
        } else {
            throw new RuntimeException(getString(R.string.error_loader_not_impl) + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onMovieClick(DisplayableMovie movie, int position) {
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra(getString(R.string.movie_key), movie);
        intent.putExtra(getString(R.string.movie_position_key), position);
        startActivityForResult(intent, getResources()
                .getInteger(R.integer.main_activity_result_code_favorite_status));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.main_activity_result_code_favorite_status)
                && resultCode == RESULT_OK) {
            checkUpdateFavoriteStatus(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void favoriteStatusChange(DisplayableMovie movie) {
        if (movie.isFavorite()) {
            DbMoviesUtils.deleteFromFavoriteByApiId(this, movie.getMovieApiId());
        } else {
            DbMoviesUtils.addToFavorites(this, movie);
        }

        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fl_main_activity_container);
        if (fragment != null && fragment instanceof MoviesResultFragment) {
            updateResultFragment(movie, (MoviesResultFragment) fragment);
        }
    }

    public void updateResultFragment(DisplayableMovie movie, MoviesResultFragment fragment) {
        if (movie.isFavorite()) {
            fragment.deleteMovieFromFavorite(movie,
                    bottomNavigationView.getSelectedItemId() == R.id.menu_bt_nav_favorites);
        } else {
            fragment.addMovieToFavorite(movie);
        }
    }

    private void unitUi() {
        navItemSelectedListener = getNavItemSelectedListener();
        bottomNavigationView.setOnNavigationItemSelectedListener(navItemSelectedListener);
        bottomNavigationView.setOnNavigationItemReselectedListener(getNavItemReselectedListener());
        bottomNavigationView.setSelectedItemId(selectedNavItemId);
    }

    @NonNull
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


    @NonNull
    private OnNavigationItemSelectedListener getNavItemSelectedListener() {
        return new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                progressBar.setVisibility(View.VISIBLE);
                selectedNavItemId = item.getItemId();
                if (selectedNavItemId == R.id.menu_bt_nav_favorites) {
                    startDbLoader();
                } else {
                    startNetworkLoader();
                }
                return true;
            }
        };
    }

    @NonNull
    private OnNavigationItemReselectedListener getNavItemReselectedListener() {
        return new OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (!isBeforeSelectedNavItem) {
                    navItemSelectedListener.onNavigationItemSelected(item);
                    isBeforeSelectedNavItem = !isBeforeSelectedNavItem;
                }
            }
        };
    }


    private void startNetworkLoader() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = getLoaderBundle(selectedNavItemId);
        if (loaderManager.getLoader(MOVIES_NETWORK_LOADER_ID) == null) {
            loaderManager.initLoader(MOVIES_NETWORK_LOADER_ID, bundle, MainActivity.this);
        } else {
            loaderManager.restartLoader(MOVIES_NETWORK_LOADER_ID, bundle, MainActivity.this);
        }
    }

    private void startDbLoader() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(MOVIES_DATABASE_LOADER_ID) == null) {
            loaderManager.initLoader(MOVIES_DATABASE_LOADER_ID, null, MainActivity.this);
        } else {
            loaderManager.restartLoader(MOVIES_DATABASE_LOADER_ID, new Bundle(), MainActivity.this);
        }
    }


    private void checkUpdateFavoriteStatus(Intent data) {
        boolean isFavoriteStatusChange =
                data.getBooleanExtra(getString(R.string.movie_is_favorite_status_change_key),
                        false);
        if (isFavoriteStatusChange) {
            int moviePosition =
                    data.getIntExtra(getString(R.string.movie_position_key),
                            getResources().getInteger(R.integer.no_selected_position));
            DisplayableMovie movie = data.getParcelableExtra(getString(R.string.movie_key));
            updateFavoriteStatus(moviePosition, movie);
        }

    }

    private void updateFavoriteStatus(int position, DisplayableMovie movie) {
        if (movie.isFavorite()) {
            DbMoviesUtils.deleteFromFavoriteByApiId(this, movie.getMovieApiId());
        } else {
            DbMoviesUtils.addToFavorites(this, movie);
        }

        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fl_main_activity_container);
        if (fragment != null && fragment instanceof MoviesResultFragment) {
            ((MoviesResultFragment) fragment).updateMovieFavoriteStatus(position,
                    bottomNavigationView.getSelectedItemId() == R.id.menu_bt_nav_favorites);
        }
    }

    private void completeDataLoad(DisplayableDataMovies data) {
        progressBar.setVisibility(View.INVISIBLE);
        ArrayList<DisplayableMovie> moviesList = new ArrayList<>(data.getMovies());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.fl_main_activity_container);

        if (fragment == null || !(fragment instanceof MoviesResultFragment)) {
            setFragment(moviesList, fragment);
        } else {
            setDataInFragment((MoviesResultFragment) fragment, moviesList);
        }
    }

    private void setDataInFragment(MoviesResultFragment moviesResultFragment,
                                   ArrayList<DisplayableMovie> moviesList) {
        moviesResultFragment.clearData();

        if (checkEmptyResult(moviesList)) {
            return;
        }
        moviesResultFragment.setData(moviesList);
    }

    private void setFragment(ArrayList<DisplayableMovie> moviesList, Fragment fragment) {
        if (checkEmptyResult(moviesList)) {
            return;
        }

        MoviesResultFragment resultFragment = MoviesResultFragment.newInstance(this, moviesList);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(TRANSIT_FRAGMENT_OPEN);

        if (fragment == null) {
            fragmentTransaction.add(R.id.fl_main_activity_container, resultFragment);
        } else {
            fragmentTransaction.replace(R.id.fl_main_activity_container, resultFragment);
        }
        fragmentTransaction.commit();
    }


    private boolean checkEmptyResult(List<DisplayableMovie> moviesList) {
        boolean isEmpty = false;
        if (moviesList.size() == 0) {
            noSearchResult();
            isEmpty = true;
        }
        return isEmpty;
    }

    private void errorDataLoad(final int loaderId) {
        Snackbar snackbar = getSnackbar(getString(R.string.error_load_data_message),
                Snackbar.LENGTH_INDEFINITE);
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
                        getSupportLoaderManager().restartLoader(loaderId, null, MainActivity.this);
                        break;
                    }
                    default: {
                        throw new RuntimeException(getString(R.string.error_loader_not_impl)
                                + loaderId);
                    }
                }
            }
        });
        snackbar.show();
    }

    @NonNull
    private Snackbar getSnackbar(String message, int lengthLong) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, lengthLong);
        View view = snackbar.getView();
        view.setLayoutParams(getLayoutParamsTopNavigation(view));
        return snackbar;
    }

    @NonNull
    private CoordinatorLayout.LayoutParams getLayoutParamsTopNavigation(View view) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                view.getLayoutParams();
        layoutParams.setAnchorId(R.id.bnv_main_activity_navigation);
        layoutParams.anchorGravity = Gravity.TOP;
        layoutParams.gravity = Gravity.TOP;
        return layoutParams;
    }

    private void noSearchResult() {
        final Snackbar snackbar = getSnackbar(getString(
                bottomNavigationView.getSelectedItemId() == R.id.menu_bt_nav_favorites
                        ? R.string.no_database_search_result
                        : R.string.no_network_search_result),
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
