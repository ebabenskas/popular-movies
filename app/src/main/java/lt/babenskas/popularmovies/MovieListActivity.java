package lt.babenskas.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import lt.babenskas.popularmovies.adapter.MoviesRecyclerViewAdapter;
import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.service.TheMovieDbService;
import lt.babenskas.popularmovies.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.MoviesRecyclerViewAdapterOnClickHandler, Callback<MoviesRequest>  {

    private static final String TAG = "MovieListActivity";
    private RecyclerView mRecyclerView;
    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private ProgressBar mProgressBar;
    private static final String MOVIES_KEY = "movies";
    private static final String PAGE_SIZE = "page";
    private static final String DB_REQUEST_TYPE = "type";
    private Boolean mLoading = false;
    private Toast noInternetToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mRecyclerView = (RecyclerView)findViewById(R.id.movie_list);
        mProgressBar = (ProgressBar)findViewById(R.id.pb_loading);
        assert mRecyclerView != null;
        mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(this);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMoviesRecyclerViewAdapter);
        if (existSavedState(savedInstanceState)) {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MovieListActivity.MOVIES_KEY);
            int page = savedInstanceState.getInt(PAGE_SIZE);
            TheMovieDbService.MovieDbRequestType movieDbRequestType = TheMovieDbService.MovieDbRequestType.valueOf(savedInstanceState.getString(DB_REQUEST_TYPE));
            mMoviesRecyclerViewAdapter.append(movies);
            mMoviesRecyclerViewAdapter.setCurrentPage(page);
            mMoviesRecyclerViewAdapter.setDbRequestType(movieDbRequestType);
        }
        else {
            showProgressBar();
            requestMoviesFromService(mMoviesRecyclerViewAdapter.getDbRequestType(), 1);
        }
        //       when is reach bottom, then is notLoading more items.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                // if not scrolling or mLoading, do nothing.
                if (dy == 0 || mLoading)
                    return;
                int pastVisiblesItems, visibleItemCount, totalItemCount;
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount()-calculateNoOfColumns(MovieListActivity.this);
                pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    requestMoviesFromService(mMoviesRecyclerViewAdapter.getDbRequestType(), mMoviesRecyclerViewAdapter.getCurrentPage()+1);
                }
            }
        });
    }

    private void requestMoviesFromService(TheMovieDbService.MovieDbRequestType type , int page) {
        if (!checkInternetAvailable())
            return;
        Log.d(TAG, "Sent request to movie service. Params: MovieDbRequestType - " + type +"; page -"+ page);
        mLoading = true;
        TheMovieDbService.getInstance().requestMoviesAsync(type, page, this);
    }

    private boolean checkInternetAvailable() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            if (noInternetToast != null)
                noInternetToast.cancel();
            noInternetToast = Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG);
            noInternetToast.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(MovieListActivity.MOVIES_KEY, mMoviesRecyclerViewAdapter.getValues());
        savedInstanceState.putInt(MovieListActivity.PAGE_SIZE, mMoviesRecyclerViewAdapter.getCurrentPage());
        savedInstanceState.putString(MovieListActivity.DB_REQUEST_TYPE, mMoviesRecyclerViewAdapter.getDbRequestType().toString());
        super.onSaveInstanceState(savedInstanceState);
    }


    private boolean existSavedState(Bundle savedInstanceState){
        return savedInstanceState != null && savedInstanceState.containsKey(MovieListActivity.MOVIES_KEY);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }


    @Override
    public void onClick(Movie item) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_KEY, item);
        startActivity(intent);
    }

    @Override
    public void onResponse(Call<MoviesRequest> call, Response<MoviesRequest> response) {
        mMoviesRecyclerViewAdapter.append(response.body().getMovies());
        mMoviesRecyclerViewAdapter.setCurrentPage(response.body().getPage());
        mLoading = false;
        hideProgressBar();

    }


    @Override
    public void onFailure(Call<MoviesRequest> call, Throwable t) {
        mLoading = false;
        hideProgressBar();
        Toast.makeText(this, R.string.error_get_movies, Toast.LENGTH_LONG).show();

    }

    private void hideProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void refresh(){
        mMoviesRecyclerViewAdapter.clear();
        showProgressBar();
        requestMoviesFromService(mMoviesRecyclerViewAdapter.getDbRequestType(), mMoviesRecyclerViewAdapter.getCurrentPage());
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!checkInternetAvailable())
            return true;
        switch (item.getItemId()) {
            case R.id.action_top_rated:
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.TOP_RATED);
                refresh();
                return true;

            case R.id.action_popular:
                mMoviesRecyclerViewAdapter.setDbRequestType(TheMovieDbService.MovieDbRequestType.POPULAR);
                refresh();
                return true;
        }
        return true;
    }
}
