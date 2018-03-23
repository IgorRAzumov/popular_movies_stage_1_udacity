package ru.geekbrains.popular_movies_stage_1_udacity.data;


public class MovieAdditionalInfoResponse {
    private ReviewsResponse reviewsResponse;
    private MovieVideosResponse movieVideosResponse;

    public MovieAdditionalInfoResponse(ReviewsResponse reviewsResponse,
                                       MovieVideosResponse movieVideosResponse) {
        this.reviewsResponse = reviewsResponse;
        this.movieVideosResponse = movieVideosResponse;
    }

    public MovieAdditionalInfoResponse(ReviewsResponse reviewsResponse) {
        this.reviewsResponse = reviewsResponse;
    }

    public MovieAdditionalInfoResponse(MovieVideosResponse movieVideosResponse) {
        this.movieVideosResponse = movieVideosResponse;
    }

    public ReviewsResponse getReviewsResponse() {
        return reviewsResponse;
    }

    public MovieVideosResponse getMovieVideosResponse() {
        return movieVideosResponse;
    }
}
