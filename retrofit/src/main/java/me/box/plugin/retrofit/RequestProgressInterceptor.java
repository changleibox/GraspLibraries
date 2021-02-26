package me.box.plugin.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 发送进度拦截器
 */
public class RequestProgressInterceptor implements Interceptor {
    @Nullable
    private final ProgressListener listener;

    public RequestProgressInterceptor(@Nullable ProgressListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request request = chain.request();
        final RequestBody delegate = request.body();
        if (delegate == null) {
            return chain.proceed(request);
        }
        return chain.proceed(chain.request().newBuilder()
                .method(request.method(), new ProgressRequestBody(delegate, listener))
                .build());
    }
}
