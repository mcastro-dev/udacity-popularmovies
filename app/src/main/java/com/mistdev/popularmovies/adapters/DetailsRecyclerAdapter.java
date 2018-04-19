package com.mistdev.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.models.Movie;
import com.mistdev.popularmovies.models.Review;
import com.mistdev.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kastr on 5/08/2016.
 *
 */
public class DetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_MOVIE = 0;
    private final int TYPE_TRAILER = 1;
    private final int TYPE_REVIEW = 2;

    private final int SECTION_MOVIE_SIZE = 1;
    private final int SECTION_TRAILER_SIZE = 1;

    private Context mContext;
    private Movie mMovie;
    private ArrayList<Review> mListReview;

    private TrailersRecyclerAdapter mTrailersRecyclerAdapter;

    public DetailsRecyclerAdapter(Context context, Movie movie, ArrayList<Review> listReview, TrailersRecyclerAdapter trailersRecyclerAdapter) {
        super();
        mContext = context;
        mMovie = movie;
        mListReview = listReview;
        mTrailersRecyclerAdapter = trailersRecyclerAdapter;
    }

    public void setTrailers(ArrayList<Trailer> listTrailer) {

        mTrailersRecyclerAdapter.setTrailers(listTrailer);
        notifyDataSetChanged();
    }

    public void setReviews(ArrayList<Review> listReview) {

        mListReview = listReview;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {

            case 0:
                return TYPE_MOVIE;
            case 1:
                return TYPE_TRAILER;

            default:
                return TYPE_REVIEW;
        }
    }

    @Override
    public int getItemCount() {
        //the movie + trailers + reviews
        return SECTION_MOVIE_SIZE + SECTION_TRAILER_SIZE + mListReview.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case TYPE_MOVIE:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
                return new MovieViewHolder(view);

            case TYPE_TRAILER:

                RecyclerView trailerRecyclerView = (RecyclerView) LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_recycler, parent, false);
                trailerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                trailerRecyclerView.setAdapter(mTrailersRecyclerAdapter);

                return new RecyclerView.ViewHolder(trailerRecyclerView) {};

            case TYPE_REVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
                return new ReviewViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {

            case TYPE_MOVIE:
                MovieViewHolder movieViewHolder = (MovieViewHolder)holder;
                movieViewHolder.set(mContext, mMovie);
                break;

            case TYPE_REVIEW:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder)holder;
                reviewViewHolder.set(mListReview.get(position - (SECTION_MOVIE_SIZE + SECTION_TRAILER_SIZE)));
                break;
        }
    }


    /* VIEW HOLDERS
     * ----------------------------------------------------------------------------------------------------*/
    class MovieViewHolder extends RecyclerView.ViewHolder{

        private ImageView mPoster;
        private TextView mYear;
        private TextView mRating;
        private TextView mSynopsis;

        public MovieViewHolder(View view) {
            super(view);

            mPoster = (ImageView)view.findViewById(R.id.detail_movie_poster);
            mYear = (TextView)view.findViewById(R.id.detail_year);
            mRating = (TextView)view.findViewById(R.id.detail_rating);
            mSynopsis = (TextView)view.findViewById(R.id.detail_overview);
        }

        public void set(Context context, final Movie movie) {
            if(movie == null)
                return;

            String posterLocalPath = Utils.localPosterPathFromUrl(context, movie.poster);
            File posterFile = new File(posterLocalPath);

            //IF poster image file was successfully saved when user favorited use it, otherwise load from url
            if(posterFile.exists())
                Picasso.with(context).load(posterFile).placeholder(R.drawable.placeholder).into(mPoster);
            else
                Picasso.with(context).load(movie.poster).placeholder(R.drawable.placeholder).into(mPoster);

            //mTitle.setText(movie.title);
            mYear.setText(movie.getStringYear());
            mRating.setText(movie.getStringFormattedRating());
            mSynopsis.setText(movie.synopsis);
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView mAuthor;
        private TextView mContent;

        public ReviewViewHolder(View view) {
            super(view);

            mAuthor = (TextView)view.findViewById(R.id.txt_review_author);
            mContent = (TextView)view.findViewById(R.id.txt_review_content);
        }

        public void set(Review review) {
            if(review == null)
                return;

            mAuthor.setText(review.author);
            mContent.setText(review.content);
        }
    }

}
