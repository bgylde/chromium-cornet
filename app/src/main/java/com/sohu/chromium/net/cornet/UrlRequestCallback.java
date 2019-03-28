package com.sohu.chromium.net.cornet;

import com.sohu.chromium.net.utils.LogUtils;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by wangyan on 2019/3/26
 */
public class UrlRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "UrlRequestCallback";

    private long startTime;
    private ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(102400);
    private ByteArrayOutputStream mBytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel mReceiveChannel = Channels.newChannel(mBytesReceived);

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        LogUtils.i(TAG, "onRedirectReceived method called.");
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        LogUtils.i(TAG, "onResponseStarted method called.");
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes to the read() method.
        //ByteBuffer byteBuffer = ByteBuffer.allocateDirect(102400);
        startTime = System.currentTimeMillis();
        request.read(mByteBuffer);
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
        LogUtils.i(TAG, "onReadCompleted method called.");
        // You should keep reading the request until there's no more data.

        try {
            byteBuffer.flip();
            mReceiveChannel.write(byteBuffer);
            byteBuffer.clear();
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        }

        request.read(mByteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        LogUtils.i(TAG, "onSucceeded method called: " + info.toString());
        LogUtils.d(TAG, "cost time: " + (System.currentTimeMillis() - startTime) + " ms");
        //byte[] bytes = mBytesReceived.toByteArray();
        String receivedData = null;
        try {
            receivedData = mBytesReceived.toString("GBK");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(TAG, e);
        }

        final String url = info.getUrl();
        final String text = "Completed " + url + " (" + info.getHttpStatusCode() + ")";
        LogUtils.i(TAG, "text: " + text);
        LogUtils.i(TAG, "receivedData: " + receivedData);
    }

    @Override
    public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {
        //LogUtils.d(TAG, "url: " + urlResponseInfo.getUrl());
        LogUtils.e(TAG, "onFailed method called.", e);
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        LogUtils.d(TAG, "onCanceled method called.");
    }
}
