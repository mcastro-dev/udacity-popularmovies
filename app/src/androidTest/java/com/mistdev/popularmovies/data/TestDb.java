package com.mistdev.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.ArrayList;

/**
 * Created by kastr on 26/07/2016.
 * Test database
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void setUp() {
        deleteTheDatabase();
    }

    //Start with a clean state
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DB_NAME);
    }

    ArrayList<String> getTables() {
        final ArrayList<String> tablesNames = new ArrayList<>();
        tablesNames.add(MoviesContract.FavoriteMovieEntry.TABLE_NAME);

        return tablesNames;
    }

    public void testCreateDb() throws Throwable {
        //Build a HashSet of all of the table names we wish to look for
        final ArrayList<String> tablesNames = getTables();

        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tablesNames.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Database was created without some or any tables registered in contract",
                tablesNames.isEmpty());

        c.close();
        db.close();
    }

    //TODO: test insert dummy data and then test the query
    /*public void testGetAll() {

        final ArrayList<String> tablesNames = getTables();

        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = null;

        for(String table : tablesNames) {

            c = db.query(
                    table,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null  // sort order
            );

            //Move the cursor to the first valid database row and check to see if we have any rows
            assertTrue( String.format("Error: No Records returned from %s GetAll query", table), c.moveToFirst() );
        }

        if(c != null) c.close();
        dbHelper.close();
    }*/



}