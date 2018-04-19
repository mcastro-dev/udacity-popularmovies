package com.mistdev.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;

/**
 * Created by kastr on 26/07/2016.
 * Database contract
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.mistdev.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    /*public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_MOST_POPULAR = "mostpopular";
    public static final String PATH_TOP_RATED = "toprated";*/


    /* MOVIE
     * --------------------------------------------------------------------------------------------------------------------------------------------*/
    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table name
        public static final String TABLE_NAME = "movie";

        //Columns
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* FAVORITE
     * --------------------------------------------------------------------------------------------------------------------------------------------*/
    /*public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        //Table name
        public static final String TABLE_NAME = "favorite";

        //Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_LOCAL_POSTER = "local_poster";

        //In JOIN cases when _id's matter
        public static final String JOIN_COLUMN_ID = "favorite_id";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteWithMovieId(long movieId) {

            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .build();
        }

        public static Uri buildFavoriteWithMovieIdAndLocalPoster(long movieId, String localPoster) {

            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .appendPath(localPoster)
                    .build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            String stringMovieId = uri.getPathSegments().get(1);
            long movieId = 0;

            try {
                movieId = Long.parseLong(stringMovieId);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return movieId;
        }

        public static String getMovieLocalPosterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    *//* MOST POPULAR
     * --------------------------------------------------------------------------------------------------------------------------------------------*//*
    public static final class MostPopularEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;

        //Table name
        public static final String TABLE_NAME = "most_popular";

        //Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMostPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    *//* TOP RATED
     * --------------------------------------------------------------------------------------------------------------------------------------------*//*
    public static final class TopRatedEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        //Table name
        public static final String TABLE_NAME = "top_rated";

        //Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTopRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }*/

}
