package com.mistdev.popularmovies.async;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mistdev.popularmovies.data.MoviesContract;
import com.mistdev.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kastr on 3/08/2016.
 * Favorite movies SQLite data
 */
public class FavoriteMoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private final String LOG_TAG = FavoriteMoviesLoader.class.getSimpleName();

    public FavoriteMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {

        List<Movie> listFavorites = new ArrayList<>();

        String sortOrder = MoviesContract.FavoriteMovieEntry._ID + " DESC";
        Cursor cursor = getContext().getContentResolver().query(MoviesContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, sortOrder);

        if(cursor == null)
            return listFavorites;

        try {

            while (cursor.moveToNext()) {
                listFavorites.add(Movie.movieFromCursor(cursor));
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error querying movies", e);

        } finally {
            cursor.close();
        }

        return listFavorites;
    }



}
