package com.sohu.cronet;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;

import org.chromium.net.CronetEngine;
import org.chromium.net.ExperimentalCronetEngine;
import org.chromium.net.HostResolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wangyan on 2019/4/4
 */
public class CronetHelper {
    private static final String TAG = "CronetHelper";
    private ExperimentalCronetEngine cronetEngine;
    private Executor executor;      //这个线程池作为回调使用，如果支持多个线程的话，会导致大量的回调到界面，导致界面卡死，暂时使用单线程池

    private static CronetHelper helper = null;

    private CronetHelper() {
        executor = Executors.newSingleThreadExecutor();
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
//                .setHostResolver(new HostResolver() {
//                    @Override
//                    public List<InetAddress> resolve(String hostname) throws UnknownHostException {
//                        if (hostname == null)
//                            throw new UnknownHostException("hostname == null");
//
//                        if ("www.bgylde.com".equals(hostname)) {
//                            List<InetAddress> result = new ArrayList<>();
//                            byte ip[] = new byte[] { (byte)10, (byte)2, (byte)146, (byte)151};
//                            result.add(InetAddress.getByAddress(hostname, ip));
//                            return result;
//                        }
//
//                        return Arrays.asList(InetAddress.getAllByName(hostname));
//                    }
//                })
                .addQuicHint("www.bgylde.com", 443, 443)
                .enableHttp2(true)  // Http/2.0 Supprot
                .enableQuic(true);   // Quic Supprot
        cronetEngine = builder.build();
        cronetEngine.createURLStreamHandlerFactory();
    }

    public URLConnection openConnection(URL url) {
        if (cronetEngine != null) {
            try {
                return cronetEngine.openConnection(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

//    public void requestUrl(String url) {
//        LogUtils.d(TAG, "requestUrl start");
//        getHtml(url, callback);
//    }
//
//    private void getHtml(String url, UrlRequest.Callback callback) {
//        startWithURL(url, callback);
////        try {
////            HttpURLConnection connection = (HttpURLConnection)cronetEngine.openConnection(new URL(url));
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    private void startWithURL(String url, UrlRequest.Callback callback) {
//        startWithURL(url, callback, null);
//    }
//
//    private void startWithURL(String url, UrlRequest.Callback callback, String postData) {
//        UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(url, callback, executor);
//        applyPostDataToUrlRequestBuilder(builder, executor, postData);
//        builder.build().start();
//    }
//
//    private void applyPostDataToUrlRequestBuilder(UrlRequest.Builder builder, Executor executor, String postData) {
//        if (postData != null && postData.length() > 0) {
//            builder.setHttpMethod("POST");
//            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
//            builder.setUploadDataProvider(UploadDataProviders.create(postData.getBytes()), executor);
//        }
//    }
//
//    public void downloadCallback(String url, ProgressCallback callback) {
//        UrlRequest.Callback downloadCallback = new DownloadRequestCallback(callback);
//        UrlRequest.Builder builder = cronetEngine.newUrlRequestBuilder(url, downloadCallback, executor);
//        builder.disableCache().allowDirectExecutor();
//        builder.build().start();
//    }
//
//    @Override
//    public void downloadTest(String url, ProgressCallback callback) {
//        if (url == null || url.trim().length() <= 0) {
//            return;
//        }
//
//        InputStream inputStream = null;
//        try {
//            URL httpUrl = new URL(url);
//            URLConnection httpConnection = cronetEngine.openConnection(httpUrl);
////            httpConnection.setConnectTimeout(100000);
////            httpConnection.setReadTimeout(100000);
//            httpConnection.setUseCaches(false);
//
//            long startTime = SystemClock.elapsedRealtime();
//            httpConnection.connect();
//            long durationTime = SystemClock.elapsedRealtime() - startTime;
//            //LogUtils.d(TAG, "durationTime: " + durationTime);
//            long lengthLong = httpConnection.getContentLength();
//            //LogUtils.d(TAG, "length: " + lengthLong);
//            inputStream = httpConnection.getInputStream();
//            int rc = 0;
//            long currentBytes = 0;
//            DecimalFormat df = new DecimalFormat("#####0.00");
//            byte[] buff = new byte[102400];
//            long startReadTime = SystemClock.elapsedRealtime();
//            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//            while ((rc = inputStream.read(buff, 0, 102400)) > 0) {
//                swapStream.write(buff, 0, rc);
//                currentBytes += rc;
//                double result = (double)currentBytes * 100 / (double)lengthLong;
//                //LogUtils.d(TAG, "downloading---" + df.format(result) + "%");
//                if (callback != null) {
//                    callback.progress((int)result);
//                }
//            }
//
//            byte[] in2b = swapStream.toByteArray();
//            swapStream.close();
//            inputStream.close();
//            long costTime = (SystemClock.elapsedRealtime() - startReadTime);
//            long speed = (in2b.length >> 10) * 1000 / costTime;
//            if (callback != null) {
//                callback.complete(Config.formatUnion(in2b.length), Config.formatTime((int)costTime), Config.formatString("%sKB/S", speed));
//            }
//
//            //String result = "length: " + Config.formatUnion(in2b.length) + " time: " + Config.formatTime((int)costTime) + " speed: " + speed + " KB/S";
//            //LogUtils.d(TAG, result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
