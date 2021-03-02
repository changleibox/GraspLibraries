/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import me.box.plugin.retrofit.SubscriberWrapper;
import me.box.plugin.retrofit.impl.RetrofitContext;
import me.box.plugin.retrofit.rx.Observables;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RetrofitFuture<T> implements Action1<Observer<T>> {
    @Nullable
    private final RetrofitContext mContext;
    @NonNull
    private final Observable<T> mObservable;
    @NonNull
    private final RxFuture<T> mTask;
    private final boolean isShowPrompt;

    public RetrofitFuture(@Nullable RetrofitContext context, @NotNull Observable<T> observable) {
        this(context, observable, true);
    }

    public RetrofitFuture(@Nullable RetrofitContext context, @NotNull Observable<T> observable, boolean isShowPrompt) {
        this.mContext = context;
        this.mObservable = observable;
        this.mTask = new RxFuture<>(this);
        this.isShowPrompt = isShowPrompt;
    }

    public T execute() throws Throwable {
        return mTask.execute();
    }

    public T execute(long timeout, TimeUnit unit) throws Throwable {
        return mTask.execute(timeout, unit);
    }

    @Override
    public void call(Observer<T> tObserver) {
        Observables.subscribe(mContext, mObservable, new SubscriberWrapper<>(tObserver, isShowPrompt), Schedulers.io());
    }
}