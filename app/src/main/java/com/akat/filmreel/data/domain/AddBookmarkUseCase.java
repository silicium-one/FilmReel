package com.akat.filmreel.data.domain;

import io.reactivex.Completable;

public class AddBookmarkUseCase {

    private final Repository repository;

    public AddBookmarkUseCase(Repository repository) {
        this.repository = repository;
    }

    public Completable setBookmark(long movieId, boolean oldState) {
        if (oldState) {
            return repository.removeBookmark(movieId);
        } else {
            return repository.setBookmark(movieId);
        }
    }
}
