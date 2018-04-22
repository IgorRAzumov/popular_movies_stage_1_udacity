package ru.geekbrains.popular_movies_stage_1_udacity.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MoviesResultAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.Utils;
import ru.geekbrains.popular_movies_stage_1_udacity.widgets.SpacingItemDecoration;


public class MoviesResultFragment extends Fragment
        implements MoviesResultAdapter.RecycleViewOnItemClickListener {
    @BindView(R.id.rv_fragment_main_result)
    RecyclerView resultRecycler;

    private OnFragmentInteractionListener interactionListener;
    private MoviesResultAdapter moviesResultAdapter;
    private Unbinder unbinder;

    public MoviesResultFragment() {
    }

    public static MoviesResultFragment newInstance(Context context,
                                                   ArrayList<DisplayableMovie> movies) {
        MoviesResultFragment fragment = new MoviesResultFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(context.getString(R.string.movies_result_bundle_key), movies);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<DisplayableMovie> movieResults = getArguments()
                    .getParcelableArrayList(getString(R.string.movies_result_bundle_key));
            moviesResultAdapter = new MoviesResultAdapter(movieResults, this);
        } else {
            throw new RuntimeException(getString(R.string.error_fragment_instance_bundle)
                    + this.getClass().getName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        unbinder = ButterKnife.bind(this, view);

        initResultRecycler();
        if (savedInstanceState != null) {
            restoreResultRecyclerPosition(savedInstanceState);
        }
        return view;
    }


    private void initResultRecycler() {
        boolean isPortraitOrientation = getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        resultRecycler.setLayoutManager(new GridLayoutManager(getContext(),
                isPortraitOrientation
                        ? getResources().getInteger(R.integer.result_fragment_grid_span_count_portrait)
                        : getResources().getInteger(R.integer.result_fragment_grid_span_count_landscape)
        ));
        resultRecycler.setAdapter(moviesResultAdapter);
        resultRecycler.addItemDecoration(new SpacingItemDecoration(
                isPortraitOrientation
                        ? getResources().getInteger(R.integer.result_fragment_grid_span_count_portrait)
                        : getResources().getInteger(R.integer.result_fragment_grid_span_count_landscape),
                Utils.dpToPx(Objects.requireNonNull(getContext()),
                        getResources().getInteger(R.integer.result_fragment_recycler_item_decorator_spacing)),
                true));
        resultRecycler.setHasFixedSize(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.error_on_activity_implement_fragment_listener));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        RecyclerView.LayoutManager layoutManager = resultRecycler.getLayoutManager();
        if (resultRecycler != null && layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            outState.putInt(getString(R.string.result_fragment_rv_saved_position_bundle_key),
                    gridLayoutManager.findLastCompletelyVisibleItemPosition());
        }
        super.onSaveInstanceState(outState);
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

    @Override
    public void onItemResultRecyclerClick(DisplayableMovie movie, int moviePosition) {
        interactionListener.onMovieClick(movie, moviePosition);
    }

    @Override
    public void onFavoriteClick(DisplayableMovie movie) {
        interactionListener.onFavoriteClick(movie);
    }

    public void setData(List<DisplayableMovie> movies) {
        moviesResultAdapter.setData(movies);
    }

    public void clearData() {
        moviesResultAdapter.clearData();
        moviesResultAdapter.notifyDataSetChanged();
        /*resultRecycler.clearOnScrollListeners();
        resultRecycler.invalidate();*/
    }

    private void restoreResultRecyclerPosition(Bundle savedInstanceState) {
        int defaultSavedPosition = getResources()
                .getInteger(R.integer.result_fragment_rv_default_save_position);
        int savedPosition = savedInstanceState.getInt(
                getString(R.string.result_fragment_rv_saved_position_bundle_key),
                defaultSavedPosition);
        if (savedPosition != defaultSavedPosition) {
            resultRecycler.getLayoutManager().scrollToPosition(savedPosition);
        }
    }

    public void deleteMovieFromFavorite(DisplayableMovie displayableMovie, boolean isFromFavorite) {
        moviesResultAdapter.deleteMovie(displayableMovie, isFromFavorite);
    }

    public void addMovieToFavorite(DisplayableMovie displayableMovie) {
        moviesResultAdapter.addMovieFromFavorite(displayableMovie);
    }

    public void updateMovieFavoriteStatus(int position, boolean isFromFavorite) {
        moviesResultAdapter.updateMovieFavoriteStatus(position, isFromFavorite);
    }

    public interface OnFragmentInteractionListener {
        void onMovieClick(DisplayableMovie movie, int moviePosition);

        void onFavoriteClick(DisplayableMovie movie);
    }
}
