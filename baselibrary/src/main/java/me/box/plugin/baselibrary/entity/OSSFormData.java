/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.File;

import me.box.plugin.baselibrary.delegate.Uploader;

public class OSSFormData implements Uploader.FormData {
    @SerializedName("Filename")
    private String fileName;
    @SerializedName("key")
    private String key;
    @SerializedName("policy")
    private String policy;
    @SerializedName("OSSAccessKeyId")
    private String ossAccessKeyId;
    @SerializedName("success_action_status")
    private Integer successActionStatus;
    @SerializedName("signature")
    private String signature;
    @SerializedName("file")
    private File file;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(@NonNull String policy) {
        this.policy = policy;
    }

    public String getOssAccessKeyId() {
        return ossAccessKeyId;
    }

    public void setOssAccessKeyId(@NonNull String ossAccessKeyId) {
        this.ossAccessKeyId = ossAccessKeyId;
    }

    public Integer getSuccessActionStatus() {
        return successActionStatus;
    }

    public void setSuccessActionStatus(@NonNull Integer successActionStatus) {
        this.successActionStatus = successActionStatus;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(@NonNull String signature) {
        this.signature = signature;
    }

    public File getFile() {
        return file;
    }

    public void setFile(@NonNull File file) {
        this.file = file;
    }
}