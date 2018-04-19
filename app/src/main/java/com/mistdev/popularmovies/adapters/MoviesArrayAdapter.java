package com.mistdev.popularmovies.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.Utils;
import com.mistdev.popularmovies.models.Movie;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kastr on 23/07/2016.
 * ArrayAdapter
 *
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    public MoviesArrayAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Movie movie = getItem(position);
        ViewHolder holder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder)convertView.getTag();

        String posterLocalPath = Utils.localPosterPathFromUrl(getContext(), movie.poster);
        File posterFile = new File(posterLocalPath);

        //IF image is saved in internal storage use it, otherwise load from url
        if(posterFile.exists())
            Picasso.with(getContext()).load(posterFile).placeholder(R.drawable.placeholder).into(holder.mImage);
        else
            Picasso.with(getContext()).load(movie.poster).placeholder(R.drawable.placeholder).into(holder.mImage);

        return convertView;
    }

    class ViewHolder {

        private ImageView mImage;

        public ViewHolder(View view) {
            mImage = (ImageView)view.findViewById(R.id.movie_poster);
        }

    }
}
