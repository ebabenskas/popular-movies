package lt.babenskas.popularmovies.service;

import java.io.IOException;

import lt.babenskas.popularmovies.BuildConfig;
import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.model.api.ReviewResponse;
import lt.babenskas.popularmovies.model.api.VideosResponse;
import retrofit2.Call;
import retrofit2.Response;
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

    public MoviesRequest getMovies(MovieDbRequestType requestType, int page) throws IOException {
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
        Response<MoviesRequest> response= request.execute();
        return response.body();
    }

    public VideosResponse getVideos(Integer movieId) throws IOException {
        Call<VideosResponse> request = mService.getVideos(movieId, mMovieDbApiKey);
        Response<VideosResponse> response = request.execute();
        return response.body();
    }

    public ReviewResponse getReviews(Integer movieId) throws IOException {
        Call<ReviewResponse> request = mService.getReviews(movieId, mMovieDbApiKey);
        Response<ReviewResponse> response = request.execute();
        return response.body();
    }

    public enum MovieDbRequestType {
        TOP_RATED,
        POPULAR,
        FAVORITES;

        public boolean isFavorites(){
            return this.equals(FAVORITES);
        }
    }
}
