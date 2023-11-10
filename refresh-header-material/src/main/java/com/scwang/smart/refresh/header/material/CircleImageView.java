/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scwang.smart.refresh.header.material;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;


/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
@SuppressLint({"ViewConstructor", "AppCompatCustomView"})
public class CircleImageView extends ImageView {

    protected static final float SHADOW_RADIUS = 3.5f;
    protected static final int SHADOW_ELEVATION = 4;

    int mShadowRadius;

    public CircleImageView(Context context, int color) {
        super(context);
        final View thisView = this;
        final float density = thisView.getResources().getDisplayMetrics().density;

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        circle = new ShapeDrawable(new OvalShape());
        thisView.setElevation(SHADOW_ELEVATION * density);
        circle.getPaint().setColor(color);
        thisView.setBackground(circle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        final View thisView = this;
        if (thisView.getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) thisView.getBackground()).getPaint().setColor(color);
        }
    }

}
