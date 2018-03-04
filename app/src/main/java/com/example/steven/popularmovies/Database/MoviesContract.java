package com.example.steven.popularmovies.Database;

import android.provider.BaseColumns;

/**
 * Created by Steven on 3/03/2018.
 */

public class MoviesContract {


    public class FavoriteMoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_IMAGE_PATH = "imagePath";
    }
}
