package com.zhujiang.circlecountdownwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    CircleCountdownView circleCountdownView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleCountdownView = (CircleCountdownView) findViewById(R.id.circle_countdown_view);
        circleCountdownView.setOnClickListener((View) -> circleCountdownView.init(10,5,10));
    }

    @Override
    protected void onResume() {
        super.onResume();
        circleCountdownView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        circleCountdownView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        circleCountdownView.onDestroy();
    }
}
