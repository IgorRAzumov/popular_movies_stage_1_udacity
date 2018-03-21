package ru.geekbrains.popular_movies_stage_1_udacity.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.geekbrains.popular_movies_stage_1_udacity.data.MoviesResponse;


public interface IPopularMoviesApi {
    @GET("movie/{sortBy}")
    Call<MoviesResponse> getMovies(@Path("sortBy") String sortBy, @Query("api_key") String apiKey,
                                   @Query("language") String language);
}
