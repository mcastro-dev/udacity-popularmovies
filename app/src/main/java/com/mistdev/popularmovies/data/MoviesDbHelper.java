package com.mistdev.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mistdev.popularmovies.data.MoviesContract.FavoriteMovieEntry;

/**
 * Created by kastr on 26/07/2016.
 * Database
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    public static final String DB_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Table MOVIE
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteMovieEntry.COLUMN_API_ID + " BIGINT NOT NULL," +
                FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_RELEASE_DATE + " DATETIME NOT NULL, " +
                FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                FavoriteMovieEntry.COLUMN_POSTER + " VARCHAR NOT NULL, " +
                //FavoriteMovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +

                " UNIQUE (" + MoviesContract.FavoriteMovieEntry.COLUMN_API_ID + ") ON CONFLICT IGNORE " +

                " );";

        //Table FAVORITE
        /*final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE ON CONFLICT IGNORE NOT NULL, " +
                FavoriteEntry.COLUMN_LOCAL_POSTER + " VARCHAR, " +

                "FOREIGN KEY (" + FavoriteEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                FavoriteMovieEntry.TABLE_NAME + " (" + FavoriteMovieEntry._ID + ") " +

                ");";

        //Table MOST POPULAR
        final String SQL_CREATE_MOST_POPULAR_TABLE = "CREATE TABLE " + MostPopularEntry.TABLE_NAME + " (" +
                MostPopularEntry.COLUMN_MOVIE_ID + " INTEGER REFERENCES " + FavoriteMovieEntry.TABLE_NAME + " (" + FavoriteMovieEntry._ID + ") UNIQUE ON CONFLICT REPLACE NOT NULL);";

        //Table TOP RATED
        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +
                TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER REFERENCES " + FavoriteMovieEntry.TABLE_NAME + " (" + FavoriteMovieEntry._ID + ") UNIQUE ON CONFLICT REPLACE NOT NULL);";*/

        //EXEC
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        /*sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MostPopularEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
