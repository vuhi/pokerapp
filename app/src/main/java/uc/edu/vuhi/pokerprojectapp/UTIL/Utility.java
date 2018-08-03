package uc.edu.vuhi.pokerprojectapp.UTIL;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import uc.edu.vuhi.pokerprojectapp.LoginActivity;
import uc.edu.vuhi.pokerprojectapp.MainActivity;

public final class Utility {

    private Utility(){}
    public static void sendTo(Activity currentActivity, Class<?> otherActivityClass, boolean finish){
        Intent intent = new Intent(currentActivity, otherActivityClass);
        currentActivity.startActivity(intent);
        if (finish == true){
            currentActivity.finish();
        }
    }

    public static void delay(final AlertDialog dialog, int time, Runnable myFunction){
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
                handler.removeCallbacks(myFunction);
            }
        });
        handler.postDelayed(runnable, time);
        if(myFunction != null){
            handler.postDelayed(myFunction, time);
        }
    }
}
