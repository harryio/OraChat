package com.harryio.orainteractive;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
}
