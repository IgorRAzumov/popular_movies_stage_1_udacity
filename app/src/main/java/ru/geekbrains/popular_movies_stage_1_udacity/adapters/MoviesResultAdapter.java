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
import ru.geekbrains.popular_movies_stage_1_udacity.data.DisplayableMovie;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;

public class MoviesResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecycleViewOnItemClickListener recyclerViewOnClickListener;
    private List<DisplayableMovie> moviesList;

    public MoviesResultAdapter(List<DisplayableMovie> moviesList,
                               RecycleViewOnItemClickListener recyclerViewOnClickListener) {
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
        DisplayableMovie movie = moviesList.get(position);
        if (holder instanceof MovieResultHolder) {
            MovieResultHolder movieResultHolder = (MovieResultHolder) holder;
            movieResultHolder.bindPosterImage(holder.itemView.getContext(),
                    movie.getMoviePosterPath());
            movieResultHolder.bindMovieTitle((movie.getMovieName()));
            movieResultHolder.bindRating(String.valueOf(movie.getMovieRating()));
            movieResultHolder.bindFavorite(movie.isFavorite());
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    public void setData(List<DisplayableMovie> movies) {
        moviesList.clear();
        moviesList.addAll(movies);
        notifyDataSetChanged();
    }

    public void deleteMovie(DisplayableMovie movie, boolean isFromFavorite) {
        int position = moviesList.indexOf(movie);
        if (isFromFavorite) {
            moviesList.remove(position);
            notifyItemRemoved(position);
        } else {
           // moviesList.get(position).setFavorite(false);
            movie.setFavorite(false);
            notifyItemChanged(position);
        }
    }

    public void addMovieFromFavorite(DisplayableMovie movie) {
        int position = moviesList.indexOf(movie);
      movie.setFavorite(true);
       // moviesList.get(position).setFavorite(true);
        notifyItemChanged(position);
    }

    public void updateFavoriteStatus(DisplayableMovie movie, boolean isFavorite) {
        int position = moviesList.indexOf(movie);

        if (isFavorite != moviesList.get(position).isFavorite()) {
            moviesList.get(position).setFavorite(isFavorite);
            notifyItemChanged(position);
        }
    }

    public interface RecycleViewOnItemClickListener {
        void onItemResultRecyclerClick(DisplayableMovie movie);

        void onFavoriteClick(DisplayableMovie movie);
    }

    class MovieResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_card_movie_favorite)
        ImageView isFavoriteImage;
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
            isFavoriteImage.setOnClickListener(this);
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
            if (v.getId() == R.id.iv_card_movie_favorite) {
                //    bindFavorite((int) isFavoriteImage.getTag() == R.drawable.ic_add_favorite);
                recyclerViewOnClickListener.onFavoriteClick(moviesList.get(getAdapterPosition()));
            } else {
                recyclerViewOnClickListener.onItemResultRecyclerClick(
                        moviesList.get(getAdapterPosition()));
            }
        }

        public void bindFavorite(boolean isFavorite) {
            if (isFavorite) {
                isFavoriteImage.setImageResource(R.drawable.ic_remove_favorite);
                isFavoriteImage.setTag(R.drawable.ic_remove_favorite);
            } else {
                isFavoriteImage.setImageResource(R.drawable.ic_add_favorite);
                isFavoriteImage.setTag(R.drawable.ic_add_favorite);
            }
        }

    }
}
