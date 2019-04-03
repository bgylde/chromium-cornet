package com.sohu.chromium.net.okhttp;

import android.os.SystemClock;

import com.sohu.chromium.net.Config;
import com.sohu.chromium.net.LogUtils;
import com.sohu.chromium.net.callback.IDownloadTest;
import com.sohu.chromium.net.callback.ProgressCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangyan on 2019/3/30
 */
public class OkhttpHelper implements IDownloadTest {

    private static final String TAG = "OkhttpHelper";
    private static OkhttpHelper helper = null;
    private static int timeout = 600;

    private OkHttpClient client = null;

    private OkhttpHelper() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        client = builder.writeTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    public static OkhttpHelper getHelper() {
        if (helper == null) {
            synchronized (OkhttpHelper.class) {
                if (helper == null) {
                    helper = new OkhttpHelper();
                }
            }
        }

        return helper;
    }

    @Override
    public void downloadTest(String url, ProgressCallback callback) {
        Request request = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(url).build();

        Call call = client.newCall(request);
        Response response = null;
        ResponseBody body = null;
        InputStream inputStream = null;
        byte[] buf = new byte[102400];

        try {
            long startTime = SystemClock.elapsedRealtime();
            response = call.execute();
            body = response.body();
            long totalLength = 0;
            if (body != null) {
                int len = 0;
                long currentLen = 0;
                totalLength = body.contentLength();

                inputStream = body.byteStream();
                while((len = inputStream.read(buf)) != -1) {
                    currentLen += len;
                    double result = (double)currentLen * 100 / (double)totalLength;
                    callback.progress((int)(result));
                }
            }

            int costTime = (int)(SystemClock.elapsedRealtime() - startTime);
            long speed = (totalLength >> 10) * 1000 / costTime;
            callback.complete(Config.formatUnion(totalLength), Config.formatTime(costTime), speed + "KB/S");
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void downloadTestAsyn(String url, ProgressCallback callback) {
        Request request = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(url).build();

        Call call = client.newCall(request);
        long startTime = SystemClock.elapsedRealtime();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e(TAG, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                InputStream inputStream = null;
                byte[] buf = new byte[102400];
                long totalLength = 0;
                try {
                    if (body != null) {
                        int len = 0;
                        long currentLen = 0;
                        totalLength = body.contentLength();

                        inputStream = body.byteStream();
                        while ((len = inputStream.read(buf)) != -1) {
                            currentLen += len;
                            double result = (double) currentLen * 100 / (double) totalLength;
                            callback.progress((int) (result));
                        }
                    }

                    int costTime = (int) (SystemClock.elapsedRealtime() - startTime);
                    long speed = (totalLength >> 10) * 1000 / costTime;
                    callback.complete(Config.formatUnion(totalLength), Config.formatTime(costTime), speed + "KB/S");
                } finally {
                    if (body != null) {
                        body.close();
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (response != null) {
                        response.close();
                    }
                }
            }
        });
    }
}
