/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Type type;
    private final TypeAdapter<T> adapter;
    private final String codeName;
    private final String messageName;
    private final String dataName;
    private final int validCode;

    GsonResponseBodyConverter(Type type, TypeAdapter<T> adapter, String codeName, String messageName, String dataName, int validCode) {
        this.type = type;
        this.adapter = adapter;
        this.codeName = codeName;
        this.messageName = messageName;
        this.dataName = dataName;
        this.validCode = validCode;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            final String json = value.string();
            final Object data = JsonCompat.get(json, this.dataName);
            final int code = JsonCompat.getInt(json, this.codeName);
            final String message = JsonCompat.getString(json, this.messageName);
            if (code != validCode) {
                throw new HttpException(message, code, data);
            }
            if (TypeCompat.isVoid(type) || data == null) {
                return null;
            }
            final Type dataType = TypeToken.get(data.getClass()).getType();
            //noinspection unchecked
            return dataType == this.type ? (T) data : adapter.fromJson(data.toString());
        } finally {
            value.close();
        }
    }

}
