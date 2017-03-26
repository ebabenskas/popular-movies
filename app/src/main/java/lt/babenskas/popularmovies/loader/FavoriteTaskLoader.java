package lt.babenskas.popularmovies.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.service.FavoriteMovieDbService;

public class FavoriteTaskLoader extends AsyncTaskLoader<Movie> {
    private Movie mMovie;
    private Bundle mArgs;
    public static final String MOVIE = "movie";
    public static final String CHANGE = "change";

    public FavoriteTaskLoader(Context context, Bundle args) {
        super(context);
        this.mArgs = args;

    }

    @Override
    protected void onStartLoading() {
        if (mArgs == null) {
            return;
        }
        if (mMovie != null) {
            deliverResult(mMovie);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie loadInBackground() {
        mMovie = mArgs.getParcelable(MOVIE);
        boolean change = mArgs.getBoolean(CHANGE, false);
        if (mMovie == null)
            return null;
        mMovie.setFavorite(new FavoriteMovieDbService(getContext().getContentResolver()).checkIsFavorite(mMovie.getId()));
        if (!change)
            return mMovie;
        if (mMovie.isFavorite())
            new FavoriteMovieDbService(getContext().getContentResolver()).deleteFromFavorite(mMovie.getId());
        else
            new FavoriteMovieDbService(getContext().getContentResolver()).addToFavorite(mMovie);
        mMovie.setFavorite(!mMovie.isFavorite());
        return mMovie;
    }
}
