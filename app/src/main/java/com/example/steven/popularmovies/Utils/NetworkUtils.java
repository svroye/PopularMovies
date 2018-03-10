package com.example.steven.popularmovies.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.steven.popularmovies.BuildConfig;
import com.example.steven.popularmovies.Objects.Movie;
import com.example.steven.popularmovies.Objects.MovieReview;

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
    public static final String MOVIE_DETAILS_END_POINT = "/movie/";

    public static final String API_KEY_PARAMETER = "api_key";
    // PLEASE PROVIDE YOUR OWN API KEY HERE
    public static final String API_KEY_VALUE = BuildConfig.API_KEY;
    public static final String APPEND_TO_RESPONSE_KEY = "append_to_response";
    public static final String VIDEOS_VALUE = "videos";
    public static final String REVIEWS_VALUE = "reviews";

    public static final String TAG = "NetworkUtils";

    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/";
    public static final String SIZE = "w185/";

    /**
     * build the URL that points to the popular movies end point
     * @return URL
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

    /**
     * build the URL that points to the top rated end point
     * @return URL
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


    public static URL buildMovieDetailsUrl(int id) {
        // get the start URL
        String startUrl = BASE_URL + MOVIE_DETAILS_END_POINT + id;
        URL url = null;
        // append the key to the url
        Uri uri = Uri.parse(startUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, API_KEY_VALUE)
                .appendQueryParameter(APPEND_TO_RESPONSE_KEY, VIDEOS_VALUE + "," + REVIEWS_VALUE)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, url.toString());
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
     */
    public static Movie[] parseJsonResultMovieList(String response) {
        // response is a JSONObject, so create a new one from the response
        JSONObject jsonResult;
        // variables for storing parameters of the response
        JSONArray results = null;
        int id = -1;
        String posterPath = null;

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
                if (movieItem.has("poster_path")){
                    posterPath = movieItem.optString("poster_path");
                }

                // instantiate a new Movie with the parameters from the movie item
                Movie movie = new Movie(id, -1, null, posterPath,
                        null, null, null, null, 0,
                        null, null, false);

                movieArray[i] = movie;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieArray;
    }

    /**
     * parse the input String response into a Movie array containing all the movies
     * present in the query
     * @param response: String holding the response from the http request
     * @return array of Movie instances holding the individual movies in the response
     */
    public static Movie parseJsonResultMovieDetails(String response) {
        // response is a JSONObject, so create a new one from the response
        JSONObject jsonResult;
        // variables for storing parameters of the response
        ArrayList<String> genres = new ArrayList<>();
        String overview = null;
        String posterPath = null;
        String releaseDate = null;
        int runTime = -1;
        String tagline = null;
        String title = null;
        double voteAverage = 0.0;
        ArrayList<String> trailerIds = new ArrayList<>();
        ArrayList<MovieReview> reviews = new ArrayList<>();

        Movie movie = null;

        try {
            jsonResult = new JSONObject(response);

            if (jsonResult.has("genres")){
                JSONArray genresArray = jsonResult.optJSONArray("genres");
                for(int j = 0; j < genresArray.length(); j++){
                    JSONObject currentGenre = genresArray.optJSONObject(j);
                    if (currentGenre.has("name")){
                        genres.add(currentGenre.optString("name"));
                    }
                }
            }

            if (jsonResult.has("overview")){
                overview = jsonResult.optString("overview");
            }

            if (jsonResult.has("poster_path")){
                posterPath = jsonResult.optString("poster_path");
            }

            if (jsonResult.has("release_date")){
                releaseDate = jsonResult.optString("release_date");
            }

            if (jsonResult.has("runtime")){
                runTime = jsonResult.optInt("runtime");
            }
            if (jsonResult.has("tagline")){
                tagline = jsonResult.optString("tagline");
            }

            if (jsonResult.has("title")){
                title = jsonResult.optString("title");
            }

            if (jsonResult.has("vote_average")){
                voteAverage = jsonResult.optDouble("vote_average");
            }

            if (jsonResult.has("videos")){
                JSONObject videos = jsonResult.optJSONObject("videos");
                if (videos.has("results")){
                    JSONArray results = videos.optJSONArray("results");
                    for (int k = 0; k < results.length(); k++){
                        JSONObject trailer = results.optJSONObject(k);
                        if (trailer.has("key")){
                            String trailerKey = trailer.optString("key");
                            trailerIds.add(trailerKey);
                        }
                    }
                }
            }

            if (jsonResult.has("reviews")){
                JSONObject reviewsCollection = jsonResult.optJSONObject("reviews");
                if (reviewsCollection.has("results")){
                    JSONArray reviewResults = reviewsCollection.optJSONArray("results");
                    for (int m = 0; m < reviewResults.length(); m++){
                        JSONObject review = reviewResults.getJSONObject(m);
                        String content = null;
                        String author = null;
                        if (review.has("content")){
                            content = review.optString("content");
                        }
                        if (review.has("author")){
                            author = review.optString("author");
                        }
                        reviews.add(new MovieReview(content, author));
                    }
                }
            }

            // instantiate a new Movie with the parameters
            movie = new Movie(-1, voteAverage, title, posterPath, genres, overview, tagline,
                    releaseDate, runTime, trailerIds, reviews, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }

    // following code is based on the stackoverflow post for checking the network state of the app
    // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
