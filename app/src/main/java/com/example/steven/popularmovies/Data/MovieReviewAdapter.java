package com.example.steven.popularmovies.Data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.steven.popularmovies.Objects.MovieReview;
import com.example.steven.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by Steven on 3/03/2018.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    ArrayList<MovieReview> mData;

    public MovieReviewAdapter(ArrayList<MovieReview> reviews){
        mData = reviews;
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        MovieReview review = mData.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mAuthorTv;
        TextView mContentTv;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTv = itemView.findViewById(R.id.movieReviewListItem_author);
            mContentTv = itemView.findViewById(R.id.movieReviewListItem_content);
        }

        public void bind(MovieReview review){
            mAuthorTv.setText(review.getAuthor());
            mContentTv.setText(review.getContent());
        }
    }
}
