package me.box.plugin.baselibrary;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * Application基类
 */
abstract public class BaseApplication extends Application {
    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
