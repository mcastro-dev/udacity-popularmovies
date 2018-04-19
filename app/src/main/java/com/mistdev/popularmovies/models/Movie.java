package com.mistdev.popularmovies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.data.MoviesContract;

import java.util.Calendar;

/**
 * Created by kastr on 23/07/2016.
 * Movie class
 */
public class Movie implements Parcelable {

    private static final String LOG_TAG = Movie.class.getSimpleName();

    public long id;
    public long apiId;
    public String title;
    public String synopsis;
    public Calendar releaseDate;
    public String poster;
    public double voteAverage;

    public Movie() {

    }

    public Movie(long id, long apiId, String title, String synopsis, Calendar releaseDate, String poster, double voteAverage) {
        this.id = id;
        this.apiId = apiId;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.voteAverage = voteAverage;
    }

    public static Movie movieFromCursor(Cursor cursor) {
        Movie movie = new Movie();

        try {

            final int INDEX_ID = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry._ID);
            final int INDEX_API_ID = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_API_ID);
            final int INDEX_TITLE = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_TITLE);
            final int INDEX_SYNOPSIS = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS);
            final int INDEX_RELEASE_DATE = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE);
            final int INDEX_VOTE_AVERAGE = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE);
            final int INDEX_POSTER = cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER);

            long id = cursor.getLong(INDEX_ID);
            long apiId = cursor.getLong(INDEX_API_ID);
            String title = cursor.getString(INDEX_TITLE);
            String synopsis = cursor.getString(INDEX_SYNOPSIS);
            Calendar releaseDate = Utils.stringToCalendar(cursor.getString(INDEX_RELEASE_DATE));
            double voteAverage = cursor.getDouble(INDEX_VOTE_AVERAGE);
            String posterPath = cursor.getString(INDEX_POSTER);

            movie = new Movie(id, apiId, title, synopsis, releaseDate, posterPath, voteAverage);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating movie from cursor");
        }

        return movie;
    }

    public String getStringFormattedRating() {
        return String.format("%s/10", voteAverage);
    }

    public String getStringFormattedReleaseDate() {
        return releaseDate.get(Calendar.YEAR) + "-" + releaseDate.get(Calendar.MONTH) + "-" + releaseDate.get(Calendar.DAY_OF_MONTH);
    }

    public String getStringYear() {
        return String.valueOf(releaseDate.get(Calendar.YEAR));
    }


    /* PARCELABLE
     * ---------------------------------------------------------------------------------*/
    public Movie(Parcel in){
        this.id = in.readLong();
        this.apiId = in.readLong();
        this.title = in.readString();
        this.synopsis = in.readString();
        long releaseDate = in.readLong();
        this.poster = in.readString();
        this.voteAverage = in.readDouble();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(releaseDate);
        this.releaseDate = calendar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(apiId);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeLong(releaseDate.getTimeInMillis());
        dest.writeString(poster);
        dest.writeDouble(voteAverage);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
