package lt.babenskas.popularmovies.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import lt.babenskas.popularmovies.model.api.ReviewResponse;
import lt.babenskas.popularmovies.service.TheMovieDbService;

public class ReviewTaskLoader extends AsyncTaskLoader<ReviewResponse> {
    private ReviewResponse mResponse;
    private Bundle mArgs;
    public static final String MOVIE_ID = "movie_id";

    public ReviewTaskLoader(Context context, Bundle args) {
        super(context);
        this.mArgs = args;

    }

    @Override
    protected void onStartLoading() {
        if (mArgs == null) {
            return;
        }
        if (mResponse != null) {
            deliverResult(mResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    public ReviewResponse loadInBackground() {
        int movieId = mArgs.getInt(MOVIE_ID);
        try {
            mResponse = TheMovieDbService.getInstance().getReviews(movieId);
            return mResponse;
        } catch (IOException e) {
            return null;
        }
    }
}
