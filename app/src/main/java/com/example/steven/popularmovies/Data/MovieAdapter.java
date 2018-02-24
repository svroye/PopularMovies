package com.example.steven.popularmovies.Data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Steven on 19/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public static final String TAG = "MovieAdapter";
    public GridItemClickListener mGridItemClickListener;
    public Context mContext;
    public Movie[] mData;

    public interface GridItemClickListener {
        void onGridItemClick(int clickedItemIndex);
    }

    public MovieAdapter(Context context, GridItemClickListener gridItemClickListener){
        mContext = context;
        mGridItemClickListener = gridItemClickListener;
    }

    public void setData(Movie[] data){
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.grid_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mData[position];
        String posterPath = movie.getPosterPath();
        holder.bindViewWithData(posterPath);
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final String BASE_URL = "http://image.tmdb.org/t/p/";
        public final String SIZE = "w185/";

        ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.listItem_movieImage);
            itemView.setOnClickListener(this);
        }

        /*
        onClickListener for the individual grid item view. The position of the clicked
        item index is passed to the listener instance
         */
        @Override
        public void onClick(View view) {
            mGridItemClickListener.onGridItemClick(getAdapterPosition());
        }

        public void bindViewWithData(String endPath){
            String imagePath = BASE_URL + SIZE + endPath;
            Picasso.with(mContext)
                    .load(imagePath)
                    .fit()
                    .into(mImageView);
        }

    }

}
