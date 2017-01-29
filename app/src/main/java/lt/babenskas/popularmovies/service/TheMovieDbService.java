package lt.babenskas.popularmovies.service;

import lt.babenskas.popularmovies.BuildConfig;
import lt.babenskas.popularmovies.model.api.MoviesRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDbService {

    public static final String HTTPS_API_THEMOVIEDB_ORG_3_MOVIE = "https://api.themoviedb.org/3/movie/";
    private Retrofit mRetrofit;
    private static TheMovieDbService mApi = new TheMovieDbService();
    private TheMovieDbRetrofitInterface mService;
    private String mMovieDbApiKey;

    public static TheMovieDbService getInstance() {
        return mApi;
    }

    private TheMovieDbService() {
        mMovieDbApiKey = BuildConfig.MOVIE_DB_API_KEY;
        if (mMovieDbApiKey.equals("missingKeyFile"))
            throw new RuntimeException("Missing api.themoviedb.org API key. Create app/config.properties file if he doesn't exist and there insert API key with movieDbKey properties name!");
        mRetrofit = new Retrofit.Builder()
                .baseUrl(HTTPS_API_THEMOVIEDB_ORG_3_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = mRetrofit.create(TheMovieDbRetrofitInterface.class);
    }


    public void requestMoviesAsync(MovieDbRequestType requestType, int page, Callback callback){
        Call<MoviesRequest> request;
        switch (requestType){
            case TOP_RATED:
                request = mService.getTopRatedMovies(mMovieDbApiKey, page);
                break;
            case POPULAR:
                request = mService.getPopularMovies(mMovieDbApiKey, page);
                break;
            default:
                request = mService.getPopularMovies(mMovieDbApiKey, page);
        }
        request.enqueue(callback);
    }

    public enum MovieDbRequestType {
        TOP_RATED,
        POPULAR;
    }
}
