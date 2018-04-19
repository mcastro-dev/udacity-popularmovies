package com.mistdev.popularmovies.async;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kastr on 5/08/2016.
 *
 */
public class FetchReviewsLoader extends AsyncTaskLoader<List<Review>> {

    private final String LOG_TAG = FetchReviewsLoader.class.getSimpleName();

    private long mMovieApiId;

    public FetchReviewsLoader(Context context, long movieApiId) {
        super(context);
        mMovieApiId = movieApiId;
    }

    @Override
    public List<Review> loadInBackground() {

        if(mMovieApiId == 0)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewsJson = null;

        try {

            Context context = super.getContext();

            //#Get filter from prefs
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final String filter = sharedPrefs.getString(
                    context.getString(R.string.pref_filter_key),
                    context.getString(R.string.pref_filter_popular)
            );

            /*if(filter.equals(context.getString(R.string.pref_filter_favorites)))
                return new ArrayList<>();*/

            //#Build URL
            final String MOVIE_BASE_URL = context.getString(R.string.themoviedb_movie_base_url);
            final String MOVIE_ID = String.valueOf(mMovieApiId);
            final String REVIEWS = "reviews";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MOVIE_ID)
                    .appendPath(REVIEWS)
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
            reviewsJson = builder.toString();

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

        return getReviewsFromJson(reviewsJson);
    }

    /* FROM JSON
     * ------------------------------------------------------------*/
    private List<Review> getReviewsFromJson(String reviewsJsonString) {

        List<Review> listReview = new ArrayList<>();

        try {
            //Names of JSON objects to be extracted
            final String JSON_RESULTS = "results";
            final String JSON_ID = "id";
            final String JSON_AUTHOR = "author";
            final String JSON_CONTENT = "content";
            final String JSON_URL = "url";

            JSONObject reviewsJson = new JSONObject(reviewsJsonString);
            JSONArray resultsArray = reviewsJson.getJSONArray(JSON_RESULTS);

            //Get data for each movie
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject reviewJsonObj = resultsArray.getJSONObject(i);

                String id = reviewJsonObj.getString(JSON_ID);
                String author = reviewJsonObj.getString(JSON_AUTHOR);
                String content = reviewJsonObj.getString(JSON_CONTENT);
                String url = reviewJsonObj.getString(JSON_URL);

                Log.d(LOG_TAG, "###Review url: " + url);

                Review review = new Review(id, author, content, url);

                listReview.add(review);
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting data from JSON", e);
        }

        return listReview;
    }
}
