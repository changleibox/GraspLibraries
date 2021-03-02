/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import android.util.Log;

import me.box.plugin.retrofit.RetrofitFactory;
import okhttp3.Interceptor;

/**
 * Created by box on 2017/8/8.
 * <p>
 * 网络请求配置
 */
public class GraspRetrofit<E> {
    private static final String RET_CODE = "RetCode";
    private static final String RET_MESSAGE = "RetMessage";
    private static final String RET_OBJECT = "RetObject";
    private static final int VALID_CODE = 0;

    private final RetrofitFactory mFactory;
    private final E mService;

    public GraspRetrofit(String baseUrl, long timeout, Class<E> cls, Interceptor... interceptors) {
        final RetrofitFactory.Options options = new RetrofitFactory.Options(RET_CODE, RET_MESSAGE, RET_OBJECT, VALID_CODE, baseUrl, timeout);
        mFactory = new RetrofitFactory(options, GraspRetrofit::log, interceptors);
        mService = mFactory.create(cls);
    }

    private static void log(String s) {
        Log.i("GraspRetrofit", s);
    }

    public RetrofitFactory getFactory() {
        return mFactory;
    }

    public E getService() {
        return mService;
    }
}