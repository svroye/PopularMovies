package com.example.steven.popularmovies.Objects;

import java.util.ArrayList;

/**
 * Created by Steven on 19/02/2018.
 */

public class Movie {

    public int mID;
    public boolean mVideo;
    public int mVoteAverage;
    public String mTitle;
    public String mPosterPath;
    public ArrayList<Integer> mGenres;
    public String mOverview;
    public boolean mAdult;


    /*
    constructor for creating a new Movie object
     */
    public Movie(int id, boolean video, int voteAverage, String title, String posterPath,
                 ArrayList<Integer> genres, String overview, boolean adult){
        mID = id;
        mVideo = video;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPosterPath = posterPath;
        mGenres = genres;
        mOverview = overview;
        mAdult = adult;
    }

    public int getID(){
        return mID;
    }

    public boolean hasVideo() {
        return mVideo;
    }

    public int getVoteAverage() {
        return mVoteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public ArrayList<Integer> getGenres() {
        return mGenres;
    }

    public String getOverview() {
        return mOverview;
    }

    public boolean isAdult() {
        return mAdult;
    }


}
