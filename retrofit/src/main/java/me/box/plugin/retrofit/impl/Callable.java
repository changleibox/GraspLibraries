/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit.impl;

/**
 * Created by changlei on 3/2/21.
 * <p>
 * 调用
 */
public interface Callable<T> {
    T call() throws Throwable;
}
