package com.mistdev.popularmovies.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.adapters.DetailsRecyclerAdapter;
import com.mistdev.popularmovies.adapters.TrailersRecyclerAdapter;
import com.mistdev.popularmovies.async.DeleteFavoriteLoader;
import com.mistdev.popularmovies.async.FetchReviewsLoader;
import com.mistdev.popularmovies.async.FetchTrailersLoader;
import com.mistdev.popularmovies.async.InsertFavoriteLoader;
import com.mistdev.popularmovies.async.IsFavoriteLoader;
import com.mistdev.popularmovies.data.MoviesContract;
import com.mistdev.popularmovies.models.Movie;
import com.mistdev.popularmovies.models.Review;
import com.mistdev.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    public static final String ARG_MOVIE = "arg_movie";

    private Movie mMovie;
    private FloatingActionButton mFabFavorite;

    private DetailsRecyclerAdapter mDetailsRecyclerAdapter;

    private OnDetailInteracted mListener;

    public static final int DETAILS_LOADER = 0;
    public static final int VIDEOS_LOADER = 1;
    public static final int REVIEWS_LOADER = 2;
    public static final int INSERT_FAVORITE_LOADER = 3;
    public static final int DELETE_FAVORITE_LOADER = 4;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Movie movie) {

        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
    }

    public void setListener(OnDetailInteracted listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMovie = new Movie();

        View view = inflater.inflate(R.layout.fragment_details, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rcy_details);

        mFabFavorite = (FloatingActionButton)view.findViewById(R.id.fab_details_favorite);
        mFabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleFavorite(mFabFavorite, mMovie);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(dy > 0) {
                    mFabFavorite.hide();
                } else if(dy <= 0) {
                    mFabFavorite.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if(getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);

            TextView title = (TextView)view.findViewById(R.id.txt_details_title);
            title.setText(mMovie.title);

            TrailersRecyclerAdapter trailersRecyclerAdapter = new TrailersRecyclerAdapter(getContext(), new ArrayList<Trailer>());
            trailersRecyclerAdapter.setOnItemClickListener(new TrailersRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onTrailerClicked(Trailer trailer) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.videoUrl)));
                }
            });

            mDetailsRecyclerAdapter = new DetailsRecyclerAdapter(getContext(), mMovie, new ArrayList<Review>(), trailersRecyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mDetailsRecyclerAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateEmptyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(VIDEOS_LOADER, null, new VideoCallback()).forceLoad();
        getLoaderManager().initLoader(REVIEWS_LOADER, null, new ReviewCallback()).forceLoad();
        getLoaderManager().initLoader(DETAILS_LOADER, null, new FavoriteCallback()).forceLoad();
    }

    private void handleFavorite(FloatingActionButton button, Movie movie) {

        if(button == null || movie.apiId < 1) return;

        //not favorite
        if(movie.id < 1) {
            button.setImageResource(R.drawable.ic_favorite_white_24dp);
            addMovieToFavorite(movie);

        } else {
            button.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            removeMovieFromFavorite(movie);
        }
    }

    //Add to favorite
    private void addMovieToFavorite(final Movie movie) {

        //copy the poster from the internet to the local storage
        Picasso.with(getContext()).load(movie.poster).into(Utils.getPicassoTargetForSavingFile(getContext(), movie.poster));

        //update as favorite in SQLite
        getLoaderManager().initLoader(INSERT_FAVORITE_LOADER, null, new LoaderManager.LoaderCallbacks<Uri>() {

            @Override
            public Loader<Uri> onCreateLoader(int id, Bundle args) {
                return new InsertFavoriteLoader(getContext(), movie);
            }

            @Override
            public void onLoadFinished(Loader<Uri> loader, Uri data) {

                //get id from response and if succeeded set the favorite icon
                if(data != null && mFabFavorite != null) {
                    long responseMovieId = ContentUris.parseId(data);

                    if(responseMovieId > 0) {
                        mMovie.id = responseMovieId;

                        if(mFabFavorite != null)
                            mFabFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);

                        if(mListener != null)
                            mListener.onFavoriteChanged();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Uri> loader) {

            }
        }).forceLoad();
    }

    //Remove from favorite
    private void removeMovieFromFavorite(final Movie movie) {

        String posterLocalPath = Utils.localPosterPathFromUrl(getContext(), movie.poster);
        File posterFile = new File(posterLocalPath);

        if(posterFile.exists())
            posterFile.delete();

        getLoaderManager().initLoader(DELETE_FAVORITE_LOADER, null, new LoaderManager.LoaderCallbacks<Integer>() {

            @Override
            public Loader<Integer> onCreateLoader(int id, Bundle args) {
                return new DeleteFavoriteLoader(getContext(), movie.id);
            }

            @Override
            public void onLoadFinished(Loader<Integer> loader, Integer data) {

                //if deleted, change favorite icon
                if(data > 0) {
                    mMovie.id = 0;

                    if(mFabFavorite != null)
                        mFabFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);

                    if(mListener != null)
                        mListener.onFavoriteChanged();
                }
            }

            @Override
            public void onLoaderReset(Loader<Integer> loader) {

            }
        }).forceLoad();

    }

    private void updateEmptyView() {

        if(getView() != null) {

            View emptyView = getView().findViewById(R.id.details_empty_view);

            if(mMovie == null || mMovie.apiId < 1)
                emptyView.setVisibility(View.VISIBLE);
            else emptyView.setVisibility(View.GONE);
        }

    }


    /* CALLBACKS
     * ----------------------------------------------------------------------------------*/
    //Movie
    private class FavoriteCallback implements LoaderManager.LoaderCallbacks<Movie> {

        @Override
        public Loader<Movie> onCreateLoader(int id, Bundle args) {

            return new IsFavoriteLoader(getContext(), mMovie.apiId);
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie data) {

            if(data == null)
                return;

            if(data.apiId == mMovie.apiId && mFabFavorite != null) {

                mMovie.id = data.id;
                mFabFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

        }
    }

    //Video
    private class VideoCallback implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
            return new FetchTrailersLoader(getContext(), mMovie.apiId);
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {

            if (data != null)
                mDetailsRecyclerAdapter.setTrailers((ArrayList<Trailer>) data);

        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    }

    //Review
    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<Review>> {

        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            return new FetchReviewsLoader(getContext(), mMovie.apiId);
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {

            if (data != null)
                mDetailsRecyclerAdapter.setReviews((ArrayList<Review>) data);
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    }


    /* LISTENER
     * ----------------------------------------------------------------------------------*/
    public interface OnDetailInteracted {

        void onFavoriteChanged();
    }

}
