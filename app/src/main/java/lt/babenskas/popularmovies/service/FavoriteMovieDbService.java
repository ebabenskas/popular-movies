package lt.babenskas.popularmovies.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import lt.babenskas.popularmovies.data.MovieContract.MovieEntry;
import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.model.api.MoviesRequest;

public class FavoriteMovieDbService {
    private final ContentResolver contentResolver;

    public FavoriteMovieDbService(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void checkFavorites(MoviesRequest moviesRequest) {
        for (Movie m : moviesRequest.getMovies()){
            m.setFavorite(checkIsFavorite(m.getId()));
        }
    }

    public boolean checkIsFavorite(Integer id) {
        Cursor c = contentResolver.query(MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{id.toString()},
                null);
        if (c == null)
            return false;
        boolean exist = c.getCount() > 0;
        c.close();
        return exist;
    }

    public void deleteFromFavorite(Integer id) {
        Uri uriToDelete = MovieEntry.CONTENT_URI.buildUpon().appendPath(id.toString()).build();
        contentResolver.delete(uriToDelete,
                null,
                null);
    }

    public void addToFavorite(Movie m) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_ID, m.getId());
        contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH, m.getBackdropPath());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, m.getOverview());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH, m.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, m.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_TITLE, m.getTitle());
        contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, m.getVoteAverage());
        contentValues.put(MovieEntry.COLUMN_VOTE_COUNT, m.getVoteCount());
        contentResolver.insert(MovieEntry.CONTENT_URI, contentValues);
    }

    public MoviesRequest getFavoriteMovies(){
        Cursor c = contentResolver.query(MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        ArrayList<Movie> movies = new ArrayList<>();
        while(c.moveToNext()) {
            Movie m = new Movie();
            m.setId(c.getInt(c.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
            m.setBackdropPath(c.getString(c.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH)));
            m.setOverview(c.getString(c.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));
            m.setPosterPath(c.getString(c.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)));
            m.setReleaseDate(c.getString(c.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
            m.setTitle(c.getString(c.getColumnIndex(MovieEntry.COLUMN_TITLE)));
            m.setVoteAverage(c.getDouble(c.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
            m.setVoteCount(c.getInt(c.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT)));
            m.setFavorite(true);
            movies.add(m);
        }
        return new MoviesRequest(1, movies, movies.size(), 1);
    }
}
