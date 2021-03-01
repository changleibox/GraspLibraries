/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.delegate;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.box.plugin.retrofit.RequestProgressInterceptor;
import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * oss文件上传器
 */
@SuppressWarnings("UnusedReturnValue")
public class Uploader {
    private static final int DEFAULT_TIMEOUT = 15;

    private final Retrofit mRetrofit;

    public Uploader(@Nullable ProgressListener listener) {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(s -> Log.i("uploader", s));
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RequestProgressInterceptor(listener))
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://box.me")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public Observable<Void> asObservable(@NonNull String host, @NonNull FormData data) {
        return mRetrofit.create(UploadService.class)
                .upload(host, data.toBody())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Subscription upload(@NonNull String host, @NonNull FormData data, @Nullable Observer<Void> observer) {
        final Observable<Void> observable = asObservable(host, data);
        return observer == null ? observable.subscribe() : observable.subscribe(observer);
    }

    public interface UploadService {
        @POST
        Observable<Void> upload(@Url String host, @Body MultipartBody body);
    }

    public interface FormData {
        @NonNull
        default String boundary() {
            return UUID.randomUUID().toString();
        }

        @NonNull
        default MultipartBody toBody() {
            final Field[] fields = getClass().getDeclaredFields();
            Arrays.sort(fields, (Field o1, Field o2) -> {
                final Class<?> type1 = o1.getType();
                final Class<?> type2 = o2.getType();
                if (type1 == File.class && type2 != File.class) {
                    return 1;
                } else if (type1 != File.class && type2 == File.class) {
                    return -1;
                }
                return 0;
            });
            final MultipartBody.Builder builder = new MultipartBody.Builder(boundary());
            for (Field field : fields) {
                field.setAccessible(true);
                addFormDataPart(this, field, builder);
            }
            return builder.setType(MultipartBody.FORM).build();
        }

        static void addFormDataPart(Object obj, Field field, MultipartBody.Builder builder) {
            try {
                final Object value = field.get(obj);
                if (Modifier.isTransient(field.getModifiers()) || value == null) {
                    return;
                }
                final SerializedName annotation = field.getAnnotation(SerializedName.class);
                final String fieldName = annotation == null ? field.getName() : annotation.value();
                if (value instanceof File) {
                    final File file = (File) value;
                    final MediaType mediaType = MediaType.parse("multipart/form-data");
                    final RequestBody body = RequestBody.create(file, mediaType);
                    builder.addFormDataPart(fieldName, file.getName(), body);
                } else {
                    builder.addFormDataPart(fieldName, value.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
