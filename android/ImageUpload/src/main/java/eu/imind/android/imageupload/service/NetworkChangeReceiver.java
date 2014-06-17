package eu.imind.android.imageupload.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static eu.imind.android.imageupload.util.Util.*;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnectionOn(context)) {
            context.startService(new Intent(context, ImageUploadService.class));
        }
    }
}
