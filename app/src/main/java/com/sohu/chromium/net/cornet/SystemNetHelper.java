package com.sohu.chromium.net.cornet;

import android.os.SystemClock;

import com.sohu.chromium.net.utils.Config;
import com.sohu.chromium.net.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * Created by wangyan on 2019/3/30
 */
public class SystemNetHelper implements IDownloadTest{

    private static final String TAG = "SystemNetHelper";

    private static SystemNetHelper helper = null;

    private SystemNetHelper() {}

    public static SystemNetHelper getInstance() {
        if (helper == null) {
            synchronized (SystemNetHelper.class) {
                if (helper == null) {
                    helper = new SystemNetHelper();
                }
            }
        }

        return helper;
    }

    @Override
    public void downloadTest(String url, ProgressCallback callback) {
        if (url == null || url.trim().length() <= 0) {
            return;
        }

        InputStream inputStream = null;
        try {
            URL httpUrl = new URL(url);
            URLConnection httpConnection = httpUrl.openConnection();
//            httpConnection.setConnectTimeout(100000);
//            httpConnection.setReadTimeout(100000);
            httpConnection.setUseCaches(false);

            long startTime = SystemClock.elapsedRealtime();
            httpConnection.connect();
            long durationTime = SystemClock.elapsedRealtime() - startTime;
            LogUtils.d(TAG, "durationTime: " + durationTime);
            long lengthLong = httpConnection.getContentLength();
            LogUtils.d(TAG, "length: " + lengthLong);
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

            String result = "length: " + Config.formatUnion(in2b.length) + " time: " + Config.formatTime((int)costTime) + " speed: " + speed + " KB/S";
            LogUtils.d(TAG, result);
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        }
    }
}
