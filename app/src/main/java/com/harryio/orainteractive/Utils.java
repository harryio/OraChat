package com.harryio.orainteractive;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private Utils() {
    }

    /**
     * Get {@link Intent} which can be used to launch activity with no history or with no backstack
     *
     * @param context              Context
     * @param toBeLaunchedActivity class of the activity is to be launched with no history
     */
    public static Intent getClearStackIntent(Context context, Class toBeLaunchedActivity) {
        Intent intent = new Intent(context, toBeLaunchedActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Helper method to show short duration {@link Toast} message
     * @param context Context
     * @param message message to be shown
     */
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will return human readable relative date string from the supplied date string.
     */
    public static String getSimpleDateString(String dateString) {
        try {
            Date date = getParsedDate(dateString);
            Date now = new Date();

            return DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(),
                    DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Determine if the supplied date string is today's date
     */
    public static boolean isToday(String dateString) {
        try {
            Date date = getParsedDate(dateString);

            return DateUtils.isToday(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Parse date string in the format yyyy-MM-ddTHH:mm:ssZ and returns {@link Date} instance
     * @param dateString Date string in format yyyy-MM-ddTHH:mm:ssZ
     */
    private static Date getParsedDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.parse(dateString);
    }

    /**
     * Check if internet connection is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
