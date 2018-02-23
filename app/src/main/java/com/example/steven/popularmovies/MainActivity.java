package com.example.steven.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.example.steven.popularmovies.Data.MovieAdapter;
import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    // tag used for logging
    public static final String TAG = "MainActivity";
    // RecyclerView populating the activity and containing movie images
    RecyclerView mRecyclerView;
    // adapter to set to the RecyclerView
    MovieAdapter mAdapter;
    private static final int NUM_LIST_ITEMS = 100;
    // Loading indicator for when the data is loaded
    ProgressBar mProgressBar;

    // https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.mainActivity_recyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.mainActivity_progressBar);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new MovieAdapter(this,this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // read the preferred ordering from the preferences
        String preferredOrdering = sp.getString(getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_most_popular_label));

        URL requestUrl = getPreferredUrl(preferredOrdering);

        MovieQueryTask task = new MovieQueryTask();

        task.execute(requestUrl);
    }


    public URL getPreferredUrl(String preferenceValue){
        if (preferenceValue.equals(getString(R.string.pref_order_by_most_popular_value))){
            return NetworkUtils.buildPopularMoviesUrl();
        } else if (preferenceValue.equals(getString(R.string.pref_order_by_top_rated_value))){
            return NetworkUtils.buildTopRatedUrl();
        }
        return null;
    }

    /*
    Create the option menu in the header
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /*
        Actions for what should happen when one of the options
        in the menu is selected
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
        Log.d(TAG, "POS: " + clickedItemIndex);
        Intent intentToDetailActivity = new Intent(MainActivity.this,
                DetailActivity.class);
        Movie clickedMovie = mAdapter.mData[clickedItemIndex];
        int movieId = clickedMovie.getID();
        intentToDetailActivity.putExtra("id", movieId);
        startActivity(intentToDetailActivity);
    }

    /*
    called when the loading is started. Hides the results (RecyclerView)
    and shows the loading indicator (ProgressBar)
     */
    public void showLoadingIndicator(){
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /*
    called when the loading has finished. Shows the results (RecyclerView)
    and hides the loading indicator (ProgressBar)
     */
    public void hideLoadingIndicator(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == getString(R.string.pref_order_by_key)){
            String preferredOrdering = sharedPreferences.getString(
                    getString(R.string.pref_order_by_key),
                    getString(R.string.pref_order_by_most_popular_label));
            URL newURL = getPreferredUrl(preferredOrdering);
            (new MovieQueryTask()).execute(newURL);
        }
    }


    public class MovieQueryTask extends AsyncTask<URL, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // start the loading, so show the loading indicator and hide the recyclerview
            showLoadingIndicator();
        }

        @Override
        protected Movie[] doInBackground(URL... urls) {
            // get the input URL
            URL queryUrl = urls[0];
            if (null == queryUrl) return null;
            // instantiate  new Movie array for storing the results of the query
            Movie[] movies = null;

            try {
                // get the response from the request
                String response = NetworkUtils.fetchHttpResponse(queryUrl);
                // parse the response and assign the result to the Movie array
                movies = NetworkUtils.parseJsonResultMovieList(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] s) {
            super.onPostExecute(s);
            Log.d(TAG, "Results: " + s.length);
            // loading has finished, so hide the loading indicator and show the results
            hideLoadingIndicator();
            mAdapter.setData(s);
        }
    }
}
