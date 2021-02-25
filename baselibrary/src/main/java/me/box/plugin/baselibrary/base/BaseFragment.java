/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import me.box.plugin.baselibrary.delegate.ContextDelegate;
import me.box.plugin.baselibrary.impl.ContextWrapper;
import me.box.plugin.baselibrary.impl.FragmentLifecycle;
import me.box.plugin.retrofit.impl.RetrofitProgressImpl;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * Fragment基类
 */
abstract public class BaseFragment extends Fragment implements ContextWrapper, LifecycleProvider<FragmentEvent>, FragmentLifecycle {
    private final ContextDelegate.FragmentDelegate mDelegate = ContextDelegate.create(this);

    @Nonnull
    @Override
    public Observable<FragmentEvent> lifecycle() {
        return mDelegate.lifecycle();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull FragmentEvent event) {
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

    @CallSuper
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mDelegate.onAttach(context);
    }

    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
    }

    @CallSuper
    public void onViewCreated(@NotNull @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDelegate.onViewCreated(view, savedInstanceState);
    }

    @CallSuper
    public void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @CallSuper
    public void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @CallSuper
    public void onPause() {
        mDelegate.onPause();
        super.onPause();
    }

    @CallSuper
    public void onStop() {
        mDelegate.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mDelegate.onDestroyView();
        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mDelegate.onDetach();
        super.onDetach();
    }
}
