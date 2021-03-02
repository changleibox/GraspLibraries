/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.box.plugin.retrofit.Callback;
import me.box.plugin.retrofit.HttpRequest;
import me.box.plugin.retrofit.SubscriberWrapper;
import me.box.plugin.retrofit.impl.RetrofitContext;
import okhttp3.Interceptor;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by box on 2017/8/8.
 * <p>
 * 网络请求配置
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class HttpRequestWrapper<E> {
    private final HttpRequest mRequest;
    private final E mService;

    public HttpRequestWrapper(String baseUrl, long timeout, Class<E> cls, Interceptor... interceptors) {
        HttpRequest.initialize("RetCode", "RetMessage", "RetObject", 0);
        mRequest = new HttpRequest(baseUrl, timeout, HttpRequestWrapper::log, interceptors);
        mService = mRequest.create(cls);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer) {
        return request(iContext, observable, observer, true);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer, @NonNull Scheduler observeOn) {
        return request(iContext, observable, observer, true, observeOn);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer, boolean isShowPrompt) {
        return request(iContext, observable, Callback.inclusion(observer), isShowPrompt);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> observer, boolean isShowPrompt, @NonNull Scheduler observeOn) {
        return request(iContext, observable, Callback.inclusion(observer), isShowPrompt, observeOn);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer) {
        return request(iContext, observable, observer, true);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer, @NonNull Scheduler observeOn) {
        return request(iContext, observable, observer, true, observeOn);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer, boolean isShowPrompt) {
        return mRequest.request(iContext, observable, new SubscriberWrapper<>(observer, isShowPrompt));
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer, boolean isShowPrompt, @NonNull Scheduler observeOn) {
        return mRequest.request(iContext, observable, new SubscriberWrapper<>(observer, isShowPrompt), observeOn);
    }

    private static void log(String s) {
        Log.i("HttpRequest", s);
    }

    public HttpRequest getRequest() {
        return mRequest;
    }

    public E getService() {
        return mService;
    }
}