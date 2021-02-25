/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.impl;

import me.box.plugin.retrofit.impl.LifecycleImpl;
import me.box.plugin.retrofit.impl.RetrofitContext;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * 实现自定义Context逻辑，接口模板
 */
public interface ContextWrapper extends RetrofitContext, LifecycleImpl {
}