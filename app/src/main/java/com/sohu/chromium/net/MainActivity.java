package com.sohu.chromium.net;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sohu.chromium.net.cornet.CronetHelper;

public class MainActivity extends AppCompatActivity {

    private Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CronetHelper.getInstance().init(this);
        initView();
    }

    private void initView() {
        request = findViewById(R.id.request);
        request.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.request:
//                    CronetHelper.getInstance().requestUrl("http://pv.sohu.com/cityjson");
                      //Quic Request
                    CronetHelper.getInstance().requestUrl("https://ssl.gstatic.com/gb/images/qi2_00ed8ca1.png");
                    break;
            }
        }
    };
}