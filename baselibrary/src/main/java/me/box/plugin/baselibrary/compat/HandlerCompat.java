package me.box.plugin.baselibrary.compat;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 处理handler兼容问题
 */
public class HandlerCompat {
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    private HandlerCompat() {
    }

    public static boolean runOnUiThread(Runnable runnable) {
        return MAIN_THREAD_HANDLER.post(runnable);
    }
}
