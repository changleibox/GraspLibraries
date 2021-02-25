/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.android.ActivityEvent;

import javax.annotation.Nonnull;

import me.box.plugin.baselibrary.delegate.ContextDelegate;
import me.box.plugin.baselibrary.impl.ActivityLifecycle;
import me.box.plugin.baselibrary.impl.ContextWrapper;
import me.box.plugin.retrofit.impl.RetrofitProgressImpl;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by changlei on 2/24/21.
 * <p>
 * activity基类
 */
abstract public class BaseFragmentActivity extends FragmentActivity implements ContextWrapper, LifecycleProvider<ActivityEvent>, ActivityLifecycle {
    private final ContextDelegate.ActivityDelegate mDelegate = ContextDelegate.create(this);

    @NonNull
    @Override
    public Observable<ActivityEvent> lifecycle() {
        return mDelegate.lifecycle();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ActivityEvent event) {
        return mDelegate.bindUntilEvent(event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return mDelegate.bindToLifecycle();
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return mDelegate.getCompositeSubscription();
    }

    @Override
    public RetrofitProgressImpl showRetrofitLoad() {
        return mDelegate.showRetrofitLoad();
    }

    @Override
    public void loadDismiss() {
        mDelegate.loadDismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @Override
    public void onPause() {
        mDelegate.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mDelegate.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
    }
}
