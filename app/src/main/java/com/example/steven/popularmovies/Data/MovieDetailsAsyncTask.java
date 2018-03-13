package com.example.steven.popularmovies.Data;

import android.content.Context;
import android.os.AsyncTask;

import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Steven on 26/02/2018.
 */

public class MovieDetailsAsyncTask extends AsyncTask<URL, Void, Movie> {

    private Context context;
    private AsyncTaskCompleteListener<Movie> listener;

    public MovieDetailsAsyncTask(Context context, AsyncTaskCompleteListener<Movie> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // start the loading, so show the loading indicator and hide the recyclerview
        listener.showLoadingIndicator();
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
            String response = NetworkUtils.performHttpRequest(queryUrl);
            // parse the response and assign the result to the Movie array
            movie = NetworkUtils.parseJsonResultMovieDetails(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }

    @Override
    protected void onPostExecute(Movie result) {
        super.onPostExecute(result);
        // loading has finished, so hide the loading indicator and show the results
        listener.hideLoadingIndicator();
        if (result != null){
            listener.onTaskComplete(result);
        } else {
            listener.showErrorMessage();
        }
    }
}
