
package lt.babenskas.popularmovies.model.api;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoviesRequest implements Parcelable
{

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private ArrayList<Movie> movies = null;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    public final static Creator<MoviesRequest> CREATOR = new Creator<MoviesRequest>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MoviesRequest createFromParcel(Parcel in) {
            MoviesRequest instance = new MoviesRequest();
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.movies, (Movie.class.getClassLoader()));
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public MoviesRequest[] newArray(int size) {
            return (new MoviesRequest[size]);
        }

    }
    ;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MoviesRequest() {
    }

    /**
     * 
     * @param movies
     * @param totalResults
     * @param page
     * @param totalPages
     */
    public MoviesRequest(Integer page, ArrayList<Movie> movies, Integer totalResults, Integer totalPages) {
        super();
        this.page = page;
        this.movies = movies;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(page).append(movies).append(totalResults).append(totalPages).toHashCode();
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (other == this) {
//            return true;
//        }
//        if ((other instanceof MoviesRequest) == false) {
//            return false;
//        }
//        MoviesRequest rhs = ((MoviesRequest) other);
//        return new EqualsBuilder().append(page, rhs.page).append(movies, rhs.movies).append(totalResults, rhs.totalResults).append(totalPages, rhs.totalPages).isEquals();
//    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeList(movies);
        dest.writeValue(totalResults);
        dest.writeValue(totalPages);
    }

    public int describeContents() {
        return  0;
    }

}
