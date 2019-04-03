package com.sohu.chromium.net.cronet;

import android.os.SystemClock;

import com.sohu.chromium.net.Config;
import com.sohu.chromium.net.LogUtils;
import com.sohu.chromium.net.callback.ProgressCallback;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyan on 2019/3/28
 */
public class DownloadRequestCallback extends UrlRequest.Callback {
    private static final String TAG = "DownloadRequestCallback";

    private long startTime = 0;
    private long totalLength = 0;
    private ProgressCallback callback;
    //private ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(102400);
//    private ByteArrayOutputStream mBytesReceived = new ByteArrayOutputStream();
//    private WritableByteChannel mReceiveChannel = Channels.newChannel(mBytesReceived);

    public DownloadRequestCallback(ProgressCallback callback) {
        startTime = SystemClock.elapsedRealtime();
        this.callback = callback;
    }

    @Override
    public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) throws Exception {
        //LogUtils.d(TAG, "download onRedirectReceived: " + s);
        urlRequest.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) throws Exception {
        //LogUtils.d(TAG, "onResponseStarted connect cost time: " + (SystemClock.elapsedRealtime() - startTime));
        String contentLengthStr = "";
        List<Map.Entry<String, String>> headers = urlResponseInfo.getAllHeadersAsList();
        for (Map.Entry<String, String> header : headers) {
            if (header.getKey().toLowerCase().equals("content-length")) {
                contentLengthStr = header.getValue();
            }
        }

        totalLength = Long.parseLong(contentLengthStr);
        urlRequest.read(ByteBuffer.allocateDirect(102400));
    }

    @Override
    public void onReadCompleted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) throws Exception {
//        try {
//            byteBuffer.flip();
//            mReceiveChannel.write(byteBuffer);
//            byteBuffer.clear();
//        } catch (IOException e) {
//            LogUtils.e(TAG, e);
//        }
        //mByteBuffer.clear();
        double progress = ((double)urlResponseInfo.getReceivedByteCount() * 100) / totalLength;
        callback.progress((int)progress);

        urlRequest.read(ByteBuffer.allocateDirect(102400));
    }

    @Override
    public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
        long costTime = SystemClock.elapsedRealtime() - startTime;
        //byte[] bytes = mBytesReceived.toByteArray();
        long length = urlResponseInfo.getReceivedByteCount();
        long speed = ((length * 1000) >> 10) / (costTime);
        //LogUtils.d(TAG, "onSucceeded length=" + length + "byte costTime=" + costTime + "ms speed: " + speed + "kb/s");

        callback.complete(Config.formatUnion(urlResponseInfo.getReceivedByteCount()), Config.formatTime((int)costTime), speed + "KB/S");
    }

    @Override
    public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {
        LogUtils.e(TAG, "onFailed", e);
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        long costTime = SystemClock.elapsedRealtime() - startTime;
//        byte[] bytes = mBytesReceived.toByteArray();
        long length = info.getReceivedByteCount();
        long speed = ((length * 1000) >> 10) / (costTime);
        LogUtils.d(TAG, "onCanceled length=" + length + "byte costTime=" + costTime + "ms speed: " + speed + "kb/s");
    }
}
