package me.box.plugin.retrofit.rx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.box.plugin.retrofit.HttpUtils;
import me.box.plugin.retrofit.LoadSubscriber;
import me.box.plugin.retrofit.impl.Callable;
import me.box.plugin.retrofit.impl.LifecycleImpl;
import me.box.plugin.retrofit.impl.RetrofitContext;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by changlei on 3/2/21.
 * <p>
 * observable扩展类
 */
public class Observables {
    public static final Scheduler MAIN_THREAD = AndroidSchedulers.mainThread();

    private Observables() {
    }

    public static <T> Observable<T> create(@NonNull Callable<T> callable) {
        return Observable.unsafeCreate(subscriber -> {
            try {
                subscriber.onStart();
                subscriber.onNext(callable.call());
                subscriber.onCompleted();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                subscriber.onError(throwable);
            }
        });
    }

    public static <T> Subscription subscribe(@NonNull Callable<T> callable, @Nullable Observer<T> observer) {
        return subscribe(null, callable, observer);
    }

    public static <T> Subscription subscribe(@NonNull Callable<T> callable, @Nullable Observer<T> observer, @NonNull Scheduler observeOn) {
        return subscribe(null, callable, observer, observeOn);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Callable<T> callable, @Nullable Observer<T> observer) {
        return subscribe(context, create(callable), observer);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Callable<T> callable, @Nullable Observer<T> observer, @NonNull Scheduler observeOn) {
        return subscribe(context, create(callable), observer, observeOn);
    }

    public static <T> Subscription subscribe(@NonNull Observable<T> observable, @Nullable Observer<T> observer) {
        return subscribe(null, observable, observer);
    }

    public static <T> Subscription subscribe(@NonNull Observable<T> observable, @Nullable Observer<T> observer, @NonNull Scheduler observeOn) {
        return subscribe(null, observable, observer, observeOn);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, @Nullable Observer<T> observer) {
        return subscribe(context, observable, observer, MAIN_THREAD);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, @Nullable Observer<T> observer, @NonNull Scheduler observeOn) {
        Subscriber<T> subscriber;
        if (observer instanceof LoadSubscriber) {
            subscriber = (LoadSubscriber<T>) observer;
        } else if (context != null) {
            subscriber = new LoadSubscriber<>(context, observer);
        } else if (observer instanceof Subscriber) {
            subscriber = (Subscriber<T>) observer;
        } else {
            subscriber = HttpUtils.convertToSubscriber(observer);
        }
        return subscribe(context, observable, subscriber, observeOn);
    }

    public static <T> Subscription subscribe(@NonNull Observable<T> observable, @Nullable Action1<T> onNext) {
        return subscribe(null, observable, onNext);
    }

    public static <T> Subscription subscribe(@NonNull Observable<T> observable, @Nullable Action1<T> onNext, @NonNull Scheduler observeOn) {
        return subscribe(null, observable, onNext, observeOn);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, @Nullable Action1<T> onNext) {
        return subscribe(context, observable, onNext, MAIN_THREAD);
    }

    public static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, @Nullable Action1<T> onNext, @NonNull Scheduler observeOn) {
        Subscriber<T> subscriber;
        if (context != null) {
            subscriber = new LoadSubscriber<>(context, onNext);
        } else {
            subscriber = HttpUtils.convertToSubscriber(onNext);
        }
        return subscribe(context, observable, subscriber, observeOn);
    }

    private static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, Subscriber<T> subscriber) {
        return subscribe(context, observable, subscriber, MAIN_THREAD);
    }

    private static <T> Subscription subscribe(@Nullable RetrofitContext context, @NonNull Observable<T> observable, Subscriber<T> subscriber, @NonNull Scheduler observeOn) {
        if (context instanceof LifecycleImpl) {
            observable = observable.compose(((LifecycleImpl) context).bindToLifecycle());
        }
        CompositeSubscription subscription;
        if (context instanceof LifecycleImpl && (subscription = ((LifecycleImpl) context).getCompositeSubscription()) != null) {
            subscription.add(subscriber);
        }
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(observeOn)
                .subscribe(subscriber);
    }
}
