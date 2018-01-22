package com.xl.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by hushendian on 2018/1/11.
 */

public class FlikerProgressbarActivity extends AppCompatActivity implements View.OnClickListener,
        Runnable {

    private static final int LOAD_SUCESS = 1;
    private FlikerProgressBar flikerProgressBar;
    private Thread downLoadThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flikerprogressbar_layout);
        flikerProgressBar = findViewById(R.id.progress_bar);
        flikerProgressBar.setOnClickListener(this);
        downLoad();
    }

    @Override
    public void onClick(View v) {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOAD_SUCESS) {
                flikerProgressBar.setProgress(msg.arg1);
                if (msg.arg1 == 100) {
                    flikerProgressBar.setStop(true);
                }
            }


        }
    };

    private void downLoad() {
        downLoadThread = new Thread(this);
        downLoadThread.start();
    }

    @Override
    public void run() {


        while (!downLoadThread.isInterrupted()) {
            Log.d("FlikerProgressBar", "run: ");
            float progress = flikerProgressBar.getProgress();
            progress += 2;
            Message message = Message.obtain();
            message.arg1 = (int) progress;
            message.what = LOAD_SUCESS;
            handler.sendMessageDelayed(message, 200);
            if (progress == 100) {
                break;
            }
        }
    }
}
