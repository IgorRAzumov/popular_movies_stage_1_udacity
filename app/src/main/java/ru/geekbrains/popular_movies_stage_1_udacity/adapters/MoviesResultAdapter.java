package ru.geekbrains.popular_movies_stage_1_udacity.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.RecyclerViewOnClickListener;
import ru.geekbrains.popular_movies_stage_1_udacity.data.Result;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

public class MoviesResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Result> moviesList;
    private final RecyclerViewOnClickListener recyclerViewOnClickListener;

    public MoviesResultAdapter(List<Result> moviesList, RecyclerViewOnClickListener recyclerViewOnClickListener) {
        this.moviesList = moviesList;
        this.recyclerViewOnClickListener = recyclerViewOnClickListener;
    }

    @NonNull
    @Override
    public MovieResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Result movie = moviesList.get(position);
        if (holder instanceof MovieResultHolder) {
            MovieResultHolder movieResultHolder = (MovieResultHolder) holder;
            movieResultHolder.bindPosterImage(holder.itemView.getContext(), movie.getPosterPath());
            movieResultHolder.bindMovieTitle((movie.getTitle()));
            movieResultHolder.bindRating(String.valueOf(movie.getVoteAverage()));
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public Result getMovie(int position) {
        return moviesList.get(position);
    }

    private class MovieResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView moviePoster;
        private final TextView movieName;
        private final TextView movieRating;


        MovieResultHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.iv_card_movie_poster);
            movieName = itemView.findViewById(R.id.tv_card_movie_name);
            movieRating = itemView.findViewById(R.id.tv_card_movie_rating);

            itemView.setOnClickListener(this);
        }

        void bindPosterImage(Context context, String posterPath) {
            String posterUrl = context.getString(R.string.poster_movie_base_url_w185) + posterPath;
            NetworkUtils.LoadPosterImage(context, moviePoster, posterUrl);
        }

        void bindMovieTitle(String title) {
            movieName.setText(title);
        }


        void bindRating(String rating) {
            movieRating.setText(rating);
        }

        @Override
        public void onClick(View v) {
            recyclerViewOnClickListener.onItemRecyclerClick(getAdapterPosition());
        }
    }
}
