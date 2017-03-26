package lt.babenskas.popularmovies.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.service.FavoriteMovieDbService;
import lt.babenskas.popularmovies.service.TheMovieDbService;

public class MoviesTaskLoader extends AsyncTaskLoader<MoviesRequest> {
    private MoviesRequest mMoviesRequest;
    private Bundle mArgs;
    public static final String PAGE_SIZE = "page";
    public static final String DB_REQUEST_TYPE = "type";

    public MoviesTaskLoader(Context context, Bundle args) {
        super(context);
        this.mArgs = args;

    }

    @Override
    protected void onStartLoading() {
        if (mArgs == null) {
            return;
        }
        if (mMoviesRequest != null) {
            deliverResult(mMoviesRequest);
        } else {
            forceLoad();
        }
    }

    @Override
    public MoviesRequest loadInBackground() {
        int page = mArgs.getInt(PAGE_SIZE);
        TheMovieDbService.MovieDbRequestType movieDbRequestType = TheMovieDbService.MovieDbRequestType.valueOf(mArgs.getString(DB_REQUEST_TYPE));
        try {
            if (movieDbRequestType.equals(TheMovieDbService.MovieDbRequestType.FAVORITES))
                mMoviesRequest = new FavoriteMovieDbService(getContext().getContentResolver()).getFavoriteMovies();
            else {
                mMoviesRequest = TheMovieDbService.getInstance().getMovies(movieDbRequestType, page);
                new FavoriteMovieDbService(getContext().getContentResolver()).checkFavorites(mMoviesRequest);
            }
            return mMoviesRequest;
        } catch (IOException e) {
            return null;
        }
    }
}
