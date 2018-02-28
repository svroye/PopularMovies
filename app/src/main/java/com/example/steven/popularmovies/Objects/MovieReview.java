package com.example.steven.popularmovies.Objects;

/**
 * Created by Steven on 28/02/2018.
 */

public class MovieReview {

    public String mContent;
    public String mAuthor;

    public MovieReview(String content, String author){
        mContent = content;
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
