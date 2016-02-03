package com.example.android.wearable.jumpingjack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ruiwang on 8/26/15.
 */

public class EMAAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        startPSM(context);
    }

    // start the stress meter
    private void startPSM(Context context) {
        Intent emaIntent = new Intent(context, MainActivity.class);
        emaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(emaIntent);
    }
}