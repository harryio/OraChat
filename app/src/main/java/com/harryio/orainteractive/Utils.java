package com.harryio.orainteractive;

import android.content.Context;
import android.content.Intent;
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

    public static Intent getClearStackIntent(Context context, Class toBeLaunchedActivity) {
        Intent intent = new Intent(context, toBeLaunchedActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

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

    public static boolean isToday(String dateString) {
        try {
            Date date = getParsedDate(dateString);

            return DateUtils.isToday(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Date getParsedDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.parse(dateString);
    }
}
