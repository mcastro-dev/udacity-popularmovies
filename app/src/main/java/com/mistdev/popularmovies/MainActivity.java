package com.mistdev.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.mistdev.popularmovies.fragments.DetailsFragment;
import com.mistdev.popularmovies.fragments.MoviesGridFragment;
import com.mistdev.popularmovies.models.Movie;

public class MainActivity extends AppCompatActivity
        implements MoviesGridFragment.InteractionListener, DetailsFragment.OnDetailInteracted {

    public static final int MAIN_REQUEST_CODE = 1;

    public static final int PREF_MOST_POPULAR = 0;
    public static final int PREF_TOP_RATED = 1;
    public static final int PREF_FAVORITES = 2;

    private static final String DETAILS_FRAGMENT_TAG = "DETAILSTAG";

    private Spinner mSpinnerFilter;
    private boolean mSpinnerShouldReloadData;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MoviesGridFragment moviesGridFragment = (MoviesGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies_grid);
        moviesGridFragment.setInteractionListener(this);

        if(findViewById(R.id.container_main) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {

                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setListener(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_main, detailsFragment, DETAILS_FRAGMENT_TAG)
                        .commit();

            }

        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_filter, menu);

        MenuItem item = menu.findItem(R.id.spinner_filter);
        mSpinnerFilter = (Spinner) MenuItemCompat.getActionView(item);
        setFilterSpinner(mSpinnerFilter);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(mSpinnerFilter == null)
            return;

        if(requestCode == MAIN_REQUEST_CODE && mSpinnerFilter.getSelectedItemPosition() == PREF_FAVORITES) {
            reloadMoviesData(PREF_FAVORITES);
        }
    }

    /* SPINNER
             * ------------------------------------------------------------*/
    private void setFilterSpinner(Spinner spinner) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if(mSpinnerShouldReloadData) {
                    setFilterPreference(position);
                    reloadMoviesData(position);
                }
                mSpinnerShouldReloadData = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        String filter = Utils.getFilterPreference(this);

        if(filter.equals(getString(R.string.pref_filter_toprated)))
            spinner.setSelection(PREF_TOP_RATED);
        else if(filter.equals(getString(R.string.pref_filter_favorites)))
            spinner.setSelection(PREF_FAVORITES);
    }

    /* FILTER PREFERENCES
     * ------------------------------------------------------------*/
    private void setFilterPreference(int position) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(position == PREF_MOST_POPULAR)
            editor.putString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_popular));
        else if(position == PREF_TOP_RATED)
            editor.putString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_toprated));
        else if(position == PREF_FAVORITES)
            editor.putString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_favorites));

        editor.apply();
    }

    /* MOVIES DATA
     * ------------------------------------------------------------*/
    private void reloadMoviesData(int filter) {

        FragmentManager manager = getSupportFragmentManager();
        MoviesGridFragment fragment = (MoviesGridFragment) manager.findFragmentById(R.id.fragment_movies_grid);

        fragment.restartLoader(filter);
    }


    /* MOVIES GRID FRAGMENT LISTENER
     * ------------------------------------------------------------*/
    @Override
    public void onItemSelected(Movie movie) {

        if(mTwoPane){

            DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
            detailsFragment.setListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_main, detailsFragment, DETAILS_FRAGMENT_TAG)
                    .commit();

        } else {

            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, movie);
            startActivityForResult(intent, MAIN_REQUEST_CODE);
        }
    }

    /*private void transactToDetailsFragmentWithMovie(Movie movie) {

        DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
        detailsFragment.setListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, detailsFragment, DETAILS_FRAGMENT_TAG)
                .commit();
    }*/


    /* DETAILS FRAGMENT LISTENER
     * ------------------------------------------------------------*/
    @Override
    public void onFavoriteChanged() {

        if(!mTwoPane)
            return;

        if(mSpinnerFilter.getSelectedItemPosition() == PREF_FAVORITES) {
            reloadMoviesData(PREF_FAVORITES);
        }
    }


}
