package com.mistdev.popularmovies.async;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.mistdev.popularmovies.data.MoviesContract;

/**
 * Created by mcastro on 8/22/16.
 * Delete a movie from favorites
 */
public class DeleteFavoriteLoader extends AsyncTaskLoader<Integer> {

    private long mMovieId;

    public DeleteFavoriteLoader(Context context, long movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    public Integer loadInBackground() {

        Uri uri = MoviesContract.FavoriteMovieEntry.buildMovieUri(mMovieId);

        return getContext().getContentResolver().delete(uri, null, null);
    }
}
