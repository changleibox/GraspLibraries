package me.box.plugin.retrofit;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 处理handler兼容问题
 */
@SuppressWarnings("UnusedReturnValue")
class HandlerCompat {
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    private HandlerCompat() {
    }

    static boolean runOnUiThread(Runnable runnable) {
        return MAIN_THREAD_HANDLER.post(runnable);
    }
}
