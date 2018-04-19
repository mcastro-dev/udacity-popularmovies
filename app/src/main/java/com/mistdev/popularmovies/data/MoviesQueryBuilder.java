package com.mistdev.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by kastr on 29/07/2016.
 * Query builder for more complex queries
 */
public class MoviesQueryBuilder extends SQLiteQueryBuilder {

    public MoviesQueryBuilder() {
        super();
    }

    /* MOVIE
     * ----------------------------------------------------------------------------------------------------*/
    public Cursor queryMovieWithId(SQLiteDatabase db, long id) {

        String movie_id = MoviesContract.FavoriteMovieEntry.TABLE_NAME + "." + MoviesContract.FavoriteMovieEntry._ID;
        //String favorite_movie_id = MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID;

        setTables(
                MoviesContract.FavoriteMovieEntry.TABLE_NAME /*+
                        " LEFT JOIN " + MoviesContract.FavoriteEntry.TABLE_NAME +
                        " ON " + movie_id +
                        " = " + favorite_movie_id*/
        );

        String[] projectionIn = new String[]{
                movie_id,
                MoviesContract.FavoriteMovieEntry.COLUMN_API_ID,
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
                MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER
                //MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID + " as " + MoviesContract.FavoriteEntry.JOIN_COLUMN_ID
        };

        String selection = MoviesContract.FavoriteMovieEntry._ID + "=?";
        String[] selectionArgs = new String[]{ String.valueOf(id) };

        return super.query(db, projectionIn, selection, selectionArgs, null, null, null);
    }

    /* MOST POPULAR
     * ----------------------------------------------------------------------------------------------------*/
    /*public Cursor queryMostPopularMovies(SQLiteDatabase db, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {

        String movie_id = MoviesContract.FavoriteMovieEntry.TABLE_NAME + "." + MoviesContract.FavoriteMovieEntry._ID;
        String mostPopular_movie_id = MoviesContract.MostPopularEntry.TABLE_NAME + "." + MoviesContract.MostPopularEntry.COLUMN_MOVIE_ID;
        String favorite_movie_id = MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID;

        setTables(
                MoviesContract.MostPopularEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.FavoriteMovieEntry.TABLE_NAME +
                " ON " + mostPopular_movie_id +
                " = " + movie_id +
                " LEFT JOIN " + MoviesContract.FavoriteEntry.TABLE_NAME +
                " ON " + movie_id +
                " = " + favorite_movie_id
        );

        String[] projectionIn = new String[]{
                movie_id,
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
                MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER,
                MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID + " as " + MoviesContract.FavoriteEntry.JOIN_COLUMN_ID
        };

        return super.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder);
    }


    *//* TOP RATED
     * ----------------------------------------------------------------------------------------------------*//*
    public Cursor queryTopRatedMovies(SQLiteDatabase db, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {

        String movie_id = MoviesContract.FavoriteMovieEntry.TABLE_NAME + "." + MoviesContract.FavoriteMovieEntry._ID;
        String topRated_movie_id = MoviesContract.TopRatedEntry.TABLE_NAME + "." + MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID;
        String favorite_movie_id = MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID;

        setTables(
                MoviesContract.TopRatedEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.FavoriteMovieEntry.TABLE_NAME +
                        " ON " + topRated_movie_id +
                        " = " + movie_id +
                        " LEFT JOIN " + MoviesContract.FavoriteEntry.TABLE_NAME +
                        " ON " + movie_id +
                        " = " + favorite_movie_id
        );

        String[] projectionIn = new String[]{
                movie_id,
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
                MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER,
                MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID + " as " + MoviesContract.FavoriteEntry.JOIN_COLUMN_ID
        };

        return super.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder);
    }


    *//* FAVORITE
     * ----------------------------------------------------------------------------------------------------*//*
    public Cursor queryFavoriteMovies(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {

        String movie_id = MoviesContract.FavoriteMovieEntry.TABLE_NAME + "." + MoviesContract.FavoriteMovieEntry._ID;

        setTables(
                MoviesContract.FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.FavoriteMovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.FavoriteEntry.TABLE_NAME +
                        "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + movie_id
        );

        projectionIn = new String[]{
                movie_id,
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
                MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.FavoriteMovieEntry.COLUMN_POSTER,
                MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID + " as " + MoviesContract.FavoriteEntry.JOIN_COLUMN_ID
        };

        return super.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder);
    }

    //with movieId
    public Cursor queryFavoriteMoviesWithMovieId(SQLiteDatabase db, Uri uri) {

        setTables(
                MoviesContract.FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.FavoriteMovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.FavoriteEntry.TABLE_NAME +
                        "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.FavoriteMovieEntry.TABLE_NAME +
                        "." + MoviesContract.FavoriteMovieEntry._ID
        );

        String[] projectionIn = new String[]{
                MoviesContract.FavoriteMovieEntry.TABLE_NAME + "." + MoviesContract.FavoriteMovieEntry._ID,
                MoviesContract.FavoriteMovieEntry.COLUMN_TITLE,
                MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS,
                MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.FavoriteEntry.COLUMN_LOCAL_POSTER
        };

        String selection = MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{
                String.valueOf(MoviesContract.FavoriteEntry.getMovieIdFromUri(uri))
        };

        return super.query(db, projectionIn, selection, selectionArgs, null, null, null);
    }*/



}
