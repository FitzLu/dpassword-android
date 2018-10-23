package com.dpass.android.utils;

import android.os.Build;

public class OsUtil {

    public static boolean atLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
