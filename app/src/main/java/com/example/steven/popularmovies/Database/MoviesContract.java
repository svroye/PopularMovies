package com.example.steven.popularmovies.Database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Steven on 3/03/2018.
 */

public class MoviesContract {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.steven.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final String PATH_FAVORITE_MOVIES = "favoriteMovies";


    public static final class FavoriteMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_IMAGE_PATH = "imagePath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_TAGLINE = "tagline";
        public static final String COLUMN_RUNTIME = "runtime";
    }
}
