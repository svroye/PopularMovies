package com.example.steven.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";

    public int movieId;

    ProgressBar mProgressBar;
    ScrollView mScrollView;
    TextView mTitleTv;
    TextView mTaglineTv;
    ImageView mPosterIv;
    TextView mRuntimeTv;
    TextView mReleaseDateTv;
    TextView mRatingTv;
    TextView mSynopsisTv;
    TextView mNoInternetTv;

    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String SIZE = "w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProgressBar = findViewById(R.id.detailActivity_ProgressBar);
        mScrollView =  findViewById(R.id.detailActivity_content);
        mTitleTv =  findViewById(R.id.detailActivity_Title);
        mTaglineTv = findViewById(R.id.detailActivity_Tagline);
        mPosterIv =  findViewById(R.id.detailActivity_moviePoster);
        mRuntimeTv = findViewById(R.id.detailActivity_runtime);
        mReleaseDateTv = findViewById(R.id.detailActivity_releaseDate);
        mRatingTv = findViewById(R.id.detailActivity_voteAverage);
        mSynopsisTv = findViewById(R.id.detailActivity_synopsis);
        mNoInternetTv = findViewById(R.id.detailActivity_noInternetTextView);

        Intent intentThatStartedActivity = getIntent();

        if(intentThatStartedActivity != null){
            if (intentThatStartedActivity.hasExtra("id")){
                movieId = intentThatStartedActivity.getIntExtra("id", -1);
                if (NetworkUtils.isOnline(this)){
                    (new MovieDetailsQueryTask()).execute(NetworkUtils.buildMovieDetailsUrl(movieId));
                } else {
                    showNoInternetMessage();
                }
            }

        }
    }

    /*
   called when the loading is started. Hides the results
   and shows the loading indicator (ProgressBar)
    */
    public void showLoadingIndicator(){
        mScrollView.setVisibility(View.GONE);
        mNoInternetTv.setVisibility(View.GONE);
        mPosterIv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /*
    called when the loading has finished. Shows the results (RecyclerView)
    and hides the loading indicator (ProgressBar)
     */
    public void hideLoadingIndicator(){
        mScrollView.setVisibility(View.VISIBLE);
        mNoInternetTv.setVisibility(View.GONE);
        mPosterIv.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /*
called when no internet is available. Shows the error message
and hides the loading indicator (ProgressBar) and RecyclerView
*/
    public void showNoInternetMessage(){
        mScrollView.setVisibility(View.GONE);
        mNoInternetTv.setVisibility(View.VISIBLE);
        mPosterIv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    public class MovieDetailsQueryTask extends AsyncTask<URL, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // start the loading, so show the loading indicator and hide the recyclerview
            showLoadingIndicator();
        }

        @Override
        protected Movie doInBackground(URL... urls) {
            // get the input URL
            URL queryUrl = urls[0];
            if (null == queryUrl) return null;
            // instantiate  new Movie array for storing the results of the query
            Movie movie = null;

            try {
                // get the response from the request
                String response = NetworkUtils.fetchHttpResponse(queryUrl);
                // parse the response and assign the result to the Movie array
                movie = NetworkUtils.parseJsonResultMovieDetails(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movie;
        }

        @Override
        protected void onPostExecute(Movie s) {
            super.onPostExecute(s);
            // loading has finished, so hide the loading indicator and show the results
            hideLoadingIndicator();
            bindDataToUI(s);
        }
    }

    public void bindDataToUI(Movie movie){
        String title = movie.getTitle();
        if (title != null) {
            mTitleTv.setText(title);
            setTitle(title);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }

        String tagline = movie.getTagline();
        if (title != null) {
            mTaglineTv.setText(tagline);
        } else {
            mTaglineTv.setVisibility(View.GONE);
        }

        String posterPath = movie.getPosterPath();
        if (posterPath != null){
            String imagePath = BASE_URL + SIZE + posterPath;
            Picasso.with(this)
                    .load(imagePath)
                    .fit()
                    .into(mPosterIv);
        }

        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null){
            mReleaseDateTv.setText(releaseDate);
        }

        int runtime = movie.getRuntime();
        if (runtime != 0){
            mRuntimeTv.setText(getString(R.string.runtime, runtime));
        }

        double rating = movie.getVoteAverage();
        if (rating != 0.0){
            mRatingTv.setText(getString(R.string.rating, rating));
        }

        String synopsis = movie.getOverview();
        if (synopsis != null){
            mSynopsisTv.setText(synopsis);
        }

    }
}
