package me.box.plugin.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 接收进度拦截器
 */
public class ResponseProgressInterceptor implements Interceptor {
    @Nullable
    private final ProgressListener listener;

    public ResponseProgressInterceptor(@Nullable ProgressListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());
        final ResponseBody delegate = response.body();
        if (delegate == null) {
            return response;
        }
        return response.newBuilder()
                .body(new ProgressResponseBody(delegate, listener))
                .build();
    }
}
