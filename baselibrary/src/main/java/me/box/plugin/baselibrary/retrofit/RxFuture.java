/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.functions.Action1;

public class RxFuture<T> implements Callable<T>, Observer<T> {
    @NonNull
    private final FutureTask<T> mTask;
    @NonNull
    private final CountDownLatch mLatch;
    @NonNull
    private final Action1<Observer<T>> mCallable;

    @Nullable
    private T object;
    @Nullable
    private Throwable exception;

    public RxFuture(@NonNull Action1<Observer<T>> callable) {
        this.mCallable = callable;
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
        mCallable.call(this);
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