package lt.babenskas.popularmovies.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import lt.babenskas.popularmovies.model.api.VideosResponse;
import lt.babenskas.popularmovies.service.TheMovieDbService;

public class VideoTaskLoader extends AsyncTaskLoader<VideosResponse> {
    private VideosResponse mVideosResponse;
    private Bundle mArgs;
    public static final String MOVIE_ID = "movie_id";

    public VideoTaskLoader(Context context, Bundle args) {
        super(context);
        this.mArgs = args;

    }

    @Override
    protected void onStartLoading() {
        if (mArgs == null) {
            return;
        }
        if (mVideosResponse != null) {
            deliverResult(mVideosResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    public VideosResponse loadInBackground() {
        int movieId = mArgs.getInt(MOVIE_ID);
        try {
            mVideosResponse = TheMovieDbService.getInstance().getVideos(movieId);
            return mVideosResponse;
        } catch (IOException e) {
            return null;
        }
    }
}
