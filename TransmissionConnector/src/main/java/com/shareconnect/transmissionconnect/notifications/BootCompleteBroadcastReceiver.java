package com.shareconnect.transmissionconnect.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shareconnect.transmissionconnect.TransmissionRemote;

public class BootCompleteBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (TransmissionRemote.getApplication(context).isNotificationEnabled()) {
            BackgroundUpdater.start(context);
        }
    }
}
