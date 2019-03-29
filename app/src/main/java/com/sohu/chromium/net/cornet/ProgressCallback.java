package com.sohu.chromium.net.cornet;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sohu.chromium.net.R;
import com.sohu.chromium.net.utils.LogUtils;


/**
 * Created by wangyan on 2019/3/29
 */
public class ProgressCallback {
    private static final String TAG = "ProgressCallback";
    private TextView lengthView = null;
    private TextView timeView = null;
    private TextView speedView = null;
    private ProgressBar progressBar = null;

    private Handler handler = null;

    public ProgressCallback(View rootView, Handler handler, int color) {
        this.progressBar = rootView.findViewById(R.id.quic_download_bar);
        this.lengthView = rootView.findViewById(R.id.length);
        this.timeView = rootView.findViewById(R.id.time);
        this.speedView = rootView.findViewById(R.id.speed);
        this.handler = handler;

        LogUtils.d(TAG, "color: " + color);
        this.lengthView.setTextColor(color);
        this.timeView.setTextColor(color);
        this.speedView.setTextColor(color);
    }

    public void progress(int progress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                String result = progress + "%";
                lengthView.setText(result);
            }
        });
    }

    public void complete(String length, String time, String speed) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                lengthView.setVisibility(View.VISIBLE);
                timeView.setVisibility(View.VISIBLE);
                speedView.setVisibility(View.VISIBLE);

                lengthView.setText(length);
                timeView.setText(time);
                speedView.setText(speed);
            }
        });
    }
}
