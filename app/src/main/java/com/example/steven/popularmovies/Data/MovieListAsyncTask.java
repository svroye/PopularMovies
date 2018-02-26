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

public class MovieListAsyncTask extends AsyncTask<URL, Void, Movie[]> {

    private Context context;
    private AsyncTaskCompleteListener<Movie[]> listener;

    public MovieListAsyncTask(Context ctx, AsyncTaskCompleteListener<Movie[]> listener) {
        this.context = ctx;
        this.listener = listener;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        listener.showLoadingIndicator();
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
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        // loading has finished, so hide the loading indicator and show the results
        listener.hideLoadingIndicator();
        if (movies != null) {
            listener.onTaskComplete(movies);
        } else {
            listener.showErrorMessage();
        }
    }
}