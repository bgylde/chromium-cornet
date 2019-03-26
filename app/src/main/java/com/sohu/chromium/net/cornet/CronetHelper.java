package com.sohu.chromium.net.cornet;

import android.content.Context;
import android.os.Environment;

import com.sohu.chromium.net.utils.LogUtils;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wangyan on 2019/3/26
 */
public class CronetHelper {

    private static final String TAG = "CronetHelper";
    private static final String NET_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp/Cronet";
    private CronetEngine cronetEngine;
    private Executor executor;
    private UrlRequest.Callback callback;

    private static CronetHelper helper = null;

    private CronetHelper() {
        executor = Executors.newSingleThreadExecutor();
        callback = new UrlRequestCallback();
    }

    public static CronetHelper getInstance() {
        if (helper == null) {
            synchronized (CronetHelper.class) {
                if (helper == null) {
                    helper = new CronetHelper();
                }
            }
        }

        return helper;
    }

    public void init(Context context) {
        CronetEngine.Builder builder = new CronetEngine.Builder(context);
        builder.enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISABLED, 100 * 1024) // cache
                .enableHttp2(true)  // Http/2.0 Supprot
                .enableQuic(true);   // Quic Supprot
        cronetEngine = builder.build();
        LogUtils.d(TAG, NET_LOG_PATH);
        cronetEngine.startNetLogToFile(NET_LOG_PATH, false);
        LogUtils.d(TAG, "version: " + cronetEngine.getVersionString());
    }

    public void requestUrl(String url) {
        LogUtils.d(TAG, "requestUrl start");
        getHtml(url, callback);
    }

    private void getHtml(String url, UrlRequest.Callback callback) {
        startWithURL(url, callback);
//        try {
//            HttpURLConnection connection = (HttpURLConnection)cronetEngine.openConnection(new URL(url));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private void startWithURL(String url, UrlRequest.Callback callback) {
        startWithURL(url, callback, null);
    }

    private void startWithURL(String url, UrlRequest.Callback callback, String postData) {
        UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(url, callback, executor);
        applyPostDataToUrlRequestBuilder(builder, executor, postData);
        builder.build().start();
    }

    private void applyPostDataToUrlRequestBuilder(UrlRequest.Builder builder, Executor executor, String postData) {
        if (postData != null && postData.length() > 0) {
            builder.setHttpMethod("POST");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
            builder.setUploadDataProvider(UploadDataProviders.create(postData.getBytes()), executor);
        }
    }
}
