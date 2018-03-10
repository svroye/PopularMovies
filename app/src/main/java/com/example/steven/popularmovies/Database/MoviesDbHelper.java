package com.example.steven.popularmovies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Steven on 3/03/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favoriteMovies.db";
    static final int DATABASE_VERSION = 2;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_DATABASE = "CREATE TABLE " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME
                + "( " + MoviesContract.FavoriteMoviesEntry._ID + " INTEGER AUTO INCREMENT PRIMARY KEY, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_IMAGE_PATH + " TEXT, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_RATING + " REAL, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS + " TEXT, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE + " TEXT, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_RUNTIME + " REAL);";
        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL_DELETE = "DROP TABLE IF EXISTS " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE);
        onCreate(sqLiteDatabase);
    }
}
