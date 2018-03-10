package com.example.steven.popularmovies.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Steven on 8/03/2018.
 */

public class FavoritemovieContentProvider extends ContentProvider {

    MoviesDbHelper mHelper;

    // constants for identifying the whole table or a single entry
    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES,
                FAVORITE_MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES + "/#",
                FAVORITE_MOVIE_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor returnCursor;

        switch (match){
            case FAVORITE_MOVIES:
                returnCursor = db.query(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                String mSelection = MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                String id = uri.getPathSegments().get(1);
                String[] mSelectionArgs = {id};
                returnCursor = db.query(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, projection, mSelection,
                        mSelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        Uri returnUri;
        switch (match){
            case FAVORITE_MOVIES:
                long id = db.insert(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, null,
                        contentValues);

                if (id > 0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                            id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int match = sUriMatcher.match(uri);
        int moviesDeleted;
        final SQLiteDatabase db = mHelper.getWritableDatabase();

        switch (match){
            case FAVORITE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String whereClause = MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] whereArgs = {id};
                moviesDeleted = db.delete(MoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                        whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        // not needed in this application
        return 0;
    }
}
