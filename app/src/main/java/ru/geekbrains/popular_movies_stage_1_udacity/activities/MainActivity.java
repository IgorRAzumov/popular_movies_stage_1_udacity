package ru.geekbrains.popular_movies_stage_1_udacity.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.ref.WeakReference;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.asyncTaskLoaders.MoviesAsyncTask;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.Result;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.ProgressBarFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.fragments.ResultFragment;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.PrefUtils;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
import static ru.geekbrains.popular_movies_stage_1_udacity.activities.MainActivity.ResultActivityHandler.COMPLETE_DATA_LOAD_HANDLER_CODE;
import static ru.geekbrains.popular_movies_stage_1_udacity.activities.MainActivity.ResultActivityHandler.ERROR_DATA_LOAD_HANDLER_CODE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<MoviesResponse>,
        ResultFragment.OnFragmentInteractionListener {
    private String language;
    private ResultActivityHandler resultActivityHandler;

    private Spinner menuSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        language = Resources.getSystem().getConfiguration().locale.getLanguage();
        resultActivityHandler = new ResultActivityHandler(new WeakReference<>(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_main_activity_container);
        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fl_main_activity_container, new ProgressBarFragment())
                    .setTransition(TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

        initLoader();
    }

    private void initLoader() {
        getSupportLoaderManager().initLoader(getResources().getInteger(R.integer.movies_async_task_loader_id),
                getDefaultLoaderBundle(), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        MenuItem item = menu.findItem(R.id.action_switch_sort_by_spinner);


        menuSpinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.main_activity_menu_spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(PrefUtils.readSortBySpinnerPositionFromSharedPref(this),
                false);
        menuSpinner.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.sort_by_movies_bundle_key), getSortByParam(position));
                bundle.putString(getString(R.string.language_bundle_key), language);
                getSupportLoaderManager()
                        .restartLoader(getResources().getInteger(R.integer.movies_async_task_loader_id),
                                bundle, MainActivity.this);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fl_main_activity_container, new ProgressBarFragment())
                        .setTransition(TRANSIT_FRAGMENT_FADE)
                        .commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuSpinner != null) {
            int position = PrefUtils.readSortBySpinnerPositionFromSharedPref(this);
            if (position != menuSpinner.getSelectedItemPosition()) {
                menuSpinner.setSelection(position, false);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (menuSpinner != null) {
            PrefUtils.writeSortBySpinnerPositionToSharedPref(this,
                    menuSpinner.getSelectedItemPosition());
        }
    }

    private String getSortByParam(int sortByPosition) {
        String sortBy;
        if (sortByPosition == getResources()
                .getInteger(R.integer.movies_request_param_sort_by_most_popular_index_array)) {
            sortBy = getString(R.string.movies_request_param_sort_by_most_popular);
        } else {
            sortBy = getString(R.string.movies_request_param_sort_by_top_rated);
        }
        return sortBy;
    }

    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    @Override
    public Loader<MoviesResponse> onCreateLoader(int id, Bundle args) {
        if (id == getResources().getInteger(R.integer.movies_async_task_loader_id)) {
            return new MoviesAsyncTask(this, args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MoviesResponse> loader, MoviesResponse data) {
        Message message;
        if (data != null && data.getResults() != null) {
            message = resultActivityHandler
                    .obtainMessage(COMPLETE_DATA_LOAD_HANDLER_CODE, data);
        } else {
            message = resultActivityHandler
                    .obtainMessage(ERROR_DATA_LOAD_HANDLER_CODE);
        }
        resultActivityHandler.sendMessage(message);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MoviesResponse> loader) {

    }

    private void completeDataLoad(MoviesResponse moviesResponse) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_main_activity_container);

        if (fragment != null) {
            if (fragment instanceof ProgressBarFragment) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fl_main_activity_container,
                                ResultFragment.newInstance(this, moviesResponse))
                        .setTransition(TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        } else {
            fragmentManager.beginTransaction()
                    .add(R.id.fl_main_activity_container,
                            ResultFragment.newInstance(this, moviesResponse))
                    .setTransition(TRANSIT_FRAGMENT_FADE)
                    .commit();
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
                        .getLoader(getResources().getInteger(R.integer.movies_async_task_loader_id));
                if (loader == null) {
                    initLoader();
                } else {
                    getSupportLoaderManager().restartLoader(getResources()
                                    .getInteger(R.integer.movies_async_task_loader_id),
                            getDefaultLoaderBundle(), MainActivity.this);
                }
            }
        });
        snackbar.show();
    }

    private Bundle getDefaultLoaderBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.sort_by_movies_bundle_key), getSortByParam(
                PrefUtils.readSortBySpinnerPositionFromSharedPref(MainActivity.this)));
        bundle.putString(getString(R.string.language_bundle_key), language);
        return bundle;
    }

    @Override
    public void onMovieClick(Result movie) {
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra(getString(R.string.movie_intent_key), (Parcelable) movie);
        startActivity(intent);
    }

    static class ResultActivityHandler extends Handler {
        static final int COMPLETE_DATA_LOAD_HANDLER_CODE = 222;
        static final int ERROR_DATA_LOAD_HANDLER_CODE = 333;

        private final WeakReference<MainActivity> reference;

        private ResultActivityHandler(WeakReference<MainActivity> reference) {
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE_DATA_LOAD_HANDLER_CODE: {
                    MainActivity mainActivity = reference.get();
                    if (mainActivity != null) {
                        mainActivity.completeDataLoad((MoviesResponse) msg.obj);
                    }
                    break;
                }
                case ERROR_DATA_LOAD_HANDLER_CODE: {
                    MainActivity mainActivity = reference.get();
                    if (mainActivity != null) {
                        mainActivity.errorDataLoad();
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

