package lt.babenskas.popularmovies.service;

import lt.babenskas.popularmovies.model.api.MoviesRequest;
import lt.babenskas.popularmovies.model.api.ReviewResponse;
import lt.babenskas.popularmovies.model.api.VideosResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbRetrofitInterface {
    @GET("popular")
    Call<MoviesRequest> getPopularMovies(@Query("api_key") String api, @Query("page") Integer page);

    @GET("top_rated")
    Call<MoviesRequest> getTopRatedMovies(@Query("api_key") String api, @Query("page") Integer page);

    @GET("{movie_id}/videos")
    Call<VideosResponse> getVideos(@Path("movie_id") Integer movieId, @Query("api_key") String api);

    @GET("{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") Integer movieId, @Query("api_key") String api);
}
