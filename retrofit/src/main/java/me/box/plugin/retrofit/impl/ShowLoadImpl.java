/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit.impl;

/**
 * Created by box on 2018/5/28.
 * <p>
 * 显示加载进度条
 */
public interface ShowLoadImpl {

    RetrofitProgressImpl showRetrofitLoad();

    void loadDismiss();
}
