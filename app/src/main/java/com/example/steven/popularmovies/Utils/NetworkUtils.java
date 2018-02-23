package com.example.steven.popularmovies.Utils;

import android.net.Uri;

import com.example.steven.popularmovies.Objects.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Steven on 20/02/2018.
 */

public class NetworkUtils {

    public static final String BASE_URL = "https://api.themoviedb.org/3";
    public static final String POPULAR_MOVIES_END_POINT = "/movie/popular";
    public static final String TOP_RATED_END_POINT = "/movie/top_rated";

    public static final String API_KEY_PARAMETER = "api_key";
    // PLEASE PROVIDE YOUR OWN API KEY HERE
    public static final String API_KEY_VALUE = "";

    /*
    build the URL that points to the popular movies end point
     */
    public static URL buildPopularMoviesUrl() {
        // get the start URL
        String startUrl = BASE_URL + POPULAR_MOVIES_END_POINT;
        URL url = null;
        // append the key to the url
        Uri uri = Uri.parse(startUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, API_KEY_VALUE)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /*
    build the URL that points to the top rated end point
     */
    public static URL buildTopRatedUrl() {
        // get the start URL
        String startUrl = BASE_URL + TOP_RATED_END_POINT;
        URL url = null;
        // append the key to the url
        Uri uri = Uri.parse(startUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, API_KEY_VALUE)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String fetchHttpResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

    /**
     * parse the input String response into a Movie array containing all the movies
     * present in the query
     * @param response: String holding the response from the http request
     * @return array of Movie instances holding the individual movies in the response
     * @throws JSONException if an error occurs parsing the JSON response
     */
    public static Movie[] parseJsonResult(String response) {
        // response is a JSONObject, so create a new one from the response
        JSONObject jsonResult = null;
        // variables for storing parameters of the response
        JSONArray results = null;
        int id = -1;
        boolean video = false;
        int voteAverage = -1;
        String title = null;
        String posterPath = null;
        ArrayList<Integer> genres = new ArrayList<>();
        boolean adult = false;
        String overview = null;
        Movie[] movieArray = null;

        try {
            jsonResult = new JSONObject(response);

            // get the results array, holding all the movies from the query
            if(jsonResult.has("results")){
                results = jsonResult.getJSONArray("results");
            }
            movieArray = new Movie[results.length()];
            // for every movie in the results array, parse it to make a Movie instance
            for(int i = 0; i < results.length(); i++){
                JSONObject movieItem = results.getJSONObject(i);
                if (movieItem.has("id")){
                    id = movieItem.optInt("id");
                }
                if (movieItem.has("video")){
                    video = movieItem.optBoolean("video");
                }
                if (movieItem.has("vote_average")){
                    voteAverage = movieItem.optInt("vote_average");
                }
                if (movieItem.has("title")){
                    title = movieItem.optString("title");
                }
                if (movieItem.has("poster_path")){
                    posterPath = movieItem.optString("poster_path");
                }
                if (movieItem.has("genre_ids")){
                    JSONArray genre_ids = movieItem.getJSONArray("genre_ids");
                    for(int j = 0; j < genre_ids.length(); j++){
                        genres.add(genre_ids.getInt(j));
                    }
                }
                if (movieItem.has("adult")){
                    adult = movieItem.optBoolean("adult");
                }
                if (movieItem.has("overview")){
                    overview = movieItem.optString("overview");
                }
                // instantiate a new Movie with the parameters from the movie item
                Movie movie = new Movie(id, video, voteAverage, title, posterPath, genres, overview,
                        adult);

                movieArray[i] = movie;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieArray;
    }

}
