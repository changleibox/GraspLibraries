/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.box.plugin.retrofit.impl.RetrofitProgressImpl;
import me.box.plugin.retrofit.impl.ShowLoadImpl;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Box on 17/3/15.
 * <p>
 * load的显示
 */
public class LoadSubscriber<T> extends Subscriber<T> implements DialogInterface.OnCancelListener {

    private final ShowLoadImpl mIContext;
    @Nullable
    private Observer<T> mObserver;
    @Nullable
    private Action1<T> mOnNext;

    private RetrofitProgressImpl mProgress;

    public LoadSubscriber(@NonNull ShowLoadImpl iContext, @Nullable Action1<T> onNext) {
        this.mIContext = iContext;
        this.mOnNext = onNext;
    }

    public LoadSubscriber(@NonNull ShowLoadImpl iContext, @Nullable Observer<T> observer) {
        this.mIContext = iContext;
        this.mObserver = observer;

        if (observer instanceof Subscriber) {
            ((Subscriber<T>) observer).add(new Subscription() {
                @Override
                public void unsubscribe() {
                    LoadSubscriber.this.unsubscribe();
                    mIContext.loadDismiss();
                }

                @Override
                public boolean isUnsubscribed() {
                    return LoadSubscriber.this.isUnsubscribed();
                }
            });
        }
    }

    @Override
    public void onStart() {
        mProgress = mIContext.showRetrofitLoad();
        if (mProgress != null) {
            mProgress.show();
            mProgress.setOnCancelListener(this);
        }
        if (mObserver instanceof Subscriber) {
            ((Subscriber<?>) mObserver).onStart();
        }
    }

    @Override
    public void onCompleted() {
        if (mObserver != null) {
            mObserver.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        requestCompleted();
        if (mObserver != null) {
            mObserver.onError(e);
        }
    }

    @Override
    public void onNext(T o) {
        requestCompleted();
        if (mObserver != null) {
            mObserver.onNext(o);
        }
        if (mOnNext != null) {
            mOnNext.call(o);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (!isUnsubscribed()) {
            requestCompleted();
            unsubscribe();
            onCompleted();
        }
    }

    private void requestCompleted() {
        mIContext.loadDismiss();
        if (mProgress != null) {
            mProgress.setOnCancelListener(null);
            mProgress = null;
        }
    }
}
