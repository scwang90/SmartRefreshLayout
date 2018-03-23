package com.app_design;

import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ColorUtils.setAlphaComponent(0, 0);
        ColorUtils.compositeColors(0, 0);
        new FastOutSlowInInterpolator();
        PathInterpolatorCompat.create(0, 0);

    }
}
