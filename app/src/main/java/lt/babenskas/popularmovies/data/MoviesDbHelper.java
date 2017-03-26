package lt.babenskas.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lt.babenskas.popularmovies.data.MovieContract.MovieEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;


    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "                 +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL,"                  +
                        MovieEntry.COLUMN_POSTER_PATH   + " TEXT NOT NULL, "                    +
                        MovieEntry.COLUMN_BACKDROP_PATH   + " TEXT NOT NULL, "                    +
                        MovieEntry.COLUMN_TITLE   + " TEXT NOT NULL, "                    +
                        MovieEntry.COLUMN_RELEASE_DATE   + " TEXT NOT NULL, "                    +
                        MovieEntry.COLUMN_VOTE_AVERAGE   + " REAL NOT NULL, "                    +
                        MovieEntry.COLUMN_VOTE_COUNT   + " INTEGER NOT NULL, "                    +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}