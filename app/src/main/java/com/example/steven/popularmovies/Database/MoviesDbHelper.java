package com.example.steven.popularmovies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Steven on 3/03/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favoriteMovies.db";
    static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_DATABASE = "CREATE TABLE " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME
                + "( " + MoviesContract.FavoriteMoviesEntry._ID + " INTEGER AUTO INCREMENT PRIMARY KEY, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MoviesContract.FavoriteMoviesEntry.COLUMN_IMAGE_PATH + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // no SQL for upgrade needed for now
        // to be implemented when something needs to be changed in the database
    }
}
