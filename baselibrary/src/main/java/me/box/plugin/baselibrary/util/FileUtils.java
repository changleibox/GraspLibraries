/*
 * Copyright © 2017 CHANGLEI. All rights reserved.
 */

package me.box.plugin.baselibrary.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by Box on 16/9/19.
 * <p>
 * 操作文件相关
 */
@SuppressWarnings({"JavaDoc", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class FileUtils {
    /**
     * 保存图片方法
     *
     * @param bitmap     图片
     * @param targetFile 目标文件
     */
    public static File writeFile(Bitmap bitmap, File targetFile) {
        File file = new File(Objects.requireNonNull(targetFile.getParent()));
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(targetFile.getParent(), targetFile.getName());
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        context.startActivity(Intent.createChooser(intent, "Choose File"));
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private static String getMIMEType(File file) {
        String type = "*/*";
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = name.substring(dotIndex).toLowerCase();
        if (TextUtils.isEmpty(end)) {
            return type;
        }
        for (String[] strings : MIME_MAP_TABLE) {
            if (end.equals(strings[0])) {
                type = strings[1];
            }
        }
        return type;
    }

    @WorkerThread
    public static String writeFile(byte[] data, String dir) {
        return writeFile(data, dir, System.currentTimeMillis() + ".jpg");
    }

    @WorkerThread
    public static String writeFile(byte[] data, String dir, String fileName) {
        return writeFile(data, new File(dir, fileName)).getAbsolutePath();
    }

    @WorkerThread
    public static File writeFile(byte[] data, File targetFile) {
        File file = new File(Objects.requireNonNull(targetFile.getParent()));
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(targetFile.getParent(), targetFile.getName());

        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();
        }
        return file;
    }

    @Nullable
    @WorkerThread
    public static File writeFile(InputStream inStream, File source) {
        try {
            return writeFile(input2byte(inStream), source);
        } catch (IOException ignored) {
        }
        return null;
    }

    public static byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    // MIME_MapTable是所有文件的后缀名所对应的MIME类型的一个String数组：
    private static final String[][] MIME_MAP_TABLE = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

}
