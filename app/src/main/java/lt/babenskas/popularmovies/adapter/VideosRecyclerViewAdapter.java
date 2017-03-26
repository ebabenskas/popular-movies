package lt.babenskas.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lt.babenskas.popularmovies.R;
import lt.babenskas.popularmovies.model.api.Video;

public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.VideosViewHolder> {

    private ArrayList<Video> mValues = new ArrayList<>();
    private final VideoRecyclerViewAdapterOnClickHandler mClickHandler;

    public VideosRecyclerViewAdapter(VideoRecyclerViewAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_content, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideosViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Picasso.with(holder.mView.getContext()).load(holder.mItem.getThumbnailUrl()).into(holder.mImage);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mImage;
        public Video mItem;

        public VideosViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.iv_video_list_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video m = mValues.get(adapterPosition);
            mClickHandler.onClick(m);
        }
    }

    public interface VideoRecyclerViewAdapterOnClickHandler {
        void onClick(Video item);
    }

    public ArrayList<Video> getValues() {
        return mValues;
    }

    public void append(ArrayList<Video> values) {
        int startPosition = getItemCount();
        this.mValues.addAll(values);
        notifyItemRangeInserted(startPosition, values.size());
    }


    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }




}
