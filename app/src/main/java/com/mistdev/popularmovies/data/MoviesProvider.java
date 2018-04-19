package com.mistdev.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kastr on 26/07/2016.
 * Content Provider
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    private static final MoviesQueryBuilder sQueryBuilder = new MoviesQueryBuilder();


    //This UriMatcher will match each URI to the integer constants defined above.
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code. (* for strings, # for numbers)
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                return MoviesContract.FavoriteMovieEntry.CONTENT_DIR_TYPE;

            case MOVIE_WITH_ID:
                return MoviesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
    }


    /* QUERY
     * --------------------------------------------------------------------------------------------------------------*/
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case MOVIE: {

                cursor = db.query(MoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }

            case MOVIE_WITH_ID: {

                long id = ContentUris.parseId(uri);
                cursor = sQueryBuilder.queryMovieWithId(db, id);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if(getContext() != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /* INSERT
     * --------------------------------------------------------------------------------------------------------------*/
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)) {
            //movie
            case MOVIE: {

                long id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0)
                    returnUri = MoviesContract.FavoriteMovieEntry.buildMovieUri(id);

                break;
            }
            case MOVIE_WITH_ID: {

                long id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0)
                    returnUri = MoviesContract.FavoriteMovieEntry.buildMovieUri(id);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);

        }
        if(getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    /* BULK INSERT
     * --------------------------------------------------------------------------------------------------------------*/
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnCount = 0;

        switch (sUriMatcher.match(uri)) {

            case MOVIE: {
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_NAME, null, value);
                        if (id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }

                break;
            }

            default:
                return super.bulkInsert(uri, values);
        }

        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }


    /* DELETE
     * --------------------------------------------------------------------------------------------------------------*/
    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deletedRows = 0;

        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_ID: {

                long id = ContentUris.parseId(uri);

                whereClause = MoviesContract.FavoriteMovieEntry._ID + "=?";
                whereArgs = new String[]{ String.valueOf(id) };

                deletedRows = db.delete(MoviesContract.FavoriteMovieEntry.TABLE_NAME, whereClause, whereArgs);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);

        }

        if(getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }


    /* UPDATE
     * --------------------------------------------------------------------------------------------------------------*/
    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_ID: {

                long id = ContentUris.parseId(uri);

                whereClause = MoviesContract.FavoriteMovieEntry._ID + "=?";
                whereArgs = new String[]{ String.valueOf(id) };

                rowsUpdated = db.update(MoviesContract.FavoriteMovieEntry.TABLE_NAME, contentValues, whereClause, whereArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (rowsUpdated > 0 && getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }


}
