/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Box on 17/3/16.
 * <p>
 * 网络请求工具类
 */
@SuppressWarnings({"WeakerAccess"})
public class HttpUtils {

    private static final MediaType MEDIA_TYPE_NORMAL = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("multipart/form-data; charset=utf-8");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

    @NonNull
    public static <T> Subscriber<T> convertToSubscriber(@Nullable final Action1<T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(T t) {
                if (onNext != null) {
                    onNext.call(t);
                }
            }
        };
    }

    @NonNull
    public static <T> Subscriber<T> convertToSubscriber(@Nullable final Observer<T> observer) {
        Subscriber<T> subscriber = new Subscriber<T>() {
            @Override
            public void onCompleted() {
                if (observer != null) {
                    observer.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (observer != null) {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(T t) {
                if (observer != null) {
                    observer.onNext(t);
                }
            }
        };
        if (observer instanceof Subscriber) {
            subscriber.add(new Subscription() {
                @Override
                public void unsubscribe() {
                    ((Subscriber<T>) observer).unsubscribe();
                }

                @Override
                public boolean isUnsubscribed() {
                    return ((Subscriber<T>) observer).isUnsubscribed();
                }
            });
        }
        return subscriber;
    }

    public static RequestBody getRequestBody(@NonNull Map<String, ?> map) {
        return RequestBody.create(new JSONObject(map).toString(), MEDIA_TYPE_NORMAL);
    }

    public static RequestBody getRequestBody(@Nullable Object obj) {
        return RequestBody.create(obj == null ? "" : new Gson().toJson(obj), MEDIA_TYPE_NORMAL);
    }

    public static RequestBody getTextRequestBody(@NonNull Object value) {
        return RequestBody.create(String.valueOf(value), MEDIA_TYPE_TEXT);
    }

    public static RequestBody getFormRequestBody(@Nullable Object obj) {
        return RequestBody.create(obj == null ? "" : new Gson().toJson(obj), MEDIA_TYPE_FILE);
    }

    public static RequestBody getFileRequestBody(@NonNull String filePath) {
        return getFileRequestBody(new File(filePath));
    }

    public static RequestBody getFileRequestBody(@NonNull File file) {
        return RequestBody.create(file, MEDIA_TYPE_FILE);
    }

    public static MultipartBody.Part getPart(@NonNull String name, @NonNull File file) {
        final RequestBody requestBody = getFileRequestBody(file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestBody);
    }

    public static MultipartBody.Part getPart(@NonNull String name, @NonNull String filePath) {
        return getPart(name, new File(filePath));
    }

    public static List<MultipartBody.Part> getMultipartBodyParts(@NonNull String name, @NonNull List<File> files) {
        final List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            parts.add(MultipartBody.Part.createFormData(name, file.getName(), getFileRequestBody(file)));
        }
        return parts;
    }

    public static RequestBody getBase64RequestBody(@NonNull String base64) {
        return create(Base64.decode(base64, Base64.DEFAULT), MEDIA_TYPE_FILE);
    }

    public static RequestBody getFileRequestBody(@NonNull byte[] data) {
        return create(data, MEDIA_TYPE_FILE);
    }

    public static MultipartBody.Part getPart(@NonNull String name, @NonNull byte[] data) {
        final RequestBody requestBody = getFileRequestBody(data);
        return MultipartBody.Part.createFormData(name, null, requestBody);
    }

    public static List<MultipartBody.Part> getMultipartBodyPartsWithBytes(@NonNull String name, @NonNull List<byte[]> bytes) {
        final List<MultipartBody.Part> parts = new ArrayList<>(bytes.size());
        for (byte[] data : bytes) {
            parts.add(MultipartBody.Part.createFormData(name, null, getFileRequestBody(data)));
        }
        return parts;
    }

    public static RequestBody create(@NotNull final byte[] data, @Nullable final MediaType contentType) {
        Intrinsics.checkParameterIsNotNull(data, "data");
        return new RequestBody() {
            @Nullable
            public MediaType contentType() {
                return contentType;
            }

            public long contentLength() {
                return data.length;
            }

            public void writeTo(@NotNull BufferedSink sink) throws IOException {
                Intrinsics.checkParameterIsNotNull(sink, "sink");
                final Source schemaSource = Okio.source(new ByteArrayInputStream(data));
                Throwable throwable = null;

                try {
                    sink.writeAll(schemaSource);
                } catch (Exception e) {
                    throwable = e;
                    throw e;
                } finally {
                    try {
                        schemaSource.close();
                    } catch (IOException e) {
                        if (throwable != null) {
                            throwable.addSuppressed(e);
                        }
                    }
                }

            }
        };
    }
}
