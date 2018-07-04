package com.squadron.philip.journaly;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by philip on 04/07/2018.
 */

public class Notification {

    private static final String CHANNEL_ID = "jay";

    public NotificationCompat.Builder getNotiv(Context context, String title, String content, int priority){
        return (new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT));
    }
}
