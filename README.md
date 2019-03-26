# chromium-cronet
The demo of cornet library compiled by myself.
# chromium-cronet库的编译用于Android和ios平台实现quic协议

> [chromium-cronet文档](https://chromium.googlesource.com/chromium/src/+/master/components/cronet/build_instructions.md) 原文文档写的已经很清楚，最好还是参考官方文档，避免由于版本原因导致的问题。

环境配置
--------
1. chromium源码环境；
2. 已经配置好相关环境，安装好相关依赖；
3. 这里是在linux环境下对Android库的编译，在macos环境下会直接编译为ios平台库；

编译开发和debug环境的Cronet库
--------
### Android / IOS build
```sh
$ ./components/cronet/tools/cr_cronet.py gn --out_dir=out/Cronet
```
如果build主机是在linux环境下，build的是android的库。如果build主机是macOS，build的是ios库。

```sh
$ ninja -C out/Cronet cronet_package
```
编译Cronet库，最终文件可以在out/Cronet/cornet中寻找。

使用cronet库
--------
1. 创建CronetEngine，最好整个应用使用一个CronetEngine，这里可以理解为OkHttpClient；
2. 创建自己的线程池给Cronet使用，Cronet的网络请求都会在线程池中，避免主线程阻塞；
3. 实现回调UrlRequest.Callback，在UrlRequest调用start以后，网络请求开始，之后产生请求回调；

```java
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
        //LogUtils.i(TAG, "text: " + text);
        //LogUtils.i(TAG, "receivedData: " + receivedData);
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
```
* onRedirectReceived 顾名思义，是重定向的回调，这里直接选择继续访问重定向的地址，也可以调用UrlRequest的cancel方法，取消访问；
* onResponseStarted 从google文档上面来看是请求完header以后开始请求body部分会回调这里，每次请求只会回调一次；
* onReadCompleted 这是读取body一定数据时会回调这个方法，这里request.read读取的数据不一定会填满缓冲区，请求生命周期中会有多次回调发生；
* onSucceeded 最终请求成功回调，可以作为数据处理阶段；
* onFailed 请求失败回调，例如网络不通，或者没有网络访问权限之类的错误；
* onCanceled 请求取消回调，只会在cancel后才会回调，回调这个意味着整个请求完成；

> [https://ssl.gstatic.com/gb/images/qi2_00ed8ca1.png](https://ssl.gstatic.com/gb/images/qi2_00ed8ca1.png) 实现quic访问，可作为测试地址。