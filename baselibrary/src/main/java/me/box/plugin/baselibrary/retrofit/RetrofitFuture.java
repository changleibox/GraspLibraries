/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import me.box.plugin.retrofit.impl.RetrofitContext;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

public class RetrofitFuture<T> implements Callable<T>, Observer<T> {
    @NonNull
    private final HttpRequestWrapper<?> mWrapper;
    @Nullable
    private final RetrofitContext context;
    @NonNull
    private final Observable<T> observable;
    @NonNull
    private final FutureTask<T> mTask;
    @NonNull
    private final CountDownLatch mLatch;

    @Nullable
    private T object;
    @Nullable
    private Throwable exception;

    public RetrofitFuture(@Nullable RetrofitContext context, @NonNull HttpRequestWrapper<?> wrapper, @NotNull Observable<T> observable) {
        this.context = context;
        this.mWrapper = wrapper;
        this.observable = observable;
        this.mTask = new FutureTask<>(this);
        this.mLatch = new CountDownLatch(1);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        exception = e;
        mLatch.countDown();
    }

    @Override
    public void onNext(T t) {
        object = t;
        mLatch.countDown();
    }

    @Override
    public T call() throws Exception {
        mWrapper.request(context, observable, this, Schedulers.io());
        mLatch.await();
        return object;
    }

    public T execute() throws Throwable {
        mTask.run();
        final T result = mTask.get();
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    public T execute(long timeout, TimeUnit unit) throws Throwable {
        mTask.run();
        final T result = mTask.get(timeout, unit);
        if (exception != null) {
            throw exception;
        }
        return result;
    }
}