/*
 * Copyright © 2017 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.delegate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

import me.box.plugin.baselibrary.util.FileUtils;
import me.box.plugin.retrofit.ResponseProgressInterceptor;
import me.box.plugin.retrofit.impl.ProgressListener;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by box on 2017/5/22.
 * <p>
 * 文件下载器
 */
@SuppressWarnings({"WeakerAccess"})
public class Downloader {
    private static final int DEFAULT_TIMEOUT = 15;

    public final Retrofit mRetrofit;

    public Downloader(@Nullable ProgressListener listener) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ResponseProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public void download(@NonNull String url, final File file, Observer<File> subscriber) {
        mRetrofit.create(DownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(responseBody -> FileUtils.writeFile(responseBody.byteStream(), file))
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public interface DownloadService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
}
