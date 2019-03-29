package com.sohu.chromium.net.cornet;

import android.os.SystemClock;

import com.sohu.chromium.net.utils.LogUtils;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Created by wangyan on 2019/3/28
 */
public class DownloadRequestCallback extends UrlRequest.Callback {
    private static final String TAG = "DownloadRequestCallback";

    private long startTime = 0;
    private ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(102400);
    private ByteArrayOutputStream mBytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel mReceiveChannel = Channels.newChannel(mBytesReceived);

    public DownloadRequestCallback() {
        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) throws Exception {
        LogUtils.d(TAG, "download onRedirectReceived: " + s);
        urlRequest.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) throws Exception {
        LogUtils.d(TAG, "onResponseStarted connect cost time: " + (SystemClock.elapsedRealtime() - startTime));
        urlRequest.read(mByteBuffer);
    }

    @Override
    public void onReadCompleted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) throws Exception {
        try {
            byteBuffer.flip();
            mReceiveChannel.write(byteBuffer);
            byteBuffer.clear();
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        }

        urlRequest.read(mByteBuffer);

        if (SystemClock.elapsedRealtime() - startTime > 10000) {
            urlRequest.cancel();
        }
    }

    @Override
    public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
        long costTime = SystemClock.elapsedRealtime() - startTime;
        byte[] bytes = mBytesReceived.toByteArray();
        long length = bytes.length;
        long speed = ((length * 1000) >> 10) / (costTime);
        LogUtils.d(TAG, "onSucceeded length=" + length + "byte costTime=" + costTime + "ms speed: " + speed + "kb/s");
    }

    @Override
    public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {
        LogUtils.e(TAG, "onFailed", e);
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        long costTime = SystemClock.elapsedRealtime() - startTime;
        byte[] bytes = mBytesReceived.toByteArray();
        long length = bytes.length;
        long speed = ((length * 1000) >> 10) / (costTime);
        LogUtils.d(TAG, "onCanceled length=" + length + "byte costTime=" + costTime + "ms speed: " + speed + "kb/s");
    }
}
