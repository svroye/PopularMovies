package com.example.steven.popularmovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steven.popularmovies.Data.AsyncTaskCompleteListener;
import com.example.steven.popularmovies.Data.MovieDetailsAsyncTask;
import com.example.steven.popularmovies.Data.MovieReviewAdapter;
import com.example.steven.popularmovies.Data.MovieTrailerAdapter;
import com.example.steven.popularmovies.Database.MoviesContract;
import com.example.steven.popularmovies.Database.MoviesDbHelper;
import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Objects.MovieReview;
import com.example.steven.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity
        implements MovieTrailerAdapter.MovieTrailerClickListener,
        MovieReviewAdapter.MovieReviewClickItemListener{

    public static final String TAG = "DetailActivity";

    public int movieId;
    public Movie mMovie;

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
    LinearLayout mTrailersSection;
    LinearLayout mReviewsSection;

    RecyclerView mRecyclerViewReviews;
    RecyclerView mRecyclerViewTrailers;

    SQLiteDatabase mDatabase;

    Toast mToast;

    public static final String YOUTUBE_START_URL = "https://www.youtube.com/watch";

    boolean initialState;

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
        mTrailersSection = findViewById(R.id.detailActivity_trailerSection);
        mReviewsSection = findViewById(R.id.detailActivity_reviewSection);
        mRecyclerViewReviews = findViewById(R.id.detailActivity_recyclerViewReviews);
        mRecyclerViewTrailers = findViewById(R.id.detailActivity_recyclerViewTrailers);

        mDatabase = (new MoviesDbHelper(this)).getWritableDatabase();

        Intent intentThatStartedActivity = getIntent();

        if(intentThatStartedActivity != null){
            // get the preferred ordering method from the SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    this);
            String preferredOrder = sharedPreferences.getString(getString(R.string.pref_order_by_key),
                    getString(R.string.pref_order_by_most_popular_value));
            // get the id of the movie
            if (intentThatStartedActivity.hasExtra("id")) {
                movieId = intentThatStartedActivity.getIntExtra("id", -1);
            }

            if (preferredOrder.equals(getString(R.string.pref_order_by_favorite_value)) &&
                    !NetworkUtils.isOnline(this) ) {
                loadDataFromDatabase();
            } else {
                URL url = NetworkUtils.buildMovieDetailsUrl(movieId);
                loadData(url);
            }
        } else {
            finish();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoriteButton(!mMovie.getIsFavorite());
            }
        });
    }

    /**
     * Initialize the fab button to the correct image when the activity is created (=started)
     * @param isFavorite boolean representing whether the movie is present in the favorite
     *                  database or not
     */
    public void initializeFavoriteButton(boolean isFavorite){
        if (isFavorite){
            mFab.setImageResource(android.R.drawable.star_big_on);
        } else {
            mFab.setImageResource(android.R.drawable.star_big_off);
        }
        //mMovie.setIsFavorite(isFavorite);
    }

    /**
     * changes the FloatingActionButton's colour and icon dependent on whether the user selects it
     * to be a favorite or not. Also calls the method for saving/deleting the movie in the DB
     * @param setFavorite boolean representing whether the button needs to become favorite or not
     */
    public void setFavoriteButton(boolean setFavorite){
        // cancel the previous toast message if it exists
        if (mToast != null){
            mToast.cancel();
            mToast = null;
        }
        // set the image resource of the favorites button and save the movie to the database
        // If it was added successful, create a Toast message indicating this
        if(setFavorite){
            mFab.setImageResource(android.R.drawable.star_big_on);
            Uri returnUri = addMovieToFavorites();
            if (returnUri != null) {
                mToast = Toast.makeText(this, getString(R.string.movie_added_to_favorites,
                        mMovie.getTitle()),
                        Toast.LENGTH_SHORT);
            }
            // set the image resource of the favorites button and delete the movie from the database
            // If it was deleted successful, create a Toast message indicating this.
        } else {
            mFab.setImageResource(android.R.drawable.star_big_off);
            int rowsRemoved = removeMovieFromFavorites();
            if (rowsRemoved > 0) {
                mToast = Toast.makeText(this, getString(R.string.movie_removed_from_favorites,
                        mMovie.getTitle()), Toast.LENGTH_SHORT);
            }
        }
        // show the Toast message if it exists
        if (mToast != null) {
            mToast.show();
        }
    }

    /**
     * Adds the current movie to the favorites database, holding the users favorite movies
     * This function also updates the corresponding boolean flag of the Movie object, i.e. sets the
     * mIsFavorite flag to true
     * @return uri pointing to the inserted row
     */
    public Uri addMovieToFavorites(){
        // set the boolean attribute of the movie to favorite
        mMovie.setIsFavorite(true);
        // add the movie to the DB using ContentValues and the ContentResolver. For this, we need to
        // get the movie ID and the movie Poster path
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_IMAGE_PATH,
                mMovie.getPosterPath());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_RATING, mMovie.getVoteAverage());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS, mMovie.getOverview());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE, mMovie.getTagline());
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_RUNTIME, mMovie.getRuntime());
        ContentResolver resolver = getContentResolver();
        return resolver.insert(MoviesContract.FavoriteMoviesEntry.CONTENT_URI, contentValues);
    }

    /**
     * Removes the current movie from the favorites database, holding the users favorite movies
     * This function also updates the corresponding boolean flag of the Movie object, i.e. sets the
     * mIsFavorite flag to false
     * @return int corresponding to the number of entries removed from the database
     */
    public int removeMovieFromFavorites(){
        // set the boolean attribute of the movie to favorite
        mMovie.setIsFavorite(false);
        // delete the movie from the DB using the ContentResolver. For this, we need to
        // get the movie ID, append it the URI path of the table and pass it to the resolver.
        ContentResolver resolver = getContentResolver();
        Uri uri = ContentUris.withAppendedId(MoviesContract.FavoriteMoviesEntry.CONTENT_URI, movieId);
        return resolver.delete(uri, null, null );
    }

    /**
     * starts the AsyncTask to fetch data for a certain movie
     * First checks whether an internet connection is available
     * If not, the AsyncTask is not started and an error message is shown
     */
    public void loadData(URL url){
        if (NetworkUtils.isOnline(this)){
            (new MovieDetailsAsyncTask(this, new MovieDetailCompleteListener())).execute(url);
        } else {
            showNoInternetMessage();
        }
    }

    public void loadDataFromDatabase(){
        mProgressBar.setVisibility(View.VISIBLE);
        Uri queryUri = ContentUris.withAppendedId(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                movieId);
        ContentResolver resolver = getContentResolver();
        Cursor result = resolver.query(queryUri, null, null, null,
                null);
        if (result.getCount() != 0){
            result.moveToNext();
            double vote = result.getDouble(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_RATING));
            String title = result.getString(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE));
            String posterPath = result.getString(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_IMAGE_PATH));
            String synopsis = result.getString(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS));
            String tagline = result.getString(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE));
            String releaseDate = result.getString(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE));
            int runtime = result.getInt(result.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_RUNTIME));

            mMovie = new Movie(movieId, vote, title, posterPath, null, synopsis, tagline,
                    releaseDate, runtime, null, null, true);
            bindDataToUI(mMovie);
            mProgressBar.setVisibility(View.GONE);
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

    @Override
    public void onMovieTrailerClick(int clickedItemIndex) {
        ArrayList<String> trailerIds = mMovie.getTrailerIds();
        String trailer = trailerIds.get(clickedItemIndex);
        openYoutube(trailer);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
       ArrayList<MovieReview> reviews = mMovie.getReviews();
       MovieReview review = reviews.get(clickedItemIndex);
       Intent intentToReviewDetails = new Intent(DetailActivity.this,
               ReviewDetailsScreen.class);
       intentToReviewDetails.putExtra(getString(R.string.intent_extra_review), review);
       intentToReviewDetails.putExtra(getString(R.string.intent_extra_movie),
               mMovie);
       startActivity(intentToReviewDetails);
    }

    public class MovieDetailCompleteListener implements AsyncTaskCompleteListener<Movie> {

        /**
         * binds the resulting Movie object to the correct Views in the UI
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(Movie result) {
            mMovie = result;
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
            String imagePath = NetworkUtils.BASE_URL_POSTER + NetworkUtils.SIZE + posterPath;
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

        ArrayList<String> trailers = movie.getTrailerIds();
        if (trailers != null) {
            if (trailers.size() != 0) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewTrailers.setLayoutManager(linearLayoutManager);
                MovieTrailerAdapter adapter = new MovieTrailerAdapter(this, trailers.size(), this);
                mRecyclerViewTrailers.setAdapter(adapter);
            } else {
                mTrailersSection.setVisibility(View.GONE);
            }
        } else {
            mTrailersSection.setVisibility(View.GONE);
        }

        ArrayList<MovieReview> reviews = movie.getReviews();
        if (reviews != null) {
            if (reviews.size() != 0) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewReviews.setLayoutManager(linearLayoutManager);
                MovieReviewAdapter adapter = new MovieReviewAdapter(reviews, this);
                mRecyclerViewReviews.setAdapter(adapter);
            } else {
                mReviewsSection.setVisibility(View.GONE);
            }
        } else {
            mReviewsSection.setVisibility(View.GONE);
        }

        initialState = isMovieFavorite();
        initializeFavoriteButton(initialState);

    }

    public boolean isMovieFavorite(){
        ContentResolver resolver = getContentResolver();
        Uri uri = ContentUris.withAppendedId(MoviesContract.FavoriteMoviesEntry.CONTENT_URI, movieId);
        Cursor result = resolver.query(uri, null, null, null, null);
        return result.getCount() > 0 ? true : false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (initialState != mMovie.getIsFavorite()){
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void openYoutube(String movieTrailerId){
        Intent intentToStartYoutube = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(YOUTUBE_START_URL).buildUpon()
                .appendQueryParameter("v", movieTrailerId)
                .build();
        intentToStartYoutube.setData(uri);
        if (intentToStartYoutube.resolveActivity(getPackageManager()) != null) {
            startActivity(intentToStartYoutube);
        }
    }
}

