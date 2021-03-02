/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import me.box.plugin.retrofit.impl.RetrofitContext;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RetrofitFuture<T> implements Action1<Observer<T>> {
    @NonNull
    private final HttpRequestWrapper<?> mWrapper;
    @Nullable
    private final RetrofitContext mContext;
    @NonNull
    private final Observable<T> mObservable;
    @NonNull
    private final RxFuture<T> mTask;

    public RetrofitFuture(@Nullable RetrofitContext context, @NonNull HttpRequestWrapper<?> wrapper, @NotNull Observable<T> observable) {
        this.mContext = context;
        this.mWrapper = wrapper;
        this.mObservable = observable;
        this.mTask = new RxFuture<>(this);
    }

    public T execute() throws Throwable {
        return mTask.execute();
    }

    public T execute(long timeout, TimeUnit unit) throws Throwable {
        return mTask.execute(timeout, unit);
    }

    @Override
    public void call(Observer<T> tObserver) {
        mWrapper.request(mContext, mObservable, tObserver, Schedulers.io());
    }
}