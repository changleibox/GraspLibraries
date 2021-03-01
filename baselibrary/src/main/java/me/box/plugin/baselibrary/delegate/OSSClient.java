/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.delegate;

import android.util.Base64;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import me.box.plugin.baselibrary.entity.OSSFormData;
import me.box.plugin.retrofit.JsonCompat;
import me.box.plugin.retrofit.impl.ProgressListener;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by changlei on 3/1/21.
 * <p>
 * oss客户端
 */
@SuppressWarnings("UnusedReturnValue")
public class OSSClient {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
    private static final String ALGORITHM = "HmacSHA1";
    private static final int RESULT_STATUS = 200;

    private final Uploader mUploader;

    @NonNull
    private final String endpoint;
    @NonNull
    private final String accessKeyId;
    @NonNull
    private final String accessKeySecret;

    public OSSClient(@NonNull String endpoint, @NonNull String accessKeyId, @NonNull String accessKeySecret) {
        this(endpoint, accessKeyId, accessKeySecret, null);
    }

    public OSSClient(@NonNull String endpoint, @NonNull String accessKeyId, @NonNull String accessKeySecret, @Nullable ProgressListener progressListener) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;

        this.mUploader = new Uploader(progressListener);
    }

    private String getPolicy(long fileLength) {
        final Map<String, Object> policyData = new HashMap<>();
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        policyData.put("expiration", DATE_FORMAT.format(calendar.getTime()));
        policyData.put("conditions", new Object[]{new Object[]{"content-length-range", 0, fileLength}});
        final String policyJson = JsonCompat.objectToJson(policyData);
        return Base64.encodeToString(policyJson.getBytes(), Base64.NO_WRAP);
    }

    private String getSignature(String policy) {
        try {
            final byte[] key = accessKeySecret.getBytes(StandardCharsets.UTF_8);
            final byte[] data = policy.getBytes(StandardCharsets.UTF_8);
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            return Base64.encodeToString(mac.doFinal(data), Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("生成签名错误，请检查参数");
        }
    }

    private String getOssAccessKeyId() {
        return accessKeyId;
    }

    private OSSFormData convert(String objectName, File file) {
        final String policy = getPolicy(file.length());
        final OSSFormData formData = new OSSFormData();
        formData.setFileName(file.getName());
        formData.setKey(objectName);
        formData.setOssAccessKeyId(getOssAccessKeyId());
        formData.setPolicy(policy);
        formData.setSignature(getSignature(policy));
        formData.setSuccessActionStatus(RESULT_STATUS);
        formData.setFile(file);
        return formData;
    }

    public Subscription putObject(@NotNull String bucket, @NonNull String objectName, @NonNull File file, @Nullable Observer<Void> callback) {
        final Observable<Void> observable = asObservable(bucket, objectName, file);
        return callback == null ? observable.subscribe() : observable.subscribe(callback);
    }

    public Observable<Void> asObservable(@NotNull String bucket, @NonNull String objectName, @NonNull File file) {
        final OSSFormData formData = convert(objectName, file);
        final String url = endpoint.replace("http://", String.format("http://%s.", bucket));
        return mUploader.asObservable(url, formData);
    }
}
