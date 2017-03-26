
package lt.babenskas.popularmovies.model.api;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private ArrayList<Review> reviews = new ArrayList<>();
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    public final static Parcelable.Creator<ReviewResponse> CREATOR = new Creator<ReviewResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ReviewResponse createFromParcel(Parcel in) {
            ReviewResponse instance = new ReviewResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.reviews, (Review.class.getClassLoader()));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public ReviewResponse[] newArray(int size) {
            return (new ReviewResponse[size]);
        }

    }
    ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(reviews);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return  0;
    }

}
