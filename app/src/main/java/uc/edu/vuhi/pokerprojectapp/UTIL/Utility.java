package uc.edu.vuhi.pokerprojectapp.UTIL;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

public final class Utility {

    private Utility(){}
    public static void sendTo(Activity currentActivity, Class<?> otherActivityClass){
        Intent intent = new Intent(currentActivity, otherActivityClass);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    public static void delay(final AlertDialog dialog, int time){
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, time);
    }
}
