/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.impl;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * activity生命周期
 */
public interface ActivityLifecycle {
    void onCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
