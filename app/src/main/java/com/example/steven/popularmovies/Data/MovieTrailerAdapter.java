package com.example.steven.popularmovies.Data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by Steven on 4/03/2018.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    public Context mContext;
    public int mCount;
    public MovieTrailerClickListener mMovieTrailerClickListener;

    public MovieTrailerAdapter(Context context, int numberOfTrailers, MovieTrailerClickListener
                               listener){
        mContext = context;
        mCount = numberOfTrailers;
        mMovieTrailerClickListener = listener;
    }

    public interface MovieTrailerClickListener {
        void onMovieTrailerClick(int clickedItemIndex);
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        holder.mTrailerTv.setText(mContext.getString(R.string.trailer_numbering, position + 1));
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTrailerTv;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTrailerTv = itemView.findViewById(R.id.movieTrailerListItem_trailerName);
        }

        @Override
        public void onClick(View view) {
            mMovieTrailerClickListener.onMovieTrailerClick(getAdapterPosition());
        }
    }


}
