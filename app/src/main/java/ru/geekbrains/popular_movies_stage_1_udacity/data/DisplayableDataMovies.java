package ru.geekbrains.popular_movies_stage_1_udacity.data;

import java.util.List;

public class DisplayableDataMovies {
    private final int pageNumber;
    private final int pageCount;
    private final List<DisplayableMovie> movies;

    public DisplayableDataMovies(int pageNumber, int pageCount,
                                 List<DisplayableMovie> movies) {
        this.pageNumber = pageNumber;
        this.pageCount = pageCount;
        this.movies = movies;
    }

    public DisplayableDataMovies(List<DisplayableMovie> results) {
        pageCount = 1;
        pageNumber = 1;
        movies = results;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<DisplayableMovie> getMovies() {
        return movies;
    }
}
