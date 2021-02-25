/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit.impl;

/**
 * Created by box on 2018/5/28.
 * <p>
 * 显示网络请求错误信息
 */
public interface OnObserverErrorListener {
    void onError(Throwable throwable);
}
