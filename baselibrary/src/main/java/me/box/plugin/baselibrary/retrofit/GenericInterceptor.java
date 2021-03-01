package me.box.plugin.baselibrary.retrofit;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.box.plugin.baselibrary.BaseApplication;
import me.box.plugin.baselibrary.util.Encrypt;
import me.box.plugin.baselibrary.util.Platform;
import me.box.plugin.retrofit.JsonCompat;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by changlei on 2/23/21.
 * <p>
 * 通用参数拦截器
 */
public class GenericInterceptor implements Interceptor {
    private static final String SECRET = "8C05HhgXB6xGo4qPNpkfdUEQtyTeJsRVizcZ1YwMILAuWvFm3l";
    private static final String APP_KEY = "EKCoHoXRyPGflavj51";

    @Nullable
    public static String sToken = null;

    @Nullable
    private final Map<String, Object> mGenericParams;

    public GenericInterceptor(@Nullable Map<String, Object> genericParams) {
        this.mGenericParams = genericParams;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        final Request request = chain.request();
        final String method = request.method();
        final RequestBody body = request.body();
        final HttpUrl url = request.url();

        final Map<String, Object> queryParams = new LinkedHashMap<>();
        for (int i = 0; i < url.querySize(); i++) {
            queryParams.put(url.queryParameterName(i), url.queryParameterValue(i));
        }

        final Map<String, Object> bodyParams = toMap(request.headers(), body);
        Object password = bodyParams.remove("password");
        if (password == null) {
            password = queryParams.remove("password");
        }
        if (password == null) {
            password = "";
        }

        final Map<String, Object> genericParams = new LinkedHashMap<>();
        genericParams.put("appkey", APP_KEY);
        genericParams.put("code", getRandomString());
        genericParams.put("timestamp", System.currentTimeMillis() / 1000);
        genericParams.put("esn", Platform.getImei());
        genericParams.put("clientver", getVersionName());
        if (mGenericParams != null) {
            genericParams.putAll(mGenericParams);
        }
        if (!TextUtils.isEmpty(sToken)) {
            genericParams.put("token", sToken);
        }

        final Map<String, Object> signMap = new LinkedHashMap<>(queryParams);
        signMap.putAll(genericParams);
        if (!bodyParams.isEmpty()) {
            signMap.put("postdata", JsonCompat.objectToJson(bodyParams));
        }

        final List<String> keys = new ArrayList<>(signMap.keySet());
        Collections.sort(keys, (o1, o2) -> o1.toLowerCase().compareTo(o2.toLowerCase()));
        final StringBuilder paramsValueBuilder = new StringBuilder();
        for (String key : keys) {
            final Object value = signMap.get(key);
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                continue;
            }
            paramsValueBuilder.append(value);
        }

        final StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(SECRET).append(password);
        signBuilder.append(paramsValueBuilder);
        signBuilder.append(SECRET).append(password);

        final Map<String, Object> oldQueryParams = new LinkedHashMap<>(queryParams);
        queryParams.clear();
        queryParams.putAll(genericParams);
        queryParams.putAll(oldQueryParams);
        queryParams.put("sign", Encrypt.encryptMD5(signBuilder));

        HttpUrl.Builder urlBuilder = url.newBuilder();
        for (int i = 0; i < url.querySize(); i++) {
            urlBuilder = urlBuilder.removeAllEncodedQueryParameters(url.queryParameterName(i));
        }
        for (String key : queryParams.keySet()) {
            final Object value = queryParams.get(key);
            if (value == null) {
                continue;
            }
            urlBuilder = urlBuilder.addEncodedQueryParameter(key, value.toString());
        }

        Request.Builder builder = request.newBuilder()
                .addHeader("tokenType", "Android")
                .addHeader("contentType", "application/json;charset=utf-8")
                .addHeader("Accept", "application/json")
                .url(urlBuilder.build());
        if (body != null && !bodyParams.isEmpty()) {
            builder = builder.method(method, RequestBody.create(JsonCompat.objectToJson(bodyParams), body.contentType()));
        }
        return chain.proceed(builder.build());
    }

    private static String getRandomString() {
        final String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        final Random random = new Random();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            buffer.append(base.charAt(random.nextInt(base.length())));
        }
        return buffer.toString();
    }

    private static Map<String, Object> toMap(Headers headers, RequestBody body) {
        final Map<String, Object> bodyParams = new LinkedHashMap<>();
        if (body != null && !bodyHasUnknownEncoding(headers) && !body.isDuplex()) {
            try {
                final Buffer buffer = new Buffer();
                body.writeTo(buffer);
                final String bodyJson = buffer.readString(StandardCharsets.UTF_8);
                final Map<String, Object> toMap = JsonCompat.jsonToMap(bodyJson);
                bodyParams.putAll(toMap);
            } catch (Exception ignored) {
            }
        }
        return bodyParams;
    }

    private static String getVersionName() {
        final Application context = BaseApplication.getInstance();
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception ignored) {
        }
        return versionName;
    }

    private static boolean bodyHasUnknownEncoding(Headers headers) {
        final String encoding = headers.get("Content-Encoding");
        if (encoding == null) {
            return false;
        }
        return !encoding.equalsIgnoreCase("identity") &&
                !encoding.equalsIgnoreCase("gzip");
    }
}
