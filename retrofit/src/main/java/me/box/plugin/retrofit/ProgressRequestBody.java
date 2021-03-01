package me.box.plugin.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 发送进度
 */
public class ProgressRequestBody extends RequestBody {
    @NonNull
    private final RequestBody delegate;
    @Nullable
    private final ProgressListener progressListener;

    @Nullable
    private BufferedSink bufferedSink;

    public ProgressRequestBody(@NonNull RequestBody delegate, @Nullable ProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        if (sink instanceof Buffer) {
            return;
        }
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(wrapProgressSink(sink));
        }
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink wrapProgressSink(Sink sink) throws IOException {
        return new ProgressSink(sink, progressListener, contentLength());
    }

    private static class ProgressSink extends ForwardingSink {
        @Nullable
        private final ProgressListener progressListener;
        private final long contentLength;

        private long totalBytesWrite = 0L;

        public ProgressSink(@NotNull Sink delegate, @Nullable ProgressListener progressListener, long contentLength) {
            super(delegate);
            this.progressListener = progressListener;
            this.contentLength = contentLength;
        }

        @Override
        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            totalBytesWrite += byteCount != -1 ? byteCount : 0;

            if (null != progressListener) {
                HandlerCompat.runOnUiThread(() -> progressListener.update(totalBytesWrite, contentLength, contentLength == totalBytesWrite));
            }
        }
    }
}