package com.example.steven.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Steven on 19/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public static final String TAG = "MovieAdapter";
    int mCount;

    public MovieAdapter(int number){
        mCount = number;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.grid_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.listItem_movieImage);
        }

    }

}
