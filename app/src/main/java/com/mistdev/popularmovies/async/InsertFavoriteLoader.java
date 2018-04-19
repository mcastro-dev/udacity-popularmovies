package com.mistdev.popularmovies.async;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.mistdev.popularmovies.data.MoviesContract;
import com.mistdev.popularmovies.models.Movie;

/**
 * Created by mcastro on 8/22/16.
 * Insert a movie into favorites
 */
public class InsertFavoriteLoader extends AsyncTaskLoader<Uri> {

    private Movie mMovie;

    public InsertFavoriteLoader(Context context, Movie movie) {
        super(context);
        mMovie = movie;
    }

    @Override
    public Uri loadInBackground() {

        if(mMovie == null)
            return null;

        Uri uri = MoviesContract.FavoriteMovieEntry.buildMovieUri(mMovie.id);

        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_API_ID, mMovie.apiId);
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_TITLE, mMovie.title);
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, mMovie.synopsis);
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getStringFormattedReleaseDate());
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovie.voteAverage);
        cv.put(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER, mMovie.poster);

        return getContext().getContentResolver().insert(uri, cv);
    }
}
