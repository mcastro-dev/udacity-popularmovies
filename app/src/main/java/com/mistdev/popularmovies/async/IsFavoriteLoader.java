package com.mistdev.popularmovies.async;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mistdev.popularmovies.data.MoviesContract;
import com.mistdev.popularmovies.models.Movie;
import com.mistdev.popularmovies.data.MoviesContract.FavoriteMovieEntry;

/**
 * Created by mcastro on 8/15/16.
 * Loader to check if a movie is already a favorite or not
 */
public class IsFavoriteLoader extends AsyncTaskLoader<Movie> {

    private final String LOG_TAG = IsFavoriteLoader.class.getSimpleName();

    private long mMovieApiId;

    public IsFavoriteLoader(Context context, long movieApiId) {
        super(context);
        mMovieApiId = movieApiId;
    }

    @Override
    public Movie loadInBackground() {

        Uri uri = FavoriteMovieEntry.CONTENT_URI;
        String selection = FavoriteMovieEntry.COLUMN_API_ID + "=?";
        String[] selectionArgs = new String[]{
            String.valueOf(mMovieApiId)
        };

        Cursor cursor = null;
        Movie movie = null;

        try {

            cursor = getContext().getContentResolver().query(uri, null, selection, selectionArgs, null);

            if(cursor != null && cursor.moveToFirst())
                movie = Movie.movieFromCursor(cursor);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error querying favorite movie", e);

        } finally {

            if(cursor != null)
                cursor.close();
        }

        return movie;
    }
}
