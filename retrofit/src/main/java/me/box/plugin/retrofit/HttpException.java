/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * Created by Box on 17/3/15.
 * <p/>
 * 错误
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "WeakerAccess"})
public class HttpException extends IOException {
    private static final long serialVersionUID = 8645845574230995197L;

    private final int code;
    @Nullable
    private final Object value;

    public HttpException(String msg, int code, @Nullable Object value) {
        super(msg);
        this.code = code;
        this.value = value;
    }

    public HttpException(String msg, Throwable cause, int code, @Nullable Object value) {
        super(msg, cause);
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    /**
     * @return 请求到的实体，可能为空
     */
    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || (obj instanceof HttpException && ((HttpException) obj).getCode() == getCode());
    }

    public boolean equals(HttpException e) {
        return equals(this, e);
    }

    public boolean equals(int code) {
        return this.code == code;
    }

    public static boolean equals(Throwable e1, int code) {
        return e1 instanceof HttpException && ((HttpException) e1).equals(code);
    }

    public static boolean equals(Throwable e1, HttpException e2) {
        return !(e1 == null || e2 == null) && e2.equals(e1);
    }
}
