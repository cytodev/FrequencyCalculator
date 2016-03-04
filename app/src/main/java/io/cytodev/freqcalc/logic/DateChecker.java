package io.cytodev.freqcalc.logic;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * io.cytodev.freqcalc.logic "Frequency Calculator"
 * 2016/03/04 @ 10:09
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class DateChecker {
    private static final String TAG = DateChecker.class.getSimpleName();

    /**
     * Checks the file modification time of the application to determine the last time it was updated. Only used when the installation time could not be determined.
     *
     * @param packageManager PackageManager
     * @param packageName String packageName the name of the application to look for
     * @return Date object of the last update time (file modification time) or null
     */
    private static Date updateTime(PackageManager packageManager, String packageName) {
        Log.d(TAG, "Checking for app update time");

        try {
            ApplicationInfo info    = packageManager.getApplicationInfo(packageName, 0);
            File            apkFile = new File(info.sourceDir);

            if(!apkFile.exists()) {
                Log.wtf(TAG, "The package is not installed??? WTF!?");
                return null;
            }

            return new Date(apkFile.lastModified());
        } catch(Exception e) {
            Log.w(TAG, "Could not determine update time", e);
        }

        return null;
    }

    /**
     * Checks the package info for an install time field.
     *
     * @param packageManager PackageManager
     * @param packageName String packageName the name of the application to look for
     * @return Date object of the installation time according to the package info or null
     */
    private static Date installTime(PackageManager packageManager, String packageName) {
        Log.d(TAG, "Checking for app install time");

        try {
            PackageInfo info        = packageManager.getPackageInfo(packageName, 0);
            Field       installTime = PackageInfo.class.getField("firstInstallTime");

            return new Date(installTime.getLong(info));
        } catch(Exception e) {
            Log.w(TAG, "Could not determine update time", e);
        }

        return null;
    }

    /**
     *
     * @param packageManager PackageManager
     * @param packageName String packageName the name of the application to look for
     * @return Date of installation or update from installTime and updateTime. Returns null when both fields are unavailable, a mock date should be used in it's place.
     */
    public static Date getInstallTime(PackageManager packageManager, String packageName) {
        Date install = installTime(packageManager, packageName);
        Date update  = updateTime(packageManager, packageName);

        if(install != null) {
            return install;
        } else if(update != null) {
            return update;
        } else {
            Log.wtf(TAG, "Every field of dates is empty. What's going on here?");
            return null;
        }
    }

}
