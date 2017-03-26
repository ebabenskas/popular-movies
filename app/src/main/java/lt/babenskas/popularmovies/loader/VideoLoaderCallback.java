package lt.babenskas.popularmovies.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

import java.util.ArrayList;

import lt.babenskas.popularmovies.MovieDetailActivity;
import lt.babenskas.popularmovies.R;
import lt.babenskas.popularmovies.model.api.Video;
import lt.babenskas.popularmovies.model.api.VideosResponse;

public class VideoLoaderCallback implements LoaderManager.LoaderCallbacks<VideosResponse> {
    private final MovieDetailActivity mActivity;
    private final VideoActivityCallback mVideoActivityCallback;

    public VideoLoaderCallback(MovieDetailActivity activity) {
        this.mActivity = activity;
        this.mVideoActivityCallback = activity;
    }

    @Override
    public Loader<VideosResponse> onCreateLoader(int id, final Bundle args) {
        return new VideoTaskLoader(mActivity, args);
    }

    @Override
    public void onLoadFinished(Loader<VideosResponse> loader, VideosResponse data) {
        if (data == null) {
            Toast.makeText(mActivity, R.string.error_get_movies, Toast.LENGTH_LONG).show();
            return;
        }
        mVideoActivityCallback.updateVideo(data.getVideos());
    }

    @Override
    public void onLoaderReset(Loader<VideosResponse> loader) {
    }

    public interface VideoActivityCallback {
        void updateVideo(ArrayList<Video> videos);
    }
}
