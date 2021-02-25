package me.box.plugin.baselibrary.util;

import android.app.Application;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import me.box.plugin.baselibrary.BaseApplication;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by changlei on 2/23/21.
 * <p>
 * 工具类
 */
public class Utils {
    private Utils() {
    }

    public static String getImei() {
        final Application context = BaseApplication.getInstance();
        String imei;
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                imei = tm.getImei();
            } else {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            imei = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }
}
