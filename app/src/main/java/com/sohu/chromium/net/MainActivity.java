package com.sohu.chromium.net;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sohu.chromium.net.cornet.CronetHelper;
import com.sohu.chromium.net.cornet.ProgressCallback;
import com.sohu.chromium.net.utils.Config;

public class MainActivity extends AppCompatActivity {

    private Button request;
    private Button download;

    private LinearLayout quicContainer;
    private LinearLayout requestContainer;

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
        requestContainer = findViewById(R.id.requestContainer);
        quicContainer = findViewById(R.id.quicContainer);

        request.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.request:
//                    CronetHelper.getInstance().requestUrl("http://pv.sohu.com/cityjson");
                      //Quic Request
//                    CronetHelper.getInstance().requestUrl("https://ssl.gstatic.com/gb/images/qi2_00ed8ca1.png");
                    downloadTest(requestContainer, Config.httpTestUrls);
                    break;
                case R.id.download:
                    downloadTest(quicContainer, Config.quicTestUrls);
                    break;
            }
        }
    };

    private void downloadTest(LinearLayout view, final String[] urls) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int color = Color.RED;
                for (String url : urls) {
                    RelativeLayout linearLayout = (RelativeLayout) inflater.inflate(R.layout.progress_view, null);
                    ProgressCallback progressCallback = new ProgressCallback(linearLayout, handler, color);
                    color += 0x1999;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.addView(linearLayout);
                        }
                    });

                    cronetHelper.downloadFile(url, progressCallback);
                }
            }
        }).start();
    }
}