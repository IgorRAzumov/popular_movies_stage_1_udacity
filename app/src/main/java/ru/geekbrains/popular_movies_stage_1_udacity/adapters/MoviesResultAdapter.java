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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MovieResult;
import ru.geekbrains.popular_movies_stage_1_udacity.data.RecyclerViewOnClickListener;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

public class MoviesResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieResult> moviesList;
    private final RecyclerViewOnClickListener recyclerViewOnClickListener;

    public MoviesResultAdapter(List<MovieResult> moviesList, RecyclerViewOnClickListener recyclerViewOnClickListener) {
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
        MovieResult movie = moviesList.get(position);
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

    public MovieResult getMovie(int position) {
        return moviesList.get(position);
    }

    public void setData(List<MovieResult> movies) {
        moviesList = movies;
        notifyDataSetChanged();
    }

    class MovieResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_card_movie_poster)
        ImageView moviePoster;
        @BindView(R.id.tv_card_movie_name)
        TextView movieName;
        @BindView(R.id.tv_card_movie_rating)
        TextView movieRating;


        MovieResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindPosterImage(Context context, String posterPath) {
            String posterUrl = context.getString(R.string.poster_movie_base_url_w185) + posterPath;
            NetworkUtils.loadPosterImage(context, moviePoster, posterUrl);
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
