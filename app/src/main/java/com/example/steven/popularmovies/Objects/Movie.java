package com.example.steven.popularmovies.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import static java.lang.System.out;

/**
 * Created by Steven on 19/02/2018.
 */
// code for the parcelable based on
// https://guides.codepath.com/android/using-parcelable

public class Movie implements Parcelable {

    public int mID;
    public double mVoteAverage;
    public String mTitle;
    public String mPosterPath;
    public ArrayList<String> mGenres;
    public String mOverview;
    public String mTagline;
    public String mReleaseDate;
    public int mRuntime;
    public ArrayList<String> mTrailerKeys;
    public ArrayList<MovieReview> mReviews;
    public boolean mIsFavorite;


    /*
    constructor for creating a new Movie object
     */
    public Movie(int id, double voteAverage, String title, String posterPath,
                 ArrayList<String> genres, String overview, String tagline,
                 String releaseDate, int runtime, ArrayList<String> trailers,
                 ArrayList<MovieReview> reviews, boolean isFavorite){
        mID = id;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPosterPath = posterPath;
        mGenres = genres;
        mOverview = overview;
        mTagline = tagline;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
        mTrailerKeys = trailers;
        mReviews = reviews;
        mIsFavorite = isFavorite;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    public ArrayList<String> getTrailerIds() {
        return mTrailerKeys;
    }

    public ArrayList<MovieReview> getReviews() {
        return mReviews;
    }

    public boolean getIsFavorite(){
        return mIsFavorite;
    }

    public void setIsFavorite(boolean favorite){
        mIsFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel parcel){
        mID = parcel.readInt();
        mVoteAverage = parcel.readDouble();
        mTitle = parcel.readString();
        mPosterPath = parcel.readString();
        mGenres = (ArrayList<String>) parcel.readSerializable();
        mOverview = parcel.readString();
        mTagline = parcel.readString();
        mReleaseDate = parcel.readString();
        mRuntime = parcel.readInt();
        mTrailerKeys = (ArrayList<String>) parcel.readSerializable();
        mReviews = (ArrayList<MovieReview>) parcel.readSerializable();
        mIsFavorite = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeSerializable(mGenres);
        parcel.writeString(mOverview);
        parcel.writeString(mTagline);
        parcel.writeString(mReleaseDate);
        parcel.writeInt(mRuntime);
        parcel.writeSerializable(mTrailerKeys);
        parcel.writeSerializable(mReviews);
        parcel.writeInt(mIsFavorite ? 1 : 0);
    }
}
