package com.sprint.lock.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppUtils {
    private static final String PREFS_NAME = "AppExpiryPrefs";
    private static final String KEY_FIRST_LAUNCH_TIME = "first_launch_time";

    // 记录首次启动时间（如果未记录）
    public static void recordFirstLaunchTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_FIRST_LAUNCH_TIME)) {
            long currentTime = System.currentTimeMillis();
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, currentTime).apply();
        }
    }

    // 检查是否过期（60天后）
    public static boolean isExpired(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long firstLaunchTime = prefs.getLong(KEY_FIRST_LAUNCH_TIME, 0);
        if (firstLaunchTime == 0) return false; // 未记录，默认未过期

        long currentTime = System.currentTimeMillis();
        long threeDaysInMillis = 60 * 24 * 60 * 60 * 1000L; // 60天的毫秒数
        return (currentTime - firstLaunchTime) > threeDaysInMillis;
    }
}