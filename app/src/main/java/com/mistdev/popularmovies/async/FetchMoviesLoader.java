package com.mistdev.popularmovies.async;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mistdev.popularmovies.models.Movie;
import com.mistdev.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kastr on 23/07/2016.
 * Get movies data from API
 *
 */
public class FetchMoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private final String LOG_TAG = FetchMoviesLoader.class.getSimpleName();

    public FetchMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJson = null;

        try {

            Context context = super.getContext();

            //#Get filter from prefs
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final String filter = sharedPrefs.getString(
                    context.getString(R.string.pref_filter_key),
                    context.getString(R.string.pref_filter_popular)
            );

            if(filter.equals(context.getString(R.string.pref_filter_favorites)))
                return new ArrayList<>();

            //#Build URL
            final String MOVIE_BASE_URL = context.getString(R.string.themoviedb_movie_base_url);
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(filter)
                    .appendQueryParameter(API_KEY, context.getString(R.string.themoviedb_api_key))
                    .build();

            Log.d(LOG_TAG, "###URL: " + builtUri.toString());
            URL url = new URL(builtUri.toString());

            //#Connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //#Stream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                Log.d(LOG_TAG, "inputStream == null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //#Read
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                Log.d(LOG_TAG, "builder.length() == 0");
                return null;
            }
            moviesJson = builder.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return getMoviesDataFromJson(moviesJson);
    }

    /* FROM JSON
     * ------------------------------------------------------------*/
    private List<Movie> getMoviesDataFromJson(String moviesJsonString) {

        List<Movie> listMovies = new ArrayList<>();

        try {
            //Names of JSON objects to be extracted
            final String JSON_RESULTS = "results";
            final String JSON_MOVIE_API_ID = "id";
            final String JSON_POSTER_PATH = "poster_path";
            final String JSON_OVERVIEW = "overview";
            final String JSON_RELEASE_DATE = "release_date";
            final String JSON_ORIGINAL_TITLE = "original_title";
            final String JSON_VOTE_AVERAGE = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray resultsArray = moviesJson.getJSONArray(JSON_RESULTS);

            //Get data for each movie
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieJsonObj = resultsArray.getJSONObject(i);

                String POSTER_PATH_BASE_URL = super.getContext().getString(R.string.themoviedb_poster_base_url);

                String posterPath = movieJsonObj.getString(JSON_POSTER_PATH);
                posterPath = posterPath.replace("/", "");
                posterPath = Uri.parse(POSTER_PATH_BASE_URL).buildUpon()
                        .appendPath("w342")
                        .appendPath(posterPath)
                        .build().toString();

                long movieApiId = movieJsonObj.getLong(JSON_MOVIE_API_ID);
                String overview = movieJsonObj.getString(JSON_OVERVIEW);
                String releaseDateStr = movieJsonObj.getString(JSON_RELEASE_DATE);
                String originalTitle = movieJsonObj.getString(JSON_ORIGINAL_TITLE);
                double voteAverage = movieJsonObj.getDouble(JSON_VOTE_AVERAGE);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(dateFormat.parse(releaseDateStr));

                listMovies.add(new Movie(0, movieApiId, originalTitle, overview, calendar, posterPath, voteAverage));
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting data from JSON", e);
        }

        return listMovies;
    }


}
