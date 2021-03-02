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

/**
 * Created by Box on 17/3/16.
 * <p>
 * 网络请求
 */
public class RetrofitFactory {
    private final Retrofit mRetrofit;

    public RetrofitFactory(@NonNull Options options) {
        this(options, (Logger) null);
    }

    public RetrofitFactory(@NonNull Options options, @Nullable Interceptor... interceptors) {
        this(options, null, interceptors);
    }

    public RetrofitFactory(@NonNull Options options, @Nullable Logger logger) {
        this(options, logger, (Interceptor[]) null);
    }

    public RetrofitFactory(@NonNull Options options, @Nullable Logger logger, @Nullable Interceptor... interceptors) {
        this(options, createHttpClient(options.timeout, logger, interceptors));
    }

    public RetrofitFactory(@NonNull Options options, @NonNull OkHttpClient client) {
        this(createRetrofit(client, options));
    }

    private RetrofitFactory(@NonNull Retrofit retrofit) {
        this.mRetrofit = retrofit;
    }

    public <T> T create(@NonNull final Class<T> cls) {
        return mRetrofit.create(cls);
    }

    private static Retrofit createRetrofit(@NonNull OkHttpClient client, @NonNull Options options) {
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(options.codeName, options.messageName, options.dataName, options.validCode))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(options.baseUrl)
                .build();
    }

    private static OkHttpClient createHttpClient(@IntRange(from = 1, to = Integer.MAX_VALUE) long timeout, @Nullable Logger logger, @Nullable Interceptor... interceptors) {
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

    static public class Options {
        private final String codeName;
        private final String messageName;
        private final String dataName;
        private final Integer validCode;
        private final String baseUrl;
        private final long timeout;

        public Options(@NonNull String codeName, @NonNull String messageName, @NonNull String dataName, @NonNull Integer validCode, @NonNull String baseUrl, @IntRange(from = 1, to = Integer.MAX_VALUE) long timeout) {
            this.codeName = codeName;
            this.messageName = messageName;
            this.dataName = dataName;
            this.validCode = validCode;
            this.baseUrl = baseUrl;
            this.timeout = timeout;
        }
    }
}
