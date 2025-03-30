package com.sprint.lock.app.widge;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class RebootUtils {
    public static void rebootSystem(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        powerManager.reboot("Restart");
    }
}
