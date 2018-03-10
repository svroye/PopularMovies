package com.example.steven.popularmovies.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steven on 28/02/2018.
 */

public class MovieReview implements Parcelable{

    public String mContent;
    public String mAuthor;

    public MovieReview(String content, String author){
        mContent = content;
        mAuthor = author;
    }

    protected MovieReview(Parcel in) {
        mContent = in.readString();
        mAuthor = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mContent);
        parcel.writeString(mAuthor);
    }

}
