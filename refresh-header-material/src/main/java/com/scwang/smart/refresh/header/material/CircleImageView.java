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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.ImageView;


/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
@SuppressLint({"ViewConstructor", "AppCompatCustomView"})
public class CircleImageView extends ImageView {

    protected static final int KEY_SHADOW_COLOR = 0x1E000000;
    protected static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    protected static final float X_OFFSET = 0f;
    protected static final float Y_OFFSET = 1.75f;
    protected static final float SHADOW_RADIUS = 3.5f;
    protected static final int SHADOW_ELEVATION = 4;

    //    private Animation.AnimationListener mListener;
    int mShadowRadius;

    public CircleImageView(Context context, int color) {
        super(context);
        final View thisView = this;
        final float density = thisView.getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            circle = new ShapeDrawable(new OvalShape());
            thisView.setElevation(SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius);
            circle = new ShapeDrawable(oval);
            thisView.setLayerType(LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            thisView.setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(color);
        thisView.setBackground(circle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Build.VERSION.SDK_INT < 21) {
            super.setMeasuredDimension(
                    thisView.getMeasuredWidth() + mShadowRadius * 2,
                    thisView.getMeasuredHeight() + mShadowRadius * 2);
        }
    }

//    public void setAnimationListener(Animation.AnimationListener listener) {
//        mListener = listener;
//    }
//
//    @Override
//    public void onAnimationStart() {
//        super.onAnimationStart();
//        if (mListener != null) {
//            mListener.onAnimationStart(getAnimation());
//        }
//    }
//
//    @Override
//    public void onAnimationEnd() {
//        super.onAnimationEnd();
//        if (mListener != null) {
//            mListener.onAnimationEnd(getAnimation());
//        }
//    }

//    /**
//     * Update the background color of the circle image view.
//     *
//     * @param colorRes Id of a color resource.
//     */
//    public void setBackgroundColorRes(int colorRes) {
//        Context context = getContext();
//        if (Build.VERSION.SDK_INT >= 23) {
//            setBackgroundColor(context.getResources().getColor(colorRes, context.getTheme()));
//        } else {
//            //noinspection deprecation
//            setBackgroundColor(context.getResources().getColor(colorRes));
//        }
//    }
//
    @Override
    public void setBackgroundColor(@ColorInt int color) {
        final View thisView = this;
        if (thisView.getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) thisView.getBackground()).getPaint().setColor(color);
        }
    }

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;

        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) super.rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final View thisView = CircleImageView.this;
            final int viewWidth = thisView.getWidth();
            final int viewHeight = thisView.getHeight();
            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, viewWidth / 2f, mShadowPaint);
            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, viewWidth / 2f - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2f, diameter / 2f,
                    mShadowRadius, new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT },
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }
}
