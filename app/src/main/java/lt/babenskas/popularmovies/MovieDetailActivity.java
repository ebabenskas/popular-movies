package lt.babenskas.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import lt.babenskas.popularmovies.adapter.VideosRecyclerViewAdapter;
import lt.babenskas.popularmovies.databinding.ActivityMovieDetailBinding;
import lt.babenskas.popularmovies.databinding.ReviewListContentBinding;
import lt.babenskas.popularmovies.loader.FavoriteLoaderCallback;
import lt.babenskas.popularmovies.loader.FavoriteTaskLoader;
import lt.babenskas.popularmovies.loader.ReviewLoaderCallback;
import lt.babenskas.popularmovies.loader.ReviewTaskLoader;
import lt.babenskas.popularmovies.loader.VideoLoaderCallback;
import lt.babenskas.popularmovies.loader.VideoTaskLoader;
import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.model.api.Review;
import lt.babenskas.popularmovies.model.api.Video;
import lt.babenskas.popularmovies.util.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements  VideosRecyclerViewAdapter.VideoRecyclerViewAdapterOnClickHandler,
        ReviewLoaderCallback.ReviewActivityCallback, VideoLoaderCallback.VideoActivityCallback, FavoriteLoaderCallback.FavoriteActivityCallback {
    private static final String HTTP_IMAGE_TMDB_ORG_T_P_W185 = "http://image.tmdb.org/t/p/w185/";
    private static final String HTTP_IMAGE_TMDB_ORG_T_P_W500 = "http://image.tmdb.org/t/p/w500/";
    private static final String STATE_MOVIES = "movie";
    private static final String STATE_REVIEWS = "reviews";
    private static final String STATE_VIDEOS = "videos";
    private static final int VIDEOS_GET_LOADER = 33;
    private static final int REVIEWS_GET_LOADER = 44;
    private static final int FAVORITE_GET_LOADER = 55;
    public static final String MOVIE_KEY = "movie";

    private Movie mMovie;
    private ArrayList<Review> mReviews;
    private ArrayList<Video> mVideos;

    private ReviewLoaderCallback mReviewLoaderCallback;
    private VideoLoaderCallback mVideoResultLoaderListener;
    private FavoriteLoaderCallback mFavoriteResultLoaderListener;
    private VideosRecyclerViewAdapter mVideosRecyclerViewAdapter;
    private ActivityMovieDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        setSupportActionBar(mDetailBinding.tTitleBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mMovie = getMovie(savedInstanceState);
        mDetailBinding.tvDetailMovieYear.setText(mMovie.getReleaseDate());
        mDetailBinding.tvDetailMovieLenght.setText(String.format(getCurrentLocale(), getString(R.string.rating), mMovie.getVoteAverage()));
        mDetailBinding.tvDetailMovieVotes.setText(String.format(getCurrentLocale(), getString(R.string.votes), mMovie.getVoteCount()));
        actionBar.setTitle(mMovie.getTitle());
        mDetailBinding.tvDetailMovieInfo.setText(mMovie.getOverview());
        Picasso.with(this).load(HTTP_IMAGE_TMDB_ORG_T_P_W185 + mMovie.getPosterPath()).into(mDetailBinding.ivDetailMovieImage);
        Picasso.with(this).load(HTTP_IMAGE_TMDB_ORG_T_P_W500 + mMovie.getBackdropPath()).into(mDetailBinding.ivDetailMovieTopImage);
        mVideosRecyclerViewAdapter = new VideosRecyclerViewAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDetailBinding.rvVideoList.setAdapter(mVideosRecyclerViewAdapter);
        mDetailBinding.rvVideoList.setLayoutManager(linearLayoutManager);
        mReviewLoaderCallback = new ReviewLoaderCallback(this);
        mVideoResultLoaderListener = new VideoLoaderCallback(this);
        mFavoriteResultLoaderListener = new FavoriteLoaderCallback(this);
        mDetailBinding.fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFavoriteLoader(mMovie, true);
            }
        });
        mDetailBinding.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideos != null && mVideos.size() > 0) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_trailer_of) + mMovie.getTitle());
                    i.putExtra(Intent.EXTRA_TEXT, mVideos.get(0).getYoutubeWebUrl());
                    startActivity(Intent.createChooser(i, getString(R.string.share_trailer)));
                }
            }
        });
        initExternalData(savedInstanceState);

    }

    private void initExternalData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailActivity.STATE_VIDEOS)){
            mVideos = savedInstanceState.getParcelableArrayList(MovieDetailActivity.STATE_VIDEOS);
            updateVideo(mVideos);
        } else
            startVideoLoader(mMovie.getId());
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailActivity.STATE_REVIEWS)){
            mReviews = savedInstanceState.getParcelableArrayList(MovieDetailActivity.STATE_REVIEWS);
            updateReview(mReviews);
        } else
            startReviewLoader(mMovie.getId());
        startFavoriteLoader(mMovie, false);
    }


    private Movie getMovie(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailActivity.STATE_MOVIES)) {
            return savedInstanceState.getParcelable(MovieDetailActivity.STATE_MOVIES);
        }
        return getIntent().getParcelableExtra(MovieDetailActivity.MOVIE_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This is needed, because while click home buton, navigation recreates parent activity instead onResume().
            // When click hardware back button, this work correctly
            //
            Intent h = NavUtils.getParentActivityIntent(this);
            h.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, h);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(STATE_MOVIES, mMovie);
        savedInstanceState.putParcelableArrayList(STATE_REVIEWS, mReviews);
        savedInstanceState.putParcelableArrayList(STATE_VIDEOS, mVideos);
        super.onSaveInstanceState(savedInstanceState);
    }


    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            return getResources().getConfiguration().locale;
        }
    }

    private void startReviewLoader(Integer movieId) {
        if (!NetworkUtils.isNetworkAvailable(this))
            return;
        Bundle bundle = new Bundle();
        bundle.putInt(ReviewTaskLoader.MOVIE_ID, movieId);
        startLoader(REVIEWS_GET_LOADER, bundle, mReviewLoaderCallback);
    }


    private void startVideoLoader(int movieId) {
        if (!NetworkUtils.isNetworkAvailable(this))
            return;
        Bundle bundle = new Bundle();
        bundle.putInt(VideoTaskLoader.MOVIE_ID, movieId);
        startLoader(VIDEOS_GET_LOADER, bundle, mVideoResultLoaderListener);
    }

    private void startFavoriteLoader(Movie m, boolean change) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(FavoriteTaskLoader.MOVIE, m);
        bundle.putBoolean(FavoriteTaskLoader.CHANGE, change);
        startLoader(FAVORITE_GET_LOADER, bundle, mFavoriteResultLoaderListener);
    }

    public void openYoutubeVideo(Video v) {
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(v.getYoutubeVndUrl()));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(v.getYoutubeWebUrl()));
        try {
            startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(browserIntent);
        }
    }

    @Override
    public void onClick(Video item) {
        openYoutubeVideo(item);
    }

    private void startLoader(int loaderId, Bundle bundle, LoaderManager.LoaderCallbacks callbacks) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(loaderId);
        if (loader == null) {
            loaderManager.initLoader(loaderId, bundle, callbacks);
        } else {
            loaderManager.restartLoader(loaderId, bundle, callbacks);
        }
    }

    @Override
    public void updateReview(ArrayList<Review> reviews) {
        if (reviews == null) {
            return;
        }
        mReviews = reviews;
        mDetailBinding.llReviewContainer.removeAllViews();
        for (Review review : mReviews) {
            final ReviewListContentBinding reviewListContentBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.review_list_content,  mDetailBinding.llReviewContainer, false);
            reviewListContentBinding.tvReviewContent.setText(review.getContent());
            reviewListContentBinding.tvReviewAuthor.setText(review.getAuthor());
            reviewListContentBinding.tvReviewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewListContentBinding.tvReviewContent.toggle();
                }
            });
            mDetailBinding.llReviewContainer.addView(reviewListContentBinding.getRoot());
        }
    }

    private void showSnackNotificationWhenOffline(){
        if (mMovie.isFavorite() && !NetworkUtils.isNetworkAvailable(this)) {
            final Snackbar snackbar = Snackbar.make(mDetailBinding.clMovieDetail, R.string.no_internet_allow, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }
    }

    @Override
    public void updateVideo(ArrayList<Video> videos) {
        if (videos == null)
            return;
        mVideos = videos;
        mVideosRecyclerViewAdapter.append(videos);
        if (mVideos.isEmpty())
            mDetailBinding.ibShare.setVisibility(View.GONE);
    }

    @Override
    public void updateFavoriteMovie(Movie m) {
        mMovie.setFavorite(m.isFavorite());
        initFabIcon();
    }

    private void initFabIcon() {
        if (mMovie.isFavorite()) {
            mDetailBinding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_fill));
        } else {
            mDetailBinding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_empty));
        }
        showSnackNotificationWhenOffline();
    }
}