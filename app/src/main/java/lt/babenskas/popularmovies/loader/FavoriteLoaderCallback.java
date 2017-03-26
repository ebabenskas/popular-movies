package lt.babenskas.popularmovies.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import lt.babenskas.popularmovies.MovieDetailActivity;
import lt.babenskas.popularmovies.model.api.Movie;

public class FavoriteLoaderCallback implements LoaderManager.LoaderCallbacks<Movie> {
    private final MovieDetailActivity mActivity;
    private final FavoriteActivityCallback mFavoriteActivityCallback;

    public FavoriteLoaderCallback(MovieDetailActivity activity) {
        this.mActivity = activity;
        this.mFavoriteActivityCallback = activity;
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new FavoriteTaskLoader(mActivity, args);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mFavoriteActivityCallback.updateFavoriteMovie(data);
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
    }

    public interface FavoriteActivityCallback {
        void updateFavoriteMovie(Movie m);
    }
}
