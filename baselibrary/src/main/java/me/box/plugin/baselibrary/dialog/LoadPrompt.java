/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import me.box.plugin.retrofit.impl.RetrofitProgressImpl;

/**
 * Created by changlei on 2/24/21.
 * <p>
 * 加载框
 */
public class LoadPrompt extends ProgressDialog implements RetrofitProgressImpl {
    public LoadPrompt(Context context) {
        super(context);
        setMessage("正在加载，请稍后");
        getWindow().setDimAmount(0);
    }
}
