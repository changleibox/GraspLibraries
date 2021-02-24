/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Type type;
    private final TypeAdapter<T> adapter;
    private final String code;
    private final String message;
    private final String data;
    private final int validCode;

    GsonResponseBodyConverter(Type type, TypeAdapter<T> adapter, String code, String message, String data, int validCode) {
        this.type = type;
        this.adapter = adapter;
        this.code = code;
        this.message = message;
        this.data = data;
        this.validCode = validCode;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        final String json = value.string();
        try {
            final JSONObject object = new JSONObject(json);
            final int code = object.getInt(this.code);
            final String message = object.getString(this.message);
            if (code != validCode) {
                throw new HttpException(message, code);
            }
        } catch (Exception e) {
            IOException exception;
            if (e instanceof IOException) {
                exception = (IOException) e;
            } else {
                exception = new IOException(e.getMessage());
            }
            throw exception;
        }
        if (TypeCompat.isVoid(type)) {
            return null;
        }
        final Type tmpType = TypeCompat.getResponseType(type);
        if (tmpType != null) {
            //noinspection unchecked
            return (T) JsonCompat.get(json, data);
        }
        final String data = JsonCompat.getString(json, this.data);
        if (TextUtils.isEmpty(data)) {
            return null;
        } else {
            assert data != null;
            return adapter.fromJson(data);
        }
    }

}
