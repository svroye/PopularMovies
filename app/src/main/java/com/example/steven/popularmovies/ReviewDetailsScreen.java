package com.example.steven.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Objects.MovieReview;
import com.example.steven.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class ReviewDetailsScreen extends AppCompatActivity {

    ImageView mPosterImageView;
    TextView mAuthorTextView;
    TextView mContentTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details_screen);

        mPosterImageView = findViewById(R.id.reviewDetails_moviePoster);
        mAuthorTextView = findViewById(R.id.reviewDetail_author);
        mContentTextView = findViewById(R.id.reviewDetail_content);

        Intent intentThatStartedActivity = getIntent();
        MovieReview review = intentThatStartedActivity.getParcelableExtra(
                getString(R.string.intent_extra_review));
        Movie movie = intentThatStartedActivity.getParcelableExtra(
                getString(R.string.intent_extra_movie));
        String imagePath = NetworkUtils.BASE_URL_POSTER + NetworkUtils.SIZE + movie.getPosterPath();
        setTitle(movie.getTitle());

        if(review != null){
            Picasso.with(this)
                    .load(imagePath)
                    .fit()
                    .into(mPosterImageView);
            mAuthorTextView.setText(review.getAuthor());
            mContentTextView.setText(review.getContent());
        } else {
            finish();
        }
    }
}
