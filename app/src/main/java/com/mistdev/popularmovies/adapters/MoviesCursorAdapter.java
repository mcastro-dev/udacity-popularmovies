package com.mistdev.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * Created by kastr on 28/07/2016.
 * Cursor adapter.
 */
public class MoviesCursorAdapter extends CursorAdapter {

    public MoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        String posterUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER));
        Picasso.with(context).load(posterUrl).fetch();

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        onBindViewHolder(context, viewHolder, cursor);
    }

    private void onBindViewHolder(Context context, ViewHolder viewHolder, Cursor cursor) {

        String posterUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_POSTER));
        String posterLocalPath = Utils.localPosterPathFromUrl(context, posterUrl);

        File posterLocalFile = new File(posterLocalPath);

        if(posterLocalFile.exists())
            Picasso.with(context).load(posterLocalFile).placeholder(R.drawable.placeholder).into(viewHolder.poster);
        else
            Picasso.with(context).load(posterUrl).placeholder(R.drawable.placeholder).into(viewHolder.poster);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    public static class ViewHolder {
        public final ImageView poster;

        public ViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }
}
