package me.box.plugin.retrofit.impl;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 进度回调
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}