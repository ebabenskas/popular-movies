package lt.babenskas.popularmovies.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.ArrayList;

import lt.babenskas.popularmovies.MovieDetailActivity;
import lt.babenskas.popularmovies.model.api.Review;
import lt.babenskas.popularmovies.model.api.ReviewResponse;

public class ReviewLoaderCallback implements LoaderManager.LoaderCallbacks<ReviewResponse> {
    private final MovieDetailActivity mActivity;
    private final ReviewActivityCallback mActivityCallback;

    public ReviewLoaderCallback(MovieDetailActivity activity) {
        this.mActivity = activity;
        this.mActivityCallback = activity;
    }

    @Override
    public Loader<ReviewResponse> onCreateLoader(int id, final Bundle args) {
        return new ReviewTaskLoader(mActivity, args);
    }

    @Override
    public void onLoadFinished(Loader<ReviewResponse> loader, ReviewResponse data) {
        mActivityCallback.updateReview(data.getReviews());
    }

    @Override
    public void onLoaderReset(Loader<ReviewResponse> loader) {
    }

    public interface ReviewActivityCallback {
        void updateReview(ArrayList<Review> reviews);
    }

}
