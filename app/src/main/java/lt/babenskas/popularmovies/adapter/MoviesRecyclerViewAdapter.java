package lt.babenskas.popularmovies.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lt.babenskas.popularmovies.R;
import lt.babenskas.popularmovies.databinding.MovieListContentBinding;
import lt.babenskas.popularmovies.model.api.Movie;
import lt.babenskas.popularmovies.service.TheMovieDbService;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MoviesViewHolder> {

    public static final String HTTP_IMAGE_TMDB_ORG_T_P_W185 = "http://image.tmdb.org/t/p/w185/";
    private ArrayList<Movie> mValues = new ArrayList<>();
    private final MoviesRecyclerViewAdapterOnClickHandler mClickHandler;
    private int mCurrentPage = 1;
    private TheMovieDbService.MovieDbRequestType mDbRequestType = TheMovieDbService.MovieDbRequestType.POPULAR;

    public MoviesRecyclerViewAdapter(MoviesRecyclerViewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieListContentBinding contentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.movie_list_content, parent, false);
        return new MoviesViewHolder(contentBinding);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Picasso.with(holder.mContentBinding.ivMovieListImage.getContext()).load(HTTP_IMAGE_TMDB_ORG_T_P_W185 + holder.mItem.getPosterPath()).into(holder.mContentBinding.ivMovieListImage);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public TheMovieDbService.MovieDbRequestType getDbRequestType() {
        return mDbRequestType;
    }

    public void setDbRequestType(TheMovieDbService.MovieDbRequestType dbRequestType) {
        this.mDbRequestType = dbRequestType;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MovieListContentBinding mContentBinding;
        public Movie mItem;

        public MoviesViewHolder(MovieListContentBinding binding) {
            super(binding.getRoot());
            mContentBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie m = mValues.get(adapterPosition);
            mClickHandler.onClick(m);
        }
    }

    public interface MoviesRecyclerViewAdapterOnClickHandler {
        void onClick(Movie item);
    }

    public ArrayList<Movie> getValues() {
        return mValues;
    }

    public void append(ArrayList<Movie> values) {
        int startPosition = getItemCount();
        this.mValues.addAll(values);
        notifyItemRangeInserted(startPosition, values.size());
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.mCurrentPage = currentPage;
    }

    public void clear() {
        mValues.clear();
        mCurrentPage = 1;
        notifyDataSetChanged();
    }


}
