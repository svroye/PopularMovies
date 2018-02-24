package com.example.steven.popularmovies.Objects;

import java.util.ArrayList;

/**
 * Created by Steven on 19/02/2018.
 */

public class Movie {

    public int mID;
    public double mVoteAverage;
    public String mTitle;
    public String mPosterPath;
    public ArrayList<String> mGenres;
    public String mOverview;
    public String mTagline;
    public String mReleaseDate;
    public int mRuntime;


    /*
    constructor for creating a new Movie object
     */
    public Movie(int id, double voteAverage, String title, String posterPath,
                 ArrayList<String> genres, String overview, String tagline,
                 String releaseDate, int runtime){
        mID = id;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPosterPath = posterPath;
        mGenres = genres;
        mOverview = overview;
        mTagline = tagline;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
    }

    public int getID(){
        return mID;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public ArrayList<String> getGenres() {
        return mGenres;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getTagline() {
        return mTagline;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getRuntime() {
        return mRuntime;
    }
}
