package com.sohu.chromium.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sohu.chromium.net.cornet.CronetHelper;
import com.sohu.chromium.net.cornet.ProgressCallback;
import com.sohu.chromium.net.cornet.SystemNetHelper;
import com.sohu.chromium.net.utils.Config;
import com.sohu.chromium.net.utils.threadPool.ThreadPoolManager;

public class MainActivity extends Activity {

    private Button request;
    private Button download;
    private Button http2Download;

    private LinearLayout quicContainer;
    private LinearLayout requestContainer;
    private LinearLayout http2Container;

    private Handler handler;
    private CronetHelper cronetHelper;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        handler = new Handler(Looper.getMainLooper());
        cronetHelper = CronetHelper.getInstance();
        cronetHelper.init(this);

        initView();
    }

    private void initView() {
        request = findViewById(R.id.request);
        download = findViewById(R.id.download);
        http2Download = findViewById(R.id.http2_download);
        requestContainer = findViewById(R.id.requestContainer);
        quicContainer = findViewById(R.id.quicContainer);
        http2Container = findViewById(R.id.http2Container);

        request.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
        http2Download.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.request:
                    cronetTest(requestContainer, Config.httpTestUrls);
                    break;
                case R.id.download:
                    cronetTest(quicContainer, Config.quicTestUrls);
                    break;
                case R.id.http2_download:
                    cronetTest(http2Container, Config.http2TestUrls);
                    break;
            }
        }
    };

//    private void startVideoActivity() {
//        Intent intent = new Intent();
//        intent.setClass(this, VideoActivity.class);
//        startActivity(intent);
//    }

    private void cronetTest(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addLogSenderTask(new Runnable() {
                @Override
                public void run() {
                    cronetHelper.downloadTest(url, progressCallback);
                }
            });
            color += 0x1999;
        }
    }

    private void systemTest(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addLogSenderTask(new Runnable() {
                @Override
                public void run() {
                    SystemNetHelper.getInstance().downloadTest(url, progressCallback);
                }
            });
            color += 0x1999;
        }
    }

    private void okhttpTest(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addLogSenderTask(new Runnable() {
                @Override
                public void run() {
                    cronetHelper.downloadTest(url, progressCallback);
                }
            });
            color += 0x1999;
        }
    }

    //这种方式就不走quic协议了，令人费解
    private void downloadCronet(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            cronetHelper.downloadCallback(url, progressCallback);
            view.addView(linearLayout);
            color += 0x1999;
        }
    }
}