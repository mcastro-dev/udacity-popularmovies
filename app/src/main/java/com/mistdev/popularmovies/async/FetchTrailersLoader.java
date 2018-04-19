package com.mistdev.popularmovies.async;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mistdev.popularmovies.R;
import com.mistdev.popularmovies.models.Trailer;

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
 * Created by kastr on 4/08/2016.
 * Loader for movies videos
 */
public class FetchTrailersLoader extends AsyncTaskLoader<List<Trailer>> {

    private final String LOG_TAG = FetchTrailersLoader.class.getSimpleName();
    private long mMovieApiId;

    public FetchTrailersLoader(Context context, long movieApiId) {
        super(context);
        mMovieApiId = movieApiId;
    }

    @Override
    public List<Trailer> loadInBackground() {

        if(mMovieApiId == 0)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String videosJson = null;

        try {

            Context context = super.getContext();

            //#Get filter from prefs
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final String filter = sharedPrefs.getString(
                    context.getString(R.string.pref_filter_key),
                    context.getString(R.string.pref_filter_popular)
            );

            //#Build URL
            final String MOVIE_BASE_URL = context.getString(R.string.themoviedb_movie_base_url);
            final String MOVIE_ID = String.valueOf(mMovieApiId);
            final String VIDEOS = "videos";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MOVIE_ID)
                    .appendPath(VIDEOS)
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
            videosJson = builder.toString();

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

        return getVideosFromJson(videosJson);
    }

    /* FROM JSON
     * ------------------------------------------------------------*/
    private List<Trailer> getVideosFromJson(String videosJsonString) {

        List<Trailer> listVideosUrl = new ArrayList<>();

        try {
            //Names of JSON objects to be extracted
            final String JSON_RESULTS = "results";
            final String JSON_VIDEO_KEY = "key";
            final String JSON_VIDEO_NAME = "name";

            JSONObject videosJson = new JSONObject(videosJsonString);
            JSONArray resultsArray = videosJson.getJSONArray(JSON_RESULTS);

            //Get data for each movie
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieJsonObj = resultsArray.getJSONObject(i);

                String VIDEO_PATH_BASE_URL = super.getContext().getString(R.string.youtube_videos_base_url);
                String THUMBNAIL_PATH_BASE_URL = super.getContext().getString(R.string.youtube_video_thumbnail_base_url);

                String videoKey = movieJsonObj.getString(JSON_VIDEO_KEY);

                String videoUrl = VIDEO_PATH_BASE_URL + videoKey;
                String thumbnailUrl = THUMBNAIL_PATH_BASE_URL + videoKey + "/0.jpg";
                String name = movieJsonObj.getString(JSON_VIDEO_NAME);

                Log.d(LOG_TAG, "###Trailer URL: " + videoUrl);

                Trailer trailer = new Trailer(name, videoUrl, thumbnailUrl);

                listVideosUrl.add(trailer);
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting data from JSON", e);
        }

        return listVideosUrl;
    }
}
