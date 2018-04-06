package ru.geekbrains.popular_movies_stage_1_udacity.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.ReviewResult;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieResultHolder> {
    private List<ReviewResult> reviewsResult;


    public MovieReviewsAdapter(List<ReviewResult> results) {
        reviewsResult = results;
    }

    @NonNull
    @Override
    public MovieResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.movie_review_card, parent,
                        false);
        return new MovieResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieResultHolder holder, int position) {
        ReviewResult review = reviewsResult.get(position);
        holder.bindReviewAuthor(review.getAuthor());
        holder.bindReviewContent(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewsResult == null
                ? 0
                : reviewsResult.size();
    }

    class MovieResultHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_movie_review_card_author)
        TextView reviewAuthor;
        @BindView(R.id.tv_movies_review_card_text_content)
        TextView reviewContent;

        MovieResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            reviewContent.setMovementMethod(new ScrollingMovementMethod());
        }

        void bindReviewAuthor(String author) {
            reviewAuthor.setText(author);
        }

        void bindReviewContent(String content) {
            reviewContent.append(content);
        }
    }
}
