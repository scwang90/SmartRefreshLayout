package com.app_design;

import android.os.Bundle;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.appcompat.app.AppCompatActivity;

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
