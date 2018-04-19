package com.mistdev.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by kastr on 26/07/2016.
 * Provider test
 */
public class TestProvider extends AndroidTestCase {

    private final String LOG_TAG = TestProvider.class.getSimpleName();

    //This test checks to make sure that the content provider is registered correctly.
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the provider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(), MoviesProvider.class.getName());
        try
        {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the contract
            assertEquals("Error: Provider registered with authority: " + providerInfo.authority +" instead of authority: " + MoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);

        }
        catch (PackageManager.NameNotFoundException e)
        {
            assertTrue("Error: Provider not registered at " + mContext.getPackageName(), false);
        }
    }



    private void testFavorite(long movieId) {

        ContentResolver contentResolver = mContext.getContentResolver();
        assertTrue("Error: couldn't get the ContentResolver", contentResolver != null);

        //# Favorite
        Uri uri = MoviesContract.FavoriteMovieEntry.buildMovieUri(movieId);

        Uri favoriteUri = contentResolver.insert(uri, null);
        long favoriteId = ContentUris.parseId(favoriteUri);
        assertTrue(favoriteId > 0);

        //favorite with movieId and local poster path
        Cursor cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No data from FavoriteEntry", cursor.moveToFirst());

        int deletedRowsCount = contentResolver.delete(
                MoviesContract.FavoriteMovieEntry.buildMovieUri(movieId),
                null,
                null
        );
        assertTrue("Error: No data has been deleted from FavoriteEntry", deletedRowsCount > 0);
        Log.d(LOG_TAG, String.format("Deleted %d row(s) from favorite", deletedRowsCount));

        cursor.close();
    }



    /* INSERTS
     * ----------------------------------------------------------------------------------------------------*/
    public void testInserts() {

        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertTrue("Error: Couldn't open the database", db.isOpen());

        ContentResolver contentResolver = mContext.getContentResolver();
        assertTrue("Error: couldn't get the ContentResolver", contentResolver != null);

        //# Movie
        ContentValues movieTestValues = new ContentValues();
        movieTestValues.put(MoviesContract.FavoriteMovieEntry.COLUMN_TITLE, "Testing movie title");
        movieTestValues.put(MoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, "Testing movie synopsis");
        movieTestValues.put(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, "2016-07-26 00:00:00");
        movieTestValues.put(MoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, 9.5);
        movieTestValues.put(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER, "test/poster.png");

        Uri movieUri = contentResolver.insert(MoviesContract.FavoriteMovieEntry.CONTENT_URI,movieTestValues);
        long movieId = ContentUris.parseId(movieUri);
        assertTrue(movieId > 0);

        //# Favorite
        testFavorite(movieId);

        //
        db.close();
    }


    /* QUERIES
     * ----------------------------------------------------------------------------------------------------*/
    public void testQueries() {

        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getReadableDatabase();
        assertTrue("Error: Couldn't open the database", db.isOpen());

        ContentResolver contentResolver = mContext.getContentResolver();
        assertTrue("Error: couldn't get the ContentResolver", contentResolver != null);

        Cursor cursor = contentResolver.query(
                MoviesContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No data from FavoriteMovieEntry CONTENT_URI", cursor.moveToFirst());

        cursor.close();
    }


    /* UPDATES
     * ----------------------------------------------------------------------------------------------------*/


    /* DELETES
     * ----------------------------------------------------------------------------------------------------*/

}
