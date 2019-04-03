package com.sohu.chromium.net.callback;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sohu.chromium.net.R;

/**
 * Created by wangyan on 2019/3/29
 */
public class ProgressCallback {
    private static final String TAG = "ProgressCallback";
    private TextView indexView = null;
    private TextView lengthView = null;
    private TextView timeView = null;
    private TextView speedView = null;
    private ProgressBar progressBar = null;

    private Handler handler = null;
    private int index;

    public ProgressCallback(View rootView, Handler handler, int color, int index) {
        this.progressBar = rootView.findViewById(R.id.quic_download_bar);
        this.indexView = rootView.findViewById(R.id.index);
        this.lengthView = rootView.findViewById(R.id.length);
        this.timeView = rootView.findViewById(R.id.time);
        this.speedView = rootView.findViewById(R.id.speed);
        this.handler = handler;
        this.index = index;

        this.indexView.setTextColor(color);
        this.lengthView.setTextColor(color);
        this.timeView.setTextColor(color);
        this.speedView.setTextColor(color);
    }

    public void progress(final int progress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                String result = progress + "%";
                indexView.setText(result);
            }
        });
    }

    public void complete(final String length, final String time, final String speed) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                indexView.setVisibility(View.VISIBLE);
                lengthView.setVisibility(View.VISIBLE);
                timeView.setVisibility(View.VISIBLE);
                speedView.setVisibility(View.VISIBLE);

                indexView.setText(String.valueOf(index));
                lengthView.setText(length);
                timeView.setText(time);
                speedView.setText(speed);
            }
        });
    }
}
