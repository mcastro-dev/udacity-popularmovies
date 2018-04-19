package com.mistdev.popularmovies.fragments;



import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistdev.popularmovies.MainActivity;
import com.mistdev.popularmovies.adapters.MoviesArrayAdapter;
import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.async.FavoriteMoviesLoader;
import com.mistdev.popularmovies.async.FetchMoviesLoader;
import com.mistdev.popularmovies.models.Movie;
import com.mistdev.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kastr on 23/07/2016.
 * Fragment
 */
public class MoviesGridFragment extends Fragment implements  LoaderManager.LoaderCallbacks<List<Movie>> {

    private MoviesArrayAdapter mMoviesAdapter;
    private ArrayList<Movie> mMoviesArrayList;

    private final String PARC_MOVIES_KEY = "movies";

    public static final int MOVIES_LOADER = 0;
    public static final int FAVORITES_LOADER = 1;

    private InteractionListener mListener;

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    public void setInteractionListener(InteractionListener interactionListener) {
        mListener = interactionListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
            mMoviesArrayList = new ArrayList<Movie>();
        else mMoviesArrayList = savedInstanceState.getParcelableArrayList(PARC_MOVIES_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        if(mMoviesAdapter == null)
            mMoviesAdapter = new MoviesArrayAdapter(getContext(), mMoviesArrayList);

        View emptyView = rootView.findViewById(R.id.empty_view);
        emptyView.setVisibility(View.INVISIBLE);

        GridView gridMovies = (GridView)rootView.findViewById(R.id.grid_movies);
        gridMovies.setAdapter(mMoviesAdapter);
        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(mListener != null)
                    mListener.onItemSelected(mMoviesAdapter.getItem(position));

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if(savedInstanceState == null) {

            if(Utils.getFilterPreference(getContext()).equals(getString(R.string.pref_filter_favorites)))
                getLoaderManager().initLoader(FAVORITES_LOADER, null, this).forceLoad();
            else
                getLoaderManager().initLoader(MOVIES_LOADER, null, this).forceLoad();
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(PARC_MOVIES_KEY, mMoviesArrayList);
        super.onSaveInstanceState(outState);
    }

    private void updateEmptyView(ListDataState dataState) {

        if(getView() != null) {

            GridView gridMovies = (GridView)getView().findViewById(R.id.grid_movies);
            View emptyView = getView().findViewById(R.id.empty_view);

            //List is null (due to server issues or network issues)
            if(dataState == ListDataState.DATA_NULL) {

                emptyView.setVisibility(View.VISIBLE);
                gridMovies.setEmptyView(emptyView);

                ImageView emptyViewImage = (ImageView)emptyView.findViewById(R.id.img_empty_view);
                TextView emptyViewText = (TextView)emptyView.findViewById(R.id.txt_empty_view);

                boolean hasNetwork = Utils.isNetworkAvailable(getContext());
                if(!hasNetwork) {
                    emptyViewImage.setImageResource(R.drawable.ic_signal_wifi_off_white_36dp);
                    emptyViewText.setText(getString(R.string.empty_list_message_no_internet));

                } else {
                    emptyViewImage.setImageResource(0);
                    emptyViewText.setText(getString(R.string.empty_list_message_server_or_internet_issue));
                }

                //List is not null but is empty
            } else if(dataState == ListDataState.DATA_EMPTY) {

                emptyView.setVisibility(View.VISIBLE);
                gridMovies.setEmptyView(emptyView);

                ImageView emptyViewImage = (ImageView)emptyView.findViewById(R.id.img_empty_view);
                TextView emptyViewText = (TextView)emptyView.findViewById(R.id.txt_empty_view);

                emptyViewImage.setImageResource(0);
                emptyViewText.setText(getString(R.string.empty_list_message_no_data));

            } else if(dataState == ListDataState.DATA_NOT_EMPTY) {
                gridMovies.setEmptyView(null);
                emptyView.setVisibility(View.GONE);
            }

        }

    }

    private enum ListDataState {

        //Means the http request failed because of internet connection or server issues
        DATA_NULL,
        //Means the request was successfull but returned no data
        DATA_EMPTY,
        //Means the request was successful and returned data
        DATA_NOT_EMPTY
    }


    /* LISTENER
     * ----------------------------------------------------------------------------------*/
    public interface InteractionListener {

        void onItemSelected(Movie movie);
    }


    /* LOADER
     * ----------------------------------------------------------------------------------*/
    public void restartLoader(int filter) {
        mMoviesAdapter.clear();

        if(filter == MainActivity.PREF_FAVORITES)
            getLoaderManager().restartLoader(MoviesGridFragment.FAVORITES_LOADER, null, this).forceLoad();
        else
            getLoaderManager().restartLoader(MoviesGridFragment.MOVIES_LOADER, null, this).forceLoad();
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        if(id == FAVORITES_LOADER)
            return new FavoriteMoviesLoader(getContext());
        else
            return new FetchMoviesLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, final List<Movie> data) {

        if(data == null) {
            updateEmptyView(ListDataState.DATA_NULL);
            return;

        } else if(data.isEmpty()) {
            updateEmptyView(ListDataState.DATA_EMPTY);

        } else {
            updateEmptyView(ListDataState.DATA_NOT_EMPTY);
        }


        if(mMoviesAdapter == null)
            return;

        mMoviesAdapter.clear();

        for(Movie movie : data) {
            mMoviesAdapter.add(movie);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}
