package com.akat.filmreel.util;

import android.content.Context;

import com.akat.filmreel.data.Repository;
import com.akat.filmreel.data.db.AppDatabase;
import com.akat.filmreel.data.network.ApiManager;
import com.akat.filmreel.data.network.NetworkDataSource;
import com.akat.filmreel.ui.movieDetail.MovieDetailViewModelFactory;
import com.akat.filmreel.ui.movieList.MovieListViewModelFactory;

public class InjectorUtils {

    private static Repository provideRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        ApiManager manager = ApiManager.getInstance();
        NetworkDataSource networkDataSource =
                NetworkDataSource.getInstance(executors, manager);
        return Repository.getInstance(
                database.topRatedDao(),
                database.bookmarksDao(),
                networkDataSource,
                executors
        );
    }

    public static MovieListViewModelFactory provideMovieListViewModelFactory(Context context) {
        Repository repository = provideRepository(context);
        return new MovieListViewModelFactory(repository);
    }

    public static MovieDetailViewModelFactory provideMovieDetailViewModelFactory(Context context, long movieId) {
        Repository repository = provideRepository(context);
        return new MovieDetailViewModelFactory(repository, movieId);
    }

}
