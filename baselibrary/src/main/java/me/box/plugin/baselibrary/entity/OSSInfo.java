/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.entity;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import me.box.plugin.baselibrary.util.Encrypt;
import me.box.plugin.retrofit.JsonCompat;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * oss授权信息
 */
public class OSSInfo implements Serializable {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
    private static final String ALGORITHM = "HmacSHA1";
    private static final int RESULT_STATUS = 200;

    @SerializedName("AppKey")
    private String appKey;
    @SerializedName("AppSecret")
    private String appSecret;
    @SerializedName("TempBucket")
    private String tempBucket;
    @SerializedName("ImgBucket")
    private String imgBucket;
    @SerializedName("PublicEndPoint")
    private String publicEndPoint;
    @SerializedName("ImgEndPoint")
    private String imgEndPoint;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getTempBucket() {
        return tempBucket;
    }

    public void setTempBucket(String tempBucket) {
        this.tempBucket = tempBucket;
    }

    public String getImgBucket() {
        return imgBucket;
    }

    public void setImgBucket(String imgBucket) {
        this.imgBucket = imgBucket;
    }

    public String getPublicEndPoint() {
        return publicEndPoint;
    }

    public void setPublicEndPoint(String publicEndPoint) {
        this.publicEndPoint = publicEndPoint;
    }

    public String getImgEndPoint() {
        return imgEndPoint;
    }

    public void setImgEndPoint(String imgEndPoint) {
        this.imgEndPoint = imgEndPoint;
    }

    public String getPolicy(long fileLength) {
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

    public String getSignature(String policy) {
        try {
            final String appSecret = Encrypt.decrypt(this.appSecret);
            final byte[] key = appSecret.getBytes(StandardCharsets.UTF_8);
            final byte[] data = policy.getBytes(StandardCharsets.UTF_8);
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            return Base64.encodeToString(mac.doFinal(data), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTempHost() {
        final String tempPoint = Encrypt.decrypt(this.publicEndPoint);
        final String tempBucket = Encrypt.decrypt(this.tempBucket);
        return tempPoint.replace("http://", String.format("http://%s.", tempBucket));
    }

    public String getOssAccessKeyId() {
        return Encrypt.decrypt(this.appKey);
    }

    public String getKey(File file) {
        final String fileName = file.getName();
        final int pointIndex = fileName.lastIndexOf(".");
        String extension = "";
        if (pointIndex != -1) {
            extension = fileName.substring(pointIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }

    public OSSFormData convert(File file) {
        final String policy = getPolicy(file.length());
        final OSSFormData formData = new OSSFormData();
        formData.setFileName(file.getName());
        formData.setKey(getKey(file));
        formData.setOssAccessKeyId(getOssAccessKeyId());
        formData.setPolicy(policy);
        formData.setSignature(getSignature(policy));
        formData.setSuccessActionStatus(RESULT_STATUS);
        formData.setFile(file);
        return formData;
    }
}
