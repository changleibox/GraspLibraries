/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.delegate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.annotation.Nonnull;

import me.box.plugin.baselibrary.dialog.LoadPrompt;
import me.box.plugin.baselibrary.impl.ActivityLifecycle;
import me.box.plugin.baselibrary.impl.ContextWrapper;
import me.box.plugin.baselibrary.impl.FragmentLifecycle;
import me.box.plugin.retrofit.impl.RetrofitProgressImpl;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * 实现自定义Context逻辑
 */
abstract public class ContextDelegate<E> implements ContextWrapper, LifecycleProvider<E> {
    protected final BehaviorSubject<E> lifecycleSubject = BehaviorSubject.create();

    private CompositeSubscription mCompositeSubscription;
    private LoadPrompt mLoadPrompt;

    private ContextDelegate() {
    }

    public static ActivityDelegate create(Activity activity) {
        return new ActivityDelegate(activity);
    }

    public static FragmentDelegate create(Fragment fragment) {
        return new FragmentDelegate(fragment);
    }

    public static PreferenceFragmentDelegate create(PreferenceFragment fragment) {
        return new PreferenceFragmentDelegate(fragment);
    }

    public static ViewDelegate create(View view) {
        return new ViewDelegate(view);
    }

    @Nonnull
    @Override
    public Observable<E> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull E event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }

    @CallSuper
    public void onDestroy() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public RetrofitProgressImpl showRetrofitLoad() {
        if (mLoadPrompt == null) {
            mLoadPrompt = new LoadPrompt(getContext());
        }
        return mLoadPrompt;
    }

    @Override
    public void loadDismiss() {
        if (mLoadPrompt != null) {
            mLoadPrompt.dismiss();
        }
    }

    @NonNull
    abstract protected Context getContext();

    static public class ActivityDelegate extends ContextDelegate<ActivityEvent> implements ActivityLifecycle {
        private final Context mContext;

        private ActivityDelegate(Context context) {
            this.mContext = context;
        }

        @NonNull
        @Override
        public <T> LifecycleTransformer<T> bindToLifecycle() {
            return RxLifecycleAndroid.bindActivity(lifecycleSubject);
        }

        @CallSuper
        public void onCreate(@Nullable Bundle savedInstanceState) {
            lifecycleSubject.onNext(ActivityEvent.CREATE);
        }

        @CallSuper
        public void onStart() {
            lifecycleSubject.onNext(ActivityEvent.START);
        }

        @CallSuper
        public void onResume() {
            lifecycleSubject.onNext(ActivityEvent.RESUME);
        }

        @CallSuper
        public void onPause() {
            lifecycleSubject.onNext(ActivityEvent.PAUSE);
        }

        @CallSuper
        public void onStop() {
            lifecycleSubject.onNext(ActivityEvent.STOP);
        }

        @CallSuper
        public void onDestroy() {
            lifecycleSubject.onNext(ActivityEvent.DESTROY);
            super.onDestroy();
        }

        @NonNull
        @Override
        protected Context getContext() {
            return mContext;
        }
    }

    static public class FragmentDelegate extends ContextDelegate<FragmentEvent> implements FragmentLifecycle {
        private final Fragment mFragment;

        private FragmentDelegate(Fragment fragment) {
            this.mFragment = fragment;
        }

        @NonNull
        @Override
        public <T> LifecycleTransformer<T> bindToLifecycle() {
            return RxLifecycleAndroid.bindFragment(lifecycleSubject);
        }

        @CallSuper
        public void onAttach(@NonNull Context context) {
            lifecycleSubject.onNext(FragmentEvent.ATTACH);
        }

        @CallSuper
        public void onCreate(@Nullable Bundle savedInstanceState) {
            lifecycleSubject.onNext(FragmentEvent.CREATE);
        }

        @CallSuper
        public void onViewCreated(@NotNull @NonNull View view, @Nullable Bundle savedInstanceState) {
            lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        }

        @CallSuper
        public void onStart() {
            lifecycleSubject.onNext(FragmentEvent.START);
        }

        @CallSuper
        public void onResume() {
            lifecycleSubject.onNext(FragmentEvent.RESUME);
        }

        @CallSuper
        public void onPause() {
            lifecycleSubject.onNext(FragmentEvent.PAUSE);
        }

        @CallSuper
        public void onStop() {
            lifecycleSubject.onNext(FragmentEvent.STOP);
        }

        @Override
        public void onDestroyView() {
            lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        }

        @CallSuper
        @Override
        public void onDestroy() {
            lifecycleSubject.onNext(FragmentEvent.DESTROY);
            super.onDestroy();
        }

        @Override
        public void onDetach() {
            lifecycleSubject.onNext(FragmentEvent.DETACH);
        }

        @NonNull
        @Override
        protected Context getContext() {
            return Objects.requireNonNull(mFragment.getContext());
        }
    }

    static public class PreferenceFragmentDelegate extends ContextDelegate<FragmentEvent> implements FragmentLifecycle {
        private final PreferenceFragment mFragment;

        private PreferenceFragmentDelegate(PreferenceFragment fragment) {
            this.mFragment = fragment;
        }

        @NonNull
        @Override
        public <T> LifecycleTransformer<T> bindToLifecycle() {
            return RxLifecycleAndroid.bindFragment(lifecycleSubject);
        }

        @CallSuper
        public void onAttach(@NonNull Context context) {
            lifecycleSubject.onNext(FragmentEvent.ATTACH);
        }

        @CallSuper
        public void onCreate(@Nullable Bundle savedInstanceState) {
            lifecycleSubject.onNext(FragmentEvent.CREATE);
        }

        @CallSuper
        public void onViewCreated(@NotNull @NonNull View view, @Nullable Bundle savedInstanceState) {
            lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        }

        @CallSuper
        public void onStart() {
            lifecycleSubject.onNext(FragmentEvent.START);
        }

        @CallSuper
        public void onResume() {
            lifecycleSubject.onNext(FragmentEvent.RESUME);
        }

        @CallSuper
        public void onPause() {
            lifecycleSubject.onNext(FragmentEvent.PAUSE);
        }

        @CallSuper
        public void onStop() {
            lifecycleSubject.onNext(FragmentEvent.STOP);
        }

        @Override
        public void onDestroyView() {
            lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        }

        @CallSuper
        @Override
        public void onDestroy() {
            lifecycleSubject.onNext(FragmentEvent.DESTROY);
            super.onDestroy();
        }

        @Override
        public void onDetach() {
            lifecycleSubject.onNext(FragmentEvent.DETACH);
        }

        @NonNull
        @Override
        protected Context getContext() {
            return Objects.requireNonNull(mFragment.getActivity());
        }
    }

    static public class ViewDelegate extends ContextDelegate<View> {
        private final View mView;

        private ViewDelegate(View view) {
            this.mView = view;
        }

        @NonNull
        @Override
        public <T> LifecycleTransformer<T> bindToLifecycle() {
            return RxLifecycleAndroid.bindView(mView);
        }

        @NonNull
        @Override
        protected Context getContext() {
            return mView.getContext();
        }
    }
}
