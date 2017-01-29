package lt.babenskas.popularmovies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import lt.babenskas.popularmovies.model.api.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String HTTP_IMAGE_TMDB_ORG_T_P_W185 = "http://image.tmdb.org/t/p/w185/";
    private Movie mMovie = null;
    public static final String MOVIE_KEY = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mMovie = getmMovie(savedInstanceState);

        TextView title = (TextView) findViewById(R.id.tv_detail_movie_title);
        ImageView img = (ImageView) findViewById(R.id.iv_detail_movie_image);
        TextView year = (TextView) findViewById(R.id.tv_detail_movie_year);
        TextView length = (TextView) findViewById(R.id.tv_detail_movie_lenght);
        TextView info = (TextView) findViewById(R.id.tv_detail_movie_info);

        year.setText(mMovie.getReleaseDate());
        length.setText(String.format(getCurrentLocale(), "%.2f/%d", mMovie.getVoteAverage(), mMovie.getVoteCount()));
        title.setText(mMovie.getTitle());
        info.setText(mMovie.getOverview());
        Picasso.with(this).load(HTTP_IMAGE_TMDB_ORG_T_P_W185 + mMovie.getPosterPath()).into(img);
    }

    private Movie getmMovie(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MovieDetailActivity.MOVIE_KEY)) {
            return savedInstanceState.getParcelable(MovieDetailActivity.MOVIE_KEY);
        }
        return getIntent().getParcelableExtra(MovieDetailActivity.MOVIE_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This need, because when click home, navigation recreates parent activity instead onResume().
            // When click hardware back button, this work correct.
            //
            Intent h = NavUtils.getParentActivityIntent(this);
            h.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, h);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("movie", mMovie);
        super.onSaveInstanceState(savedInstanceState);
    }


    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            return getResources().getConfiguration().locale;
        }
    }
}
