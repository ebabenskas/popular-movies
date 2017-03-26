package lt.babenskas.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import lt.babenskas.popularmovies.adapter.MoviesRecyclerViewAdapter;
import lt.babenskas.popularmovies.databinding.ActivityMovieListBinding;
import lt.babenskas.popularmovies.loader.MoviesTaskLoader;
import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.service.TheMovieDbService;
import lt.babenskas.popularmovies.util.NetworkUtils;

public class MovieListActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.MoviesRecyclerViewAdapterOnClickHandler, LoaderManager.LoaderCallbacks<MoviesRequest> {
    private static final String TAG = "MovieListActivity";
    private static final String MOVIES_KEY = "movies";
    private static final String PAGE_SIZE = "page";
    private static final String DB_REQUEST_TYPE = "type";
    private static final int MOVIES_GET_LOADER = 22;

    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private Boolean mLoading = false;
    private ActivityMovieListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_list);
        setSupportActionBar(mBinding.tTitleBar);
        mBinding.tTitleBar.setTitle(getTitle());
        mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(this);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mBinding.rcMovieList.setLayoutManager(gridLayoutManager);
        mBinding.rcMovieList.setAdapter(mMoviesRecyclerViewAdapter);
        if (existSavedState(savedInstanceState)) {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MovieListActivity.MOVIES_KEY);
            int page = savedInstanceState.getInt(PAGE_SIZE);
            TheMovieDbService.MovieDbRequestType movieDbRequestType = TheMovieDbService.MovieDbRequestType.valueOf(savedInstanceState.getString(DB_REQUEST_TYPE));
            mMoviesRecyclerViewAdapter.append(movies);
            mMoviesRecyclerViewAdapter.setCurrentPage(page);
            mMoviesRecyclerViewAdapter.setDbRequestType(movieDbRequestType);
        } else {
            if (!NetworkUtils.isNetworkAvailable(this))
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.FAVORITES);
            showProgressBar();
            requestMoviesWithLoader(mMoviesRecyclerViewAdapter.getDbRequestType(), 1);
        }
        //       when bottom is reached, then more items are loaded.
        mBinding.rcMovieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                // if not scrolling or mLoading flag is true, do nothing.
                if (dy == 0 || mLoading || mMoviesRecyclerViewAdapter.getDbRequestType().isFavorites())
                    return;
                int pastVisiblesItems, visibleItemCount, totalItemCount;
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount() - calculateNoOfColumns(MovieListActivity.this);
                pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    requestMoviesWithLoader(mMoviesRecyclerViewAdapter.getDbRequestType(), mMoviesRecyclerViewAdapter.getCurrentPage() + 1);
                }
            }
        });
    }

    private void requestMoviesWithLoader(final TheMovieDbService.MovieDbRequestType type, final int page) {
        if (!mMoviesRecyclerViewAdapter.getDbRequestType().isFavorites() && !NetworkUtils.isNetworkAvailable(this)) {
            final Snackbar snackbar = Snackbar.make(mBinding.clActivityList, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            requestMoviesWithLoader(type, page);
                        }
                    });

            snackbar.show();
            return;
        }
        showSnackNotificationWhenOfflineFavorite();
        Log.d(TAG, "Sent request to movie service. Params: MovieDbRequestType - " + type + "; page -" + page);
        mLoading = true;
        Bundle bundle = new Bundle();
        bundle.putInt(MoviesTaskLoader.PAGE_SIZE, page);
        bundle.putString(MoviesTaskLoader.DB_REQUEST_TYPE, type.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIES_GET_LOADER);
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIES_GET_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_GET_LOADER, bundle, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(MovieListActivity.MOVIES_KEY, mMoviesRecyclerViewAdapter.getValues());
        savedInstanceState.putInt(MovieListActivity.PAGE_SIZE, mMoviesRecyclerViewAdapter.getCurrentPage());
        savedInstanceState.putString(MovieListActivity.DB_REQUEST_TYPE, mMoviesRecyclerViewAdapter.getDbRequestType().toString());
        super.onSaveInstanceState(savedInstanceState);
    }


    private boolean existSavedState(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.containsKey(MovieListActivity.MOVIES_KEY);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }


    @Override
    public void onClick(final Movie item) {
        if (!mMoviesRecyclerViewAdapter.getDbRequestType().isFavorites() && !NetworkUtils.isNetworkAvailable(this)) {
            final Snackbar snackbar = Snackbar.make(mBinding.clActivityList, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                    MovieListActivity.this.onClick(item);
                }
            });

            snackbar.show();
            return;
        }
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_KEY, item);
        startActivity(intent);
    }

    private void hideProgressBar() {
        if (mBinding.pbLoading.getVisibility() == View.VISIBLE) {
            mBinding.pbLoading.setVisibility(View.GONE);
            mBinding.rcMovieList.setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        mMoviesRecyclerViewAdapter.clear();
        showProgressBar();
        requestMoviesWithLoader(mMoviesRecyclerViewAdapter.getDbRequestType(), mMoviesRecyclerViewAdapter.getCurrentPage());
    }

    private void showProgressBar() {
        mBinding.pbLoading.setVisibility(View.VISIBLE);
        mBinding.rcMovieList.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_top_rated:
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.TOP_RATED);
                refresh();
                return true;

            case R.id.action_popular:
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.POPULAR);
                refresh();
                return true;

            case R.id.action_favored:
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.FAVORITES);
                refresh();
                return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMoviesRecyclerViewAdapter.getDbRequestType().isFavorites())
            refresh();
    }

    @Override
    public Loader<MoviesRequest> onCreateLoader(int id, final Bundle args) {
        return new MoviesTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<MoviesRequest> loader, MoviesRequest data) {
        hideProgressBar();
        if (data == null) {
            Toast.makeText(MovieListActivity.this, R.string.error_get_movies, Toast.LENGTH_LONG).show();
            return;
        }
        mMoviesRecyclerViewAdapter.append(data.getMovies());
        mMoviesRecyclerViewAdapter.setCurrentPage(data.getPage());
        mLoading = false;

    }

    @Override
    public void onLoaderReset(Loader<MoviesRequest> loader) {
        mLoading = false;
        hideProgressBar();
    }

    private void showSnackNotificationWhenOfflineFavorite(){
        if (mMoviesRecyclerViewAdapter.getDbRequestType().isFavorites() && !NetworkUtils.isNetworkAvailable(this)) {
            final Snackbar snackbar = Snackbar.make(mBinding.clActivityList, R.string.no_internet_favorite, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }
    }
}
