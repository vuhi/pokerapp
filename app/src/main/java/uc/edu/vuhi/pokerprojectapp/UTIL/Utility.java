package uc.edu.vuhi.pokerprojectapp.UTIL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public final class Utility {

    private Utility(){}
    public static void sendTo(Activity currentActivity, Class<?> otherActivityClass){
        Intent intent = new Intent(currentActivity, otherActivityClass);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}
