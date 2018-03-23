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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.adapters.MoviesResultAdapter;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;
import ru.geekbrains.popular_movies_stage_1_udacity.data.RecyclerViewOnClickListener;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.Utils;
import ru.geekbrains.popular_movies_stage_1_udacity.widgets.SpacingItemDecoration;


public class ResultFragment extends Fragment implements RecyclerViewOnClickListener {
    private OnFragmentInteractionListener interactionListener;
    private MoviesResultAdapter moviesResultAdapter;

    @BindView(R.id.rv_fragment_main_result)
    RecyclerView resultRecycler;

    private Unbinder unbinder;

    public ResultFragment() {
    }

    public static ResultFragment newInstance(Context context, MoviesResponse moviesResponse) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(context.getString(R.string.movies_response_bundle_key), moviesResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MoviesResponse moviesResponse = getArguments()
                    .getParcelable(getString(R.string.movies_response_bundle_key));
            assert moviesResponse != null;
            moviesResultAdapter = new MoviesResultAdapter(moviesResponse.getResults(),//we are checked moviesResponse.getResults() before put it bundle
                    this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        unbinder = ButterKnife.bind(this, view);
        initResultRecycler();

        if (savedInstanceState != null) {
            int defaultSavedPosition = getResources()
                    .getInteger(R.integer.result_fragment_default_save_position);
            int savedPosition = savedInstanceState.getInt(
                    getString(R.string.list_result_saved_position_position_bundle_key),
                    defaultSavedPosition);
            if (savedPosition != defaultSavedPosition) {
                resultRecycler.getLayoutManager().scrollToPosition(savedPosition);
            }
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
                Utils.dpToPx(getContext(),
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
            outState.putInt(getString(R.string.list_result_saved_position_position_bundle_key),
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
    public void onItemRecyclerClick(int position) {
        interactionListener.onMovieClick(moviesResultAdapter.getMovie(position));
    }

    public interface OnFragmentInteractionListener {
        void onMovieClick(MovieResult movie);
    }
}
