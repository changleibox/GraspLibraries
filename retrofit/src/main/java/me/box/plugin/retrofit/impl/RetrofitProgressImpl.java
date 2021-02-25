/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit.impl;

import android.content.DialogInterface;

/**
 * Created by box on 2018/5/28.
 * <p>
 * 进度对话框
 */
public interface RetrofitProgressImpl {
    void show();

    void setOnCancelListener(DialogInterface.OnCancelListener l);
}
