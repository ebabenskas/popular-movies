package lt.babenskas.popularmovies.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.service.TheMovieDbService;

public class MoviesTaskLoader extends AsyncTaskLoader<MoviesRequest> {
    private MoviesRequest moviesRequest;
    private Bundle args;
    public static final String PAGE_SIZE = "page";
    public static final String DB_REQUEST_TYPE = "type";

    public MoviesTaskLoader(Context context, Bundle args) {
        super(context);
        this.args = args;

    }

    @Override
    protected void onStartLoading() {
        if (args == null) {
            return;
        }
        if (moviesRequest != null) {
            deliverResult(moviesRequest);
        } else {
            forceLoad();
        }
    }

    @Override
    public MoviesRequest loadInBackground() {
        int page = args.getInt(PAGE_SIZE);
        TheMovieDbService.MovieDbRequestType movieDbRequestType = TheMovieDbService.MovieDbRequestType.valueOf(args.getString(DB_REQUEST_TYPE));
        try {
            moviesRequest = TheMovieDbService.getInstance().getMovies(movieDbRequestType, page);
            return moviesRequest;
        } catch (IOException e) {
            return null;
        }
    }
}
