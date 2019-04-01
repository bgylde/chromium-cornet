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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sohu.chromium.net.cornet.CronetHelper;
import com.sohu.chromium.net.cornet.OkhttpHelper;
import com.sohu.chromium.net.cornet.ProgressCallback;
import com.sohu.chromium.net.cornet.SystemNetHelper;
import com.sohu.chromium.net.utils.Config;
import com.sohu.chromium.net.utils.threadPool.ThreadPoolManager;

public class MainActivity extends Activity {

    private Button startTest;
    private RadioGroup protocolGroup;
    private RadioGroup requestStyleGroup;
    private LinearLayout testContaioner;

    private int prototolIndex = R.id.http;
    private int requestIndex = R.id.urlconnection;
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
        startTest = (Button) findViewById(R.id.startTest);
        protocolGroup = (RadioGroup) findViewById(R.id.protocolGroup);
        requestStyleGroup = (RadioGroup) findViewById(R.id.requestStyleGroup);
        testContaioner = (LinearLayout) findViewById(R.id.testContaioner);

        protocolGroup.setOnCheckedChangeListener(protocolGroupListener);
        requestStyleGroup.setOnCheckedChangeListener(requestGroupListener);
    }

//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()) {
//                case R.id.request:
//                    cronetTest(requestContainer, Config.httpTestUrls);
//                    break;
//                case R.id.download:
//                    cronetTest(quicContainer, Config.quicTestUrls);
//                    break;
//                case R.id.http2_download:
//                    cronetTest(http2Container, Config.http2TestUrls);
//                    break;
//            }
//        }
//    };

    private RadioGroup.OnCheckedChangeListener protocolGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            prototolIndex = checkedId;
        }
    };

    private RadioGroup.OnCheckedChangeListener requestGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            requestIndex = checkedId;
        }
    };

    public void startTest(View view) {
        switch (view.getId()) {
            case R.id.startTest: {
                String[] urls = Config.httpTestUrls;
                switch (prototolIndex) {
                    case R.id.http:
                        urls = Config.httpTestUrls;
                        break;
                    case R.id.http2:
                        urls = Config.http2TestUrls;
                        break;
                    case R.id.quic:
                        urls = Config.quicTestUrls;
                        break;
                }

                switch (requestIndex) {
                    case R.id.urlconnection:
                        systemTest(testContaioner, urls);
                        break;
                    case R.id.okhttp:
                        okhttpTest(testContaioner, urls);
                        break;
                    case R.id.quicUrlConnection:
                        cronetTest(testContaioner, urls);
                        break;
                }

                break;
            }

            case R.id.clearResult:
                testContaioner.removeAllViews();
                break;
        }
    }

//    private void startVideoActivity() {
//        Intent intent = new Intent();
//        intent.setClass(this, VideoActivity.class);
//        startActivity(intent);
//    }

    private void cronetTest(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("Cronet download style: ");
        view.addView(textView);
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addCronetTask(new Runnable() {
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
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("System download style: ");
        view.addView(textView);
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addSystemTask(new Runnable() {
                @Override
                public void run() {
                    SystemNetHelper.getInstance().downloadTest(url, progressCallback);
                }
            });
            color += 0x1999;
        }

        View lineView = new View(this);
        lineView.setBackgroundColor(0xFFFFFFFF);
        lineView.setMinimumHeight(5);
        view.addView(lineView);
    }

    private void okhttpTest(LinearLayout view, final String[] urls) {
        int color = Color.RED;
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("Okhttp download style: ");
        view.addView(textView);
        for (String url : urls) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
            view.addView(linearLayout);
            ThreadPoolManager.getInstance().addOkhttpTask(new Runnable() {
                @Override
                public void run() {
                    OkhttpHelper.getHelper().downloadTest(url, progressCallback);
                }
            });
            color += 0x1999;
        }

        View lineView = new View(this);
        lineView.setBackgroundColor(0xFFFFFFFF);
        lineView.setMinimumHeight(5);
        view.addView(lineView);
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

        View lineView = new View(this);
        lineView.setBackgroundColor(0xFFFFFFFF);
        lineView.setMinimumHeight(5);
        view.addView(lineView);
    }
}