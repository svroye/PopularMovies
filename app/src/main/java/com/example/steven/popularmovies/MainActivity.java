package com.example.steven.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
    // Loading indicator for when the data is loaded
    ProgressBar mProgressBar;

    TextView mNoInternetErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mainActivity_recyclerView);
        mProgressBar =  findViewById(R.id.mainActivity_progressBar);
        mNoInternetErrorTv = findViewById(R.id.mainActivity_noInternetOrErrorTv);

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

        loadData(requestUrl);

    }

    public void loadData(URL url){
        if (NetworkUtils.isOnline(this)){
            //task.execute(requestUrl);
            // new FetchMyDataTask(this, new FetchMyDataTaskCompleteListener()).execute("InputString");
            new MovieListAsyncTask(this, new MovieListCompleteListener()).execute(url);
        } else {
            showNoInternetMessage();
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
        Log.d(TAG, "POS: " + clickedItemIndex);
        Intent intentToDetailActivity = new Intent(MainActivity.this,
                DetailActivity.class);
        Movie clickedMovie = mAdapter.mData[clickedItemIndex];
        int movieId = clickedMovie.getID();
        intentToDetailActivity.putExtra("id", movieId);
        startActivity(intentToDetailActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /*
    called when no internet is available. Shows the error message
    and hides the loading indicator (ProgressBar) and RecyclerView
    */
    public void showNoInternetMessage(){
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mNoInternetErrorTv.setText(getString(R.string.no_internet_message));
        mNoInternetErrorTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_order_by_key))){
            String preferredOrdering = sharedPreferences.getString(
                    getString(R.string.pref_order_by_key),
                    getString(R.string.pref_order_by_most_popular_label));
            URL newURL = getPreferredUrl(preferredOrdering);
            loadData(newURL);
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
