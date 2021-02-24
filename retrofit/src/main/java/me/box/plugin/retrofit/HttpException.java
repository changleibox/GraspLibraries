/*
 * Copyright (c) 2021 CHANGLEI. All rights reserved.
 */

package me.box.plugin.retrofit;

import java.io.IOException;

/**
 * Created by Box on 17/3/15.
 * <p/>
 * 错误
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "WeakerAccess"})
public class HttpException extends IOException {
    private static final long serialVersionUID = 8645845574230995197L;

    private final int code;

    public HttpException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public HttpException(String msg, int code, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
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
