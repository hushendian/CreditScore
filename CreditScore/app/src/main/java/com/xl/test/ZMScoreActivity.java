package com.xl.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ZMScoreActivity extends AppCompatActivity {

    private ZMScore zmScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zmscore_layout);
        zmScore = findViewById(R.id.zm);
        zmScore.setMonthText(new String[]{"6月", "7月", "8月", "9月", "10月", "11月"});
        zmScore.setScore(new int[]{681, 698, 669, 686, 675, 689});
    }
}
