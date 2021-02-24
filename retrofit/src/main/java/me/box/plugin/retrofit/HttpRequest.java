/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Box on 17/3/16.
 * <p>
 * 网络请求
 */
@SuppressWarnings({"WeakerAccess"})
public class HttpRequest {
    private static String sCodeName;
    private static String sMessageName;
    private static String sDataName;
    private static Integer sValidCode;
    private static boolean isInitialized = false;

    public static void initialize(String codeName, String messageName, String dataName, int validCode) {
        if (isInitialized) {
            throw new IllegalStateException("只能初始化一次");
        }
        isInitialized = true;
        sCodeName = codeName;
        sMessageName = messageName;
        sDataName = dataName;
        sValidCode = validCode;
    }

    private final Retrofit mRetrofit;

    public HttpRequest(@NonNull Retrofit retrofit) {
        this.mRetrofit = retrofit;
    }

    public HttpRequest(@NonNull String baseUrl, @IntRange(from = 1, to = Integer.MAX_VALUE) long timeout) {
        this(baseUrl, timeout, (Logger) null);
    }

    public HttpRequest(@NonNull String baseUrl, @IntRange(from = 1, to = Integer.MAX_VALUE) long timeout, @Nullable Interceptor... interceptors) {
        this(baseUrl, timeout, null, interceptors);
    }

    public HttpRequest(@NonNull String baseUrl, @IntRange(from = 1, to = Integer.MAX_VALUE) long timeout, @Nullable Logger logger) {
        this(baseUrl, timeout, logger, (Interceptor[]) null);
    }

    public HttpRequest(@NonNull String baseUrl, @IntRange(from = 1, to = Integer.MAX_VALUE) long timeout, @Nullable Logger logger, @Nullable Interceptor... interceptors) {
        this(buildOkHttpClient(timeout, logger, interceptors), baseUrl);
    }

    public HttpRequest(@NonNull OkHttpClient client, @NonNull String baseUrl) {
        this(buildRetrofit(client, baseUrl));
    }

    public <T> T create(@NonNull final Class<T> cls) {
        return mRetrofit.create(cls);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Observer<T> observer) {
        Subscriber<T> subscriber;
        if (observer instanceof LoadSubscriber) {
            subscriber = (LoadSubscriber<T>) observer;
        } else if (iContext != null) {
            subscriber = new LoadSubscriber<>(iContext, observer);
        } else if (observer instanceof Subscriber) {
            subscriber = (Subscriber<T>) observer;
        } else {
            subscriber = HttpUtils.convertToSubscriber(observer);
        }
        return subscribe(iContext, observable, subscriber);
    }

    public <T> Subscriber<T> request(@NonNull Observable<T> observable, @Nullable Observer<T> subscriber) {
        return request(null, observable, subscriber);
    }

    public <T> Subscriber<T> request(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, @Nullable Action1<T> onNext) {
        Subscriber<T> subscriber;
        if (iContext != null) {
            subscriber = new LoadSubscriber<>(iContext, onNext);
        } else {
            subscriber = HttpUtils.convertToSubscriber(onNext);
        }
        return subscribe(iContext, observable, subscriber);
    }

    public <T> Subscriber<T> request(@NonNull Observable<T> observable, @Nullable Action1<T> onNext) {
        return request(null, observable, onNext);
    }

    private <T> Subscriber<T> subscribe(@Nullable RetrofitContext iContext, @NonNull Observable<T> observable, Subscriber<T> subscriber) {
        if (iContext instanceof LifecycleImpl) {
            observable = observable.compose(((LifecycleImpl) iContext).bindToLifecycle());
        }
        CompositeSubscription subscription;
        if (iContext instanceof LifecycleImpl && (subscription = ((LifecycleImpl) iContext).getCompositeSubscription()) != null) {
            subscription.add(subscriber);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    private static Retrofit buildRetrofit(@NonNull OkHttpClient client, @NonNull String baseUrl) {
        if (sCodeName == null || sMessageName == null || sDataName == null || sValidCode == null) {
            throw new IllegalStateException("请先初始化");
        }
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(sCodeName, sMessageName, sDataName, sValidCode))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    private static OkHttpClient buildOkHttpClient(@IntRange(from = 1, to = Integer.MAX_VALUE) long timeout, @Nullable Logger logger, @Nullable Interceptor... interceptors) {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        httpClientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        httpClientBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS);

        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                httpClientBuilder.addInterceptor(interceptor);
            }
        }

        if (logger != null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        return httpClientBuilder.build();
    }
}
