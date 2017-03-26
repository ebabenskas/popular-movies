
package lt.babenskas.popularmovies.model.api;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideosResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private ArrayList<Video> videos = null;
    public final static Parcelable.Creator<VideosResponse> CREATOR = new Creator<VideosResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public VideosResponse createFromParcel(Parcel in) {
            VideosResponse instance = new VideosResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.videos, (Video.class.getClassLoader()));
            return instance;
        }

        public VideosResponse[] newArray(int size) {
            return (new VideosResponse[size]);
        }

    }
    ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(videos);
    }

    public int describeContents() {
        return  0;
    }

}
