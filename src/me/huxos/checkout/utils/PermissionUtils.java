package me.huxos.checkout.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 权限检测工具
 * Created by cabe on 16/7/10.
 */
public class PermissionUtils {
    public final static int Z_REQUEST_CODE_BASE_START = 0xA00;
    public final static int REQUEST_CODE_APP_ALL = Z_REQUEST_CODE_BASE_START + 1;

    private static boolean checkPermissions(Activity activity, String...permissions) {
        boolean hasPermission = true;
        if(activity != null && permissions != null) {
            for(String permission : permissions) {
                hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if(!hasPermission) {
                    break;
                }
            }
        }
        return hasPermission;
    }

    private static boolean requestPermissions(Activity activity, boolean requestFlag, int requestCode, String...permissions) {
        boolean hasPermission = true;

        if (!checkPermissions(activity, permissions)) {
            hasPermission = false;
            if(requestFlag) {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
        }
        return hasPermission;
    }

    public static boolean checkAppPermissions(Activity activity, boolean requestFlag) {
        int requestCode = REQUEST_CODE_APP_ALL;
        String[] permission = {
                Manifest.permission.READ_PHONE_STATE
        };
        return requestPermissions(activity, requestFlag, requestCode, permission);
    }
}
