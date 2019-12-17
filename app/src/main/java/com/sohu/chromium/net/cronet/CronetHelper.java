package com.sohu.chromium.net.cronet;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;

import com.sohu.chromium.net.Config;
import com.sohu.chromium.net.LogUtils;
import com.sohu.chromium.net.callback.IDownloadTest;
import com.sohu.chromium.net.callback.ProgressCallback;
import com.sohu.chromium.net.threadPool.ThreadPoolManager;

import org.chromium.net.CronetEngine;
import org.chromium.net.ExperimentalCronetEngine;
import org.chromium.net.RequestFinishedInfo;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by wangyan on 2019/3/31
 */
public class CronetHelper implements IDownloadTest {
    private static final String TAG = "CronetHelper";
    private static final String NET_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp/Cronet";
    private ExperimentalCronetEngine cronetEngine;
    private Executor executor;      //这个线程池作为回调使用，如果支持多个线程的话，会导致大量的回调到界面，导致界面卡死，暂时使用单线程池
    private UrlRequest.Callback callback;

    private static CronetHelper helper = null;

    private CronetHelper() {
        executor = ThreadPoolManager.getInstance().getSingleExecutor();
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
        ExperimentalCronetEngine.Builder builder = new ExperimentalCronetEngine.Builder(context);
        builder.enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISABLED, 100 * 1024) // cache
                .addQuicHint("www.bgylde.com", 443, 443)
                .enableHttp2(true)  // Http/2.0 Supprot
                .enableQuic(true);   // Quic Supprot
        cronetEngine = builder.build();
        cronetEngine.addRequestFinishedListener(new RequestFinishedInfo.Listener(executor) {
            @Override
            public void onRequestFinished(RequestFinishedInfo requestFinishedInfo) {
                onRequestFinishedHandle(requestFinishedInfo);
            }
        });
        LogUtils.d(TAG, NET_LOG_PATH);
//        cronetEngine.startNetLogToFile(NET_LOG_PATH, false);
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

    public void downloadCallback(String url, ProgressCallback callback) {
        UrlRequest.Callback downloadCallback = new DownloadRequestCallback(callback);
        UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(url, downloadCallback, executor);
        builder.disableCache().allowDirectExecutor();
        builder.build().start();
    }

    @Override
    public void downloadTest(String url, ProgressCallback callback) {
        if (url == null || url.trim().length() <= 0) {
            return;
        }

        InputStream inputStream = null;
        try {
            URL httpUrl = new URL(url);
            URLConnection httpConnection = cronetEngine.openConnection(httpUrl);
//            httpConnection.setConnectTimeout(100000);
//            httpConnection.setReadTimeout(100000);
            httpConnection.setUseCaches(false);

            long startTime = SystemClock.elapsedRealtime();
            httpConnection.connect();
            long durationTime = SystemClock.elapsedRealtime() - startTime;
            //LogUtils.d(TAG, "durationTime: " + durationTime);
            long lengthLong = httpConnection.getContentLength();
            //LogUtils.d(TAG, "length: " + lengthLong);
            inputStream = httpConnection.getInputStream();
            int rc = 0;
            long currentBytes = 0;
            DecimalFormat df = new DecimalFormat("#####0.00");
            byte[] buff = new byte[102400];
            long startReadTime = SystemClock.elapsedRealtime();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            while ((rc = inputStream.read(buff, 0, 102400)) > 0) {
                swapStream.write(buff, 0, rc);
                currentBytes += rc;
                double result = (double)currentBytes * 100 / (double)lengthLong;
                //LogUtils.d(TAG, "downloading---" + df.format(result) + "%");
                if (callback != null) {
                    callback.progress((int)result);
                }
            }

            byte[] in2b = swapStream.toByteArray();
            swapStream.close();
            inputStream.close();
            long costTime = (SystemClock.elapsedRealtime() - startReadTime);
            long speed = (in2b.length >> 10) * 1000 / costTime;
            if (callback != null) {
                callback.complete(Config.formatUnion(in2b.length), Config.formatTime((int)costTime), Config.formatString("%sKB/S", speed));
            }

            //String result = "length: " + Config.formatUnion(in2b.length) + " time: " + Config.formatTime((int)costTime) + " speed: " + speed + " KB/S";
            //LogUtils.d(TAG, result);
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        }
    }

    /**
     * Print the request info.
     * @param requestInfo requestInfo
     */
    private static void onRequestFinishedHandle(final RequestFinishedInfo requestInfo) {
        if (!LogUtils.isDebug()) {
            return;
        }

        LogUtils.d(TAG, "############# url: " + requestInfo.getUrl() + " #############");
        LogUtils.d(TAG, "onRequestFinished: " + requestInfo.getFinishedReason());
        RequestFinishedInfo.Metrics metrics = requestInfo.getMetrics();
        if (metrics != null) {
            LogUtils.d(TAG, "RequestStart: "   + (metrics.getRequestStart()    == null ? -1 : metrics.getRequestStart().getTime()));
            LogUtils.d(TAG, "DnsStart: "       + (metrics.getDnsStart()        == null ? -1 : metrics.getDnsStart().getTime()));
            LogUtils.d(TAG, "DnsEnd: "         + (metrics.getDnsEnd()          == null ? -1 : metrics.getDnsEnd().getTime()));
            LogUtils.d(TAG, "ConnectStart: "   + (metrics.getConnectStart()    == null ? -1 : metrics.getConnectStart().getTime()));
            LogUtils.d(TAG, "ConnectEnd: "     + (metrics.getConnectEnd()      == null ? -1 : metrics.getConnectEnd().getTime()));
            LogUtils.d(TAG, "SslStart: "       + (metrics.getSslStart()        == null ? -1 : metrics.getSslStart().getTime()));
            LogUtils.d(TAG, "SslEnd: "         + (metrics.getSslEnd()          == null ? -1 : metrics.getSslEnd().getTime()));
            LogUtils.d(TAG, "SendingStart: "   + (metrics.getSendingStart()    == null ? -1 : metrics.getSendingStart().getTime()));
            LogUtils.d(TAG, "SendingEnd: "     + (metrics.getSendingEnd()      == null ? -1 : metrics.getSendingEnd().getTime()));
            LogUtils.d(TAG, "PushStart: "      + (metrics.getPushStart()       == null ? -1 : metrics.getPushStart().getTime()));
            LogUtils.d(TAG, "PushEnd: "        + (metrics.getPushEnd()         == null ? -1 : metrics.getPushEnd().getTime()));
            LogUtils.d(TAG, "ResponseStart: "  + (metrics.getResponseStart()   == null ? -1 : metrics.getResponseStart().getTime()));
            LogUtils.d(TAG, "RequestEnd: "     + (metrics.getRequestEnd()      == null ? -1 : metrics.getRequestEnd().getTime()));
            LogUtils.d(TAG, "TotalTimeMs: "    + metrics.getTotalTimeMs());
            LogUtils.d(TAG, "RecvByteCount: "  + metrics.getReceivedByteCount());
            LogUtils.d(TAG, "SentByteCount: "  + metrics.getSentByteCount());
            LogUtils.d(TAG, "SocketReused: "   + metrics.getSocketReused());
            LogUtils.d(TAG, "TtfbMs: "         + metrics.getTtfbMs());
        }

        Exception exception = requestInfo.getException();
        if (exception != null) {
            LogUtils.e(TAG, exception);
        }

        UrlResponseInfo urlResponseInfo = requestInfo.getResponseInfo();
        if (urlResponseInfo != null) {
            LogUtils.d(TAG, "Cache: " + urlResponseInfo.wasCached());
            LogUtils.d(TAG, "Protocol: " + urlResponseInfo.getNegotiatedProtocol());
            LogUtils.d(TAG, "HttpCode: " + urlResponseInfo.getHttpStatusCode());
            LogUtils.d(TAG, "ProxyServer: " + urlResponseInfo.getProxyServer());
            List<Map.Entry<String, String>> headers = urlResponseInfo.getAllHeadersAsList();
            for (Map.Entry<String, String> entry : headers) {
                LogUtils.d(TAG, "=== " + entry.getKey() + " : " + entry.getValue() + " ===");
            }
        }

        LogUtils.d(TAG , "############# END #############");
    }
}
