/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import androidx.annotation.Nullable;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import retrofit2.Response;

/**
 * Created by box on 2017/4/27.
 * <p>
 * 返回实体类型
 */

public class TypeCompat {

    public static boolean isVoid(Type type) {
        return $Gson$Types.equals(type, Void.class);
    }

    @Nullable
    public static Type getResponseType(Type type) {
        Type tmpType = null;
        if ($Gson$Types.equals(type, String.class)) {
            tmpType = new TypeToken<Response<String>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Byte.class)) {
            tmpType = new TypeToken<Response<Byte>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Double.class)) {
            tmpType = new TypeToken<Response<Double>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Float.class)) {
            tmpType = new TypeToken<Response<Float>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Integer.class)) {
            tmpType = new TypeToken<Response<Integer>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Long.class)) {
            tmpType = new TypeToken<Response<Long>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Short.class)) {
            tmpType = new TypeToken<Response<Short>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, BigDecimal.class)) {
            tmpType = new TypeToken<Response<BigDecimal>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, BigInteger.class)) {
            tmpType = new TypeToken<Response<BigInteger>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Number.class)) {
            tmpType = new TypeToken<Response<Number>>() {
            }.getType();
        } else if ($Gson$Types.equals(type, Boolean.class)) {
            tmpType = new TypeToken<Response<Boolean>>() {
            }.getType();
        }
        return tmpType;
    }

}
