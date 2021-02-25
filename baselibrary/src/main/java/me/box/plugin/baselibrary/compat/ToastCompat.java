/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.compat;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.box.plugin.baselibrary.BaseApplication;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * 处理toast显示逻辑，同时只显示一个Toast，当有Toast显示时，取消上一个，显示当前的
 */
public class ToastCompat {
    @IntDef(value = {
            Toast.LENGTH_SHORT,
            Toast.LENGTH_LONG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private ToastCompat() {
    }

    private static Toast sToast;

    public static void show(CharSequence text, @Duration int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
        sToast = Toast.makeText(BaseApplication.getInstance(), text, duration);
        sToast.show();
    }

    public static void show(@StringRes int resId, @Duration int duration) {
        if (resId == 0) {
            return;
        }
        show(BaseApplication.getInstance().getText(resId), duration);
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void show(@StringRes int resId) {
        show(resId, Toast.LENGTH_LONG);
    }
}
