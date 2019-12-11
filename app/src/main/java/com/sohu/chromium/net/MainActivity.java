package com.sohu.chromium.net;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sohu.chromium.net.callback.ProgressCallback;
import com.sohu.chromium.net.cronet.CronetHelper;
import com.sohu.chromium.net.okhttp.OkhttpHelper;
import com.sohu.chromium.net.system.SystemNetHelper;
import com.sohu.chromium.net.threadPool.ThreadPoolManager;

public class MainActivity extends AppCompatActivity {

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
                        //urls = Config.httpTestUrls;
                        urls = Config.getUrls(false, 80, 100);
                        break;
                    case R.id.http2:
                        //urls = Config.http2TestUrls;
                        urls = Config.getUrls(true, 8080, 100);
                        break;
                    case R.id.quic:
                        //urls = Config.quicTestUrls;
                        urls = Config.getUrls(true, 443, 100);
                        break;
                }

                LogUtils.d("MainActivity", "url length: " + urls.length);
                LogUtils.d("MainActivity", "url: " + urls[0]);
                switch (requestIndex) {
                    case R.id.urlconnection:
                        systemTest(testContaioner, urls, true);
                        break;
                    case R.id.okhttp:
                        okhttpTest(testContaioner, urls, true);
                        break;
                    case R.id.quicUrlConnection:
                        cronetTest(testContaioner, urls, true);
                        break;
                }

                break;
            }

            case R.id.clearResult:
                testContaioner.removeAllViews();
                break;
        }
    }
    private void cronetTest(LinearLayout view, final String[] urls, boolean useThreadPool) {
        int color = Color.RED;
        int colorInterval = 0xA;
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("Cronet download style: ");
        view.addView(textView);
        for (int i = 0; i < urls.length; i++) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            final ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color, i);
            view.addView(linearLayout);
            if (!useThreadPool) {
                cronetHelper.downloadCallback(urls[i], progressCallback);
            } else {
                final int finalI = i;
                ThreadPoolManager.getInstance().addCronetTask(new Runnable() {
                    @Override
                    public void run() {
                        cronetHelper.downloadTest(urls[finalI], progressCallback);
                    }
                });
            }
            color += colorInterval;
        }
    }

    private void systemTest(LinearLayout view, final String[] urls, boolean useThreadPool) {
        int color = Color.RED;
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("System download style: ");
        view.addView(textView);
        for (int i = 0; i < urls.length; i++) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            final ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color, i);
            view.addView(linearLayout);
            final int finalI = i;
            ThreadPoolManager.getInstance().addSystemTask(new Runnable() {
                @Override
                public void run() {
                    SystemNetHelper.getInstance().downloadTest(urls[finalI], progressCallback);
                }
            });
            color += 0xA;
        }

        View lineView = new View(this);
        lineView.setBackgroundColor(0xFFFFFFFF);
        lineView.setMinimumHeight(5);
        view.addView(lineView);
    }

    private void okhttpTest(LinearLayout view, final String[] urls, boolean useThreadPool) {
        int color = Color.RED;
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFFFFFFF);
        textView.setText("Okhttp download style: ");
        view.addView(textView);
        for (int i = 0; i < urls.length; i++) {
            RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
            final ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color, i);
            view.addView(linearLayout);
            if (!useThreadPool) {
                OkhttpHelper.getHelper().downloadTestAsyn(urls[i], progressCallback);
            } else {
                final int finalI = i;
                ThreadPoolManager.getInstance().addOkhttpTask(new Runnable() {
                    @Override
                    public void run() {
                        OkhttpHelper.getHelper().downloadTest(urls[finalI], progressCallback);
                    }
                });
            }
            color += 0xA;
        }

        View lineView = new View(this);
        lineView.setBackgroundColor(0xFFFFFFFF);
        lineView.setMinimumHeight(5);
        view.addView(lineView);
    }
}
