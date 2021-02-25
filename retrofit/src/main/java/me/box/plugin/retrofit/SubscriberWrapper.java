/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import androidx.annotation.Nullable;

import me.box.plugin.retrofit.impl.OnObserverErrorListener;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by box on 2017/5/2.
 * <p>
 * 基本的网络请求监听者
 */
public class SubscriberWrapper<T> extends Subscriber<T> {
    @Nullable
    private final Observer<T> mObserver;
    private final boolean isShowPrompt;

    @Nullable
    private static OnObserverErrorListener sObserverErrorListener;

    public SubscriberWrapper(@Nullable Observer<T> observer) {
        this(observer, true);
    }

    public SubscriberWrapper(@Nullable Observer<T> observer, boolean isShowPrompt) {
        this.mObserver = observer;
        this.isShowPrompt = isShowPrompt;
        if (observer instanceof Subscriber) {
            ((Subscriber<T>) observer).add(new Subscription() {
                @Override
                public void unsubscribe() {
                    SubscriberWrapper.this.unsubscribe();
                }

                @Override
                public boolean isUnsubscribed() {
                    return SubscriberWrapper.this.isUnsubscribed();
                }
            });
        }
    }

    @Override
    public void onStart() {
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
        e.printStackTrace();
        if (isShowPrompt) {
            showErrorMsg(e);
        }
        if (mObserver != null) {
            mObserver.onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        if (mObserver != null) {
            mObserver.onNext(t);
        }
    }

    public static void setObserverErrorListener(OnObserverErrorListener listener) {
        SubscriberWrapper.sObserverErrorListener = listener;
    }

    private static void showErrorMsg(Throwable e) {
        if (sObserverErrorListener != null) {
            sObserverErrorListener.onError(e);
        }
    }
}
