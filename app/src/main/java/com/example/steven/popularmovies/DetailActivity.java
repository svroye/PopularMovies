package com.example.steven.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steven.popularmovies.Data.AsyncTaskCompleteListener;
import com.example.steven.popularmovies.Data.MovieDetailsAsyncTask;
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
    TextView mNoInternetErrorTv;
    FloatingActionButton mFab;

    private boolean isFavorite = false;

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
        mNoInternetErrorTv = findViewById(R.id.detailActivity_noInternetOrErrorTv);
        mFab = findViewById(R.id.detailActivity_fab);

        Intent intentThatStartedActivity = getIntent();

        if(intentThatStartedActivity != null){
            if (intentThatStartedActivity.hasExtra("id")){
                movieId = intentThatStartedActivity.getIntExtra("id", -1);
                URL url = NetworkUtils.buildMovieDetailsUrl(movieId);
                loadData(url);
            }
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoriteButton(!isFavorite);
            }
        });
    }

    /**
     * changes the FloatingActionButton's colour and icon dependent on whether the user selects it
     * to be a favorite or not
     * @param setFavorite boolean representing whether the button needs to become favorite or not
     */
    public void setFavoriteButton(boolean setFavorite){
        Toast mToast;
        if(setFavorite){
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailActivity.this,R.color.colorFavorite)));
            mFab.setImageResource(R.drawable.ic_star_border_black_24dp);
            isFavorite = true;
            mToast = Toast.makeText(this, getString(R.string.movie_added_to_favorites), Toast.LENGTH_SHORT);
        } else {
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailActivity.this,R.color.colorAccent)));
            mFab.setImageResource(R.drawable.ic_star_border_white_24dp);
            isFavorite = false;
            mToast = Toast.makeText(this, getString(R.string.movie_removed_from_favorites), Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    /**
     * starts the AsyncTask to fetch data for a certain movie
     * First checks whether an internet connection is available
     * If not, the AsyncTask is not started and an error message is shown
     * @param url : url to be called
     */
    public void loadData(URL url){
        if (NetworkUtils.isOnline(this)){
            (new MovieDetailsAsyncTask(this, new MovieDetailCompleteListener())).execute(url);
        } else {
            showNoInternetMessage();
        }
    }

    /**
    called when no internet is available. Shows the error message
    and hides the loading indicator (ProgressBar) and RecyclerView
    */
    public void showNoInternetMessage(){
        mScrollView.setVisibility(View.GONE);
        mNoInternetErrorTv.setText(getString(R.string.no_internet_message));
        mNoInternetErrorTv.setVisibility(View.VISIBLE);
        mPosterIv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    public class MovieDetailCompleteListener implements AsyncTaskCompleteListener<Movie> {

        /**
         * binds the resulting Movie object to the correct Views in the UI
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(Movie result) {
            bindDataToUI(result);
        }

        /**
       called when the loading is started. Hides the results
       and shows the loading indicator (ProgressBar)
        */
        @Override
        public void showLoadingIndicator() {
            mScrollView.setVisibility(View.GONE);
            mNoInternetErrorTv.setVisibility(View.GONE);
            mPosterIv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
        called when the loading has finished. Shows the results (RecyclerView)
        and hides the loading indicator (ProgressBar)
         */
        @Override
        public void hideLoadingIndicator() {
            mScrollView.setVisibility(View.VISIBLE);
            mNoInternetErrorTv.setVisibility(View.GONE);
            mPosterIv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        /**
        called when an error occurs. Shows the error message
        and hides the loading indicator (ProgressBar) and RecyclerView
        */
        @Override
        public void showErrorMessage() {
            mScrollView.setVisibility(View.GONE);
            mNoInternetErrorTv.setText(getString(R.string.error_message));
            mNoInternetErrorTv.setVisibility(View.VISIBLE);
            mPosterIv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * binds the the data in the Movie object to the UI
     * @param movie
     */
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
