/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.impl;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by changlei on 2/25/21.
 * <p>
 * Fragment生命周期
 */
public interface FragmentLifecycle {
    void onAttach(@NonNull Context context);

    void onCreate(@Nullable Bundle savedInstanceState);

    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroyView();

    void onDestroy();

    void onDetach();
}
