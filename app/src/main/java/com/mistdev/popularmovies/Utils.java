package com.mistdev.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kastr on 28/07/2016.
 * Utils
 */
public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    public static Calendar stringToCalendar(String date) {

        Calendar calendar= Calendar.getInstance();

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(dateFormat.parse(date));

        } catch (ParseException e) {
            Log.d(LOG_TAG, "Error converting string to calendar", e);
        }
        return calendar;
    }

    public static String getFilterPreference(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.pref_filter_key),
                context.getString(R.string.pref_filter_popular)
        );
    }

    public static void saveBitmap(Context context, String fileName, Bitmap data) {

        if(fileName == null || data == null)
            return;

        try {

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getRowBytes());
            fos.close();

        } catch (Exception e) {
            Log.d(LOG_TAG, "Error saving file: " + fileName, e);
            e.printStackTrace();
        }
    }


    /*Picasso.with(context).load(posterUrl).into(Utils.getPicassoTargetForSavingFile(context, posterUrl));*/
    public static Target getPicassoTargetForSavingFile(final Context context, String fileUrl) {

        final String nFileName = localPosterPathFromUrl(context, fileUrl);

        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(nFileName);
                        try {
                            boolean didCreateFile = file.createNewFile();

                            if(!didCreateFile)
                                return;

                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(LOG_TAG, "### Picasso failed saving the image to the device's internal storage");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };
    }

    public static String localPosterPathFromUrl(Context context, String posterUrl) {
        String filename = removeHttpPrefixFromFilename(posterUrl);
        filename = changeExtensionTo(filename, "png");

        return context.getFilesDir() + "/" + filename;
    }

    private static String changeExtensionTo(String filename, String extension) {

        int lastIndexOfDot = filename.lastIndexOf(".");
        return filename.substring(0, lastIndexOfDot) + "." + extension;
    }

    private static String removeHttpPrefixFromFilename(String filename) {

        int lastIndexOfSlash = filename.lastIndexOf("/");
        return (lastIndexOfSlash < filename.length()) ? filename.substring(lastIndexOfSlash + 1) : filename.substring(lastIndexOfSlash);
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
