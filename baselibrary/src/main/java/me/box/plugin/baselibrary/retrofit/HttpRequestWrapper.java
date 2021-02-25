/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.box.plugin.retrofit.Callback;
import me.box.plugin.retrofit.HttpRequest;
import me.box.plugin.retrofit.impl.RetrofitContext;
import me.box.plugin.retrofit.SubscriberWrapper;
import okhttp3.Interceptor;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by box on 2017/8/8.
 * <p>
 * 网络请求配置
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class HttpRequestWrapper<E> {
    private final HttpRequest mRequest;
    private final E mService;

    protected HttpRequestWrapper(String baseUrl, long timeout, Class<E> cls, Interceptor... interceptors) {
        HttpRequest.initialize("RetCode", "RetMessage", "RetObject", 0);
        mRequest = new HttpRequest(baseUrl, timeout, HttpRequestWrapper::log, interceptors);
        mService = mRequest.create(cls);
    }

    protected <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer) {
        return request(iContext, observable, observer, true);
    }

    protected <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer, boolean isShowPrompt) {
        return request(iContext, observable, Callback.inclusion(observer), isShowPrompt);
    }

    protected <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer) {
        return request(iContext, observable, observer, true);
    }

    protected <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer, boolean isShowPrompt) {
        return mRequest.request(iContext, observable, new SubscriberWrapper<>(observer, isShowPrompt));
    }

    private static void log(String s) {
        Log.i("HttpRequest", s);
    }

    protected HttpRequest getRequest() {
        return mRequest;
    }

    protected E getService() {
        return mService;
    }
}