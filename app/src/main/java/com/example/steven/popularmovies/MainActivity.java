package com.example.steven.popularmovies;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.steven.popularmovies.Data.AsyncTaskCompleteListener;
import com.example.steven.popularmovies.Data.MovieAdapter;
import com.example.steven.popularmovies.Data.MovieListAsyncTask;
import com.example.steven.popularmovies.Database.MoviesContract;
import com.example.steven.popularmovies.Database.MoviesDbHelper;
import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    // tag used for logging
    public static final String TAG = "MainActivity";
    // RecyclerView populating the activity and containing movie images
    RecyclerView mRecyclerView;
    // adapter to set to the RecyclerView
    MovieAdapter mAdapter;
    // Loading indicator for when the data is loaded
    ProgressBar mProgressBar;
    // textview for when user is offline or when an error occurred
    TextView mNoInternetErrorTv;
    // database used for the favorite movies
    SQLiteDatabase mDatabase;

    public static final int DETAIL_MOVIE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mainActivity_recyclerView);
        mProgressBar =  findViewById(R.id.mainActivity_progressBar);
        mNoInternetErrorTv = findViewById(R.id.mainActivity_noInternetOrErrorTv);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        // set layout dependent on the orientation of the device
        GridLayoutManager gridLayoutManager = null;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4);
        }

        mAdapter = new MovieAdapter(this,this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase = (new MoviesDbHelper(this)).getReadableDatabase();

        // read the preferred ordering from the preferences
        String preferredOrdering = sp.getString(getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_most_popular_value));

        setMainTitle(preferredOrdering);

        loadData(preferredOrdering);
    }

    /**
     * loads the data, either by using a service call or by reading it from the database
     * @param preferredOrdering ordering mechanism for the movies, either most popular, top rated
     *                          or favorite. Used to determine whether a service call has to be made
     *                          or whether the data is read from the database
     */
    public void loadData(String preferredOrdering){
        if (preferredOrdering.equals(getString(R.string.pref_order_by_favorite_value))) {
            // load data from DB for the favorite movies
            Movie[] favorites = readDataFromDatabase();
            if (favorites != null){
                mAdapter.setData(favorites);
                showResults();
            } else {
                showNoFavoritesMessage();
            }

        } else {
            if (NetworkUtils.isOnline(this)){
                URL requestUrl = getPreferredUrl(preferredOrdering);
                new MovieListAsyncTask(this, new MovieListCompleteListener()).execute(requestUrl);
            } else {
                showNoInternetMessage();
            }
        }
    }

    /**
     * get the resource for the title of main screen
     * @param preferredOrdering value of the ordering method in the SharedPreferences
     * @return integer resource with a reference to the title, or -1 if a wrong input String
     *          was provided
     */
    public void setMainTitle(String preferredOrdering){
        // get the resource for the title
        int resource = -1;
        if (preferredOrdering.equals(getString(R.string.pref_order_by_most_popular_value))){
            resource = R.string.pref_order_by_most_popular_label;
        } else if (preferredOrdering.equals(getString(R.string.pref_order_by_top_rated_value))){
            resource = R.string.pref_order_by_top_rated_label;
        } else if (preferredOrdering.equals(getString(R.string.pref_order_by_favorite_value))){
            resource = R.string.pref_order_by_favorite_label;
        }

        // set the title to the corresponding value
        if (resource != -1){
            setTitle(resource);
        }
    }

    /**
     * read data for the favorite movies from the database
     * @return array of movies from the database
     */
    public Movie[] readDataFromDatabase(){
        // query the favorites from the database
        ContentResolver resolver = getContentResolver();
        Cursor queryResult = resolver.query(MoviesContract.FavoriteMoviesEntry.CONTENT_URI, null,
                null, null, MoviesContract.FavoriteMoviesEntry._ID);
        // check whether there are results from the query
        if( queryResult.getCount() == 0) {
            // no result, show message to the user
            showNoFavoritesMessage();
            return null;
        } else {
            // transform the cursor with the favorites to a Movie array
            Movie[] movies = new Movie[queryResult.getCount()];
            // index for keeping track of the index for adding a movie
            int index = 0;
            // as long as the cursor has a next row, a favorite is present to add
            while (queryResult.moveToNext()){
                // retrieve posterPath from the database
                String posterPath = queryResult.getString(queryResult.getColumnIndex(
                        MoviesContract.FavoriteMoviesEntry.COLUMN_IMAGE_PATH));
                // retrieve movieId from the database
                int movieId = queryResult.getInt(queryResult.getColumnIndex(
                        MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID));
                // create new Movie instance
                Movie movie = new Movie(movieId, 0, null, posterPath, null,
                        null, null, null, 0, null,
                        null, false);
                // add movie to the array of movies
                movies[index] = movie;
                // increment the index
                index++;
            }
            return movies;
        }
    }

    public URL getPreferredUrl(String preferenceValue){
        if (preferenceValue.equals(getString(R.string.pref_order_by_most_popular_value))){
            return NetworkUtils.buildPopularMoviesUrl();
        } else if (preferenceValue.equals(getString(R.string.pref_order_by_top_rated_value))){
            return NetworkUtils.buildTopRatedUrl();
        }
        return null;
    }

    /**
     * Create the option menu in the header
     * @param menu to inflate
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Actions for what should happen when one of the options
     * in the menu is selected
     * @param item selected item in the list
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menuMain_settings_item:
                Intent intentToSettingsActivity = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intentToSettingsActivity);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(int clickedItemIndex) {
        // create the intent for starting the details activity
        Intent intentToDetailActivity = new Intent(MainActivity.this,
                DetailActivity.class);
        // add the movie id of the clicked item to the intent
        int movieId = mAdapter.mData[clickedItemIndex].getID();
        intentToDetailActivity.putExtra("id", movieId);

        startActivityForResult(intentToDetailActivity, DETAIL_MOVIE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAIL_MOVIE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                // get the preferred ordering
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String preferredOrdering = sp.getString(
                        getString(R.string.pref_order_by_key),
                        getString(R.string.pref_order_by_most_popular_label));
                // only if the ordering is by favorites, a refresh needs to happen
                if (preferredOrdering.equals(getString(R.string.pref_order_by_favorite_value))){
                    loadData(preferredOrdering);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * called when no internet is available. Shows the error message
     * and hides the loading indicator (ProgressBar) and RecyclerView
     */
    public void showNoInternetMessage(){
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mNoInternetErrorTv.setText(getString(R.string.no_internet_message));
        mNoInternetErrorTv.setVisibility(View.VISIBLE);
    }

    /**
     * called when no internet is available. Shows the error message
     * and hides the loading indicator (ProgressBar) and RecyclerView
     */
    public void showNoFavoritesMessage(){
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mNoInternetErrorTv.setText(getString(R.string.no_favorites_message));
        mNoInternetErrorTv.setVisibility(View.VISIBLE);
    }

    public void showResults(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mNoInternetErrorTv.setVisibility(View.GONE);
        mNoInternetErrorTv.setVisibility(View.GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_order_by_key))){
            String preferredOrdering = sharedPreferences.getString(
                    getString(R.string.pref_order_by_key),
                    getString(R.string.pref_order_by_most_popular_value));
            loadData(preferredOrdering);
            setMainTitle(preferredOrdering);
        }
    }

    public class MovieListCompleteListener implements AsyncTaskCompleteListener<Movie[]> {

        /**
         * sets the new Movie array to the adapter
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(Movie[] result) {
            mAdapter.setData(result);
        }

        /**
         called when the loading is started. Hides the results (RecyclerView)
         and shows the loading indicator (ProgressBar)
         */
        @Override
        public void showLoadingIndicator() {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mNoInternetErrorTv.setVisibility(View.GONE);
        }

        /**
        called when the loading has finished. Shows the results (RecyclerView)
        and hides the loading indicator (ProgressBar)
         */
        @Override
        public void hideLoadingIndicator() {
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mNoInternetErrorTv.setVisibility(View.GONE);
        }

        /**
        called when an error occurred. Shows the error message
        and hides the loading indicator (ProgressBar) and RecyclerView
        */
        @Override
        public void showErrorMessage() {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoInternetErrorTv.setText(getString(R.string.error_message));
            mNoInternetErrorTv.setVisibility(View.VISIBLE);
        }
    }

}
