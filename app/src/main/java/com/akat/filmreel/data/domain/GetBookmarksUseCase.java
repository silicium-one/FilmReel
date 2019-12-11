package com.akat.filmreel.data.domain;

import com.akat.filmreel.data.model.Movie;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

@Singleton
public class GetBookmarksUseCase {

    private final Repository repository;

    @Inject
    GetBookmarksUseCase(MovieRepository repository) {
        this.repository = repository;
    }

    public Flowable<List<Movie>> observeBookmarked() {
        return repository.getBookmarkedMovies();
    }

}
