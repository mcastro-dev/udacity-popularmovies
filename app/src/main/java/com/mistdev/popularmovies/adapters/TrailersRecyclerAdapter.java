package com.mistdev.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kastr on 5/08/2016.
 *
 */
public class TrailersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Trailer> mListTrailer;

    private OnItemClickListener onItemClickListener;

    public TrailersRecyclerAdapter(Context context, ArrayList<Trailer> listTrailer) {
        super();
        mContext = context;
        mListTrailer = listTrailer;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setTrailers(ArrayList<Trailer> listTrailer) {
        this.mListTrailer = listTrailer;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Trailer trailer = mListTrailer.get(position);

        TrailerViewHolder trailerViewHolder = (TrailerViewHolder)holder;
        trailerViewHolder.set(mContext, trailer);
        trailerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null)
                    onItemClickListener.onTrailerClicked(trailer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListTrailer.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{

        private ImageView mThumbnail;
        private TextView mName;

        public TrailerViewHolder(View view) {
            super(view);

            mThumbnail = (ImageView)view.findViewById(R.id.img_trailer_thumbnail);
            mName = (TextView)view.findViewById(R.id.txt_trailer_name);
        }

        public void set(Context context, Trailer trailer) {
            if(trailer == null)
                return;

            Picasso.with(context).load(trailer.thumbnailUrl).into(mThumbnail);
            mName.setText(trailer.name);
        }
    }

    /* LISTENER
     * ----------------------------------------------------------------------------------------------------*/
    public interface OnItemClickListener {
        void onTrailerClicked(Trailer trailer);
    }

}
