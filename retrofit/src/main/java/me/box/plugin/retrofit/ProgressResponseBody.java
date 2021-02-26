package me.box.plugin.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by changlei on 2/26/21.
 * <p>
 * 接收进度
 */
public class ProgressResponseBody extends ResponseBody {
    @NonNull
    private final ResponseBody delegate;
    @Nullable
    private final ProgressListener progressListener;

    @Nullable
    private BufferedSource bufferedSource;

    public ProgressResponseBody(@NonNull ResponseBody delegate, @Nullable ProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        return delegate.contentLength();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(wrapProgressSource(delegate.source()));
        }
        return bufferedSource;
    }

    private Source wrapProgressSource(Source source) {
        return new ProgressSource(source, progressListener, contentLength());
    }

    private static class ProgressSource extends ForwardingSource {
        @Nullable
        private final ProgressListener progressListener;
        private final long contentLength;

        private long totalBytesRead = 0L;

        public ProgressSource(@NotNull Source delegate, @Nullable ProgressListener progressListener, long contentLength) {
            super(delegate);
            this.progressListener = progressListener;
            this.contentLength = contentLength;
        }

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
            final long bytesRead = super.read(sink, byteCount);
            // read() returns the number of bytes read, or -1 if this source is exhausted.
            totalBytesRead += bytesRead != -1 ? bytesRead : 0;

            if (null != progressListener) {
                HandlerCompat.runOnUiThread(() -> progressListener.update(totalBytesRead, contentLength, bytesRead == -1));
            }
            return bytesRead;
        }
    }
}