/*
 * Copyright (C) 2015 RECRUIT LIFESTYLE CO., LTD.
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
package com.scwang.smart.refresh.header.waveswipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.ColorInt;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;

/**
 * @author amyu
 *         <p>
 *         波と落ちる円を描画するView
 */
public class WaveView extends View implements ViewTreeObserver.OnPreDrawListener {

    /**
     * {@link WaveView#mDropCircleAnimator} のDuration
     */
    protected static final long DROP_CIRCLE_ANIMATOR_DURATION = 500;

    /**
     * {@link WaveView#mDropBounceVerticalAnimator} のDuration
     */
    protected static final long DROP_VERTEX_ANIMATION_DURATION = 500;

    /**
     * {@link WaveView#mDropBounceVerticalAnimator} と {@link WaveView#mDropBounceHorizontalAnimator}
     * のDuration
     */
    protected static final long DROP_BOUNCE_ANIMATOR_DURATION = 500;

    /**
     * {@link WaveView#mDisappearCircleAnimator} のDuration
     */
    protected static final int DROP_REMOVE_ANIMATOR_DURATION = 200;

    /**
     * 波がくねくねしているDuration
     */
    protected static final int WAVE_ANIMATOR_DURATION = 1000;

    /**
     * 波の最大の高さ
     */
    protected static final float MAX_WAVE_HEIGHT = 0.2f;

    /**
     * 影の色
     */
    protected static final int SHADOW_COLOR = 0x99000000;

    /**
     * 円のRadius
     */
    protected float mDropCircleRadius = 100;

    /**
     * すべてを描画するPaint
     */
    protected Paint mPaint;

    /**
     * 画面の波を描画するためのPath
     */
    protected Path mWavePath;

    /**
     * 落ちる円の接線を描画するためのPath
     */
    protected Path mDropTangentPath;

    /**
     * 落ちる円を描画するためのPath
     */
    protected Path mDropCirclePath;

//    /**
//     * 影のPaint
//     */
//  protected Paint mShadowPaint;

    /**
     * 影のPath
     */
    protected Path mShadowPath;

    /**
     * 落ちる円の座標を入れているRectF
     */
    protected RectF mDropRect;

    /**
     * Viewの横幅
     */
    protected int mWidth;

    /**
     * {@link WaveView#mDropCircleAnimator} でアニメーションしてる時の円の中心のY座標
     */
    protected float mCurrentCircleCenterY;

    /**
     * 円が落ちる最大の高さ
     */
    protected int mMaxDropHeight;

    protected boolean mIsManualRefreshing = false;

    /**
     * 落ちる円の高さが更新されたかどうか
     */
    protected boolean mDropHeightUpdated = false;

    /**
     * {@link WaveView#mMaxDropHeight} を更新するための一時的な値の置き場
     */
    protected int mUpdateMaxDropHeight;

    /**
     * 落ちてくる円についてくる三角形の一番上の頂点のAnimator
     */
    protected ValueAnimator mDropVertexAnimator;

    /**
     * 落ちた円が横に伸びるときのAnimator
     */
    protected ValueAnimator mDropBounceVerticalAnimator;

    /**
     * 落ちた縁が縦に伸びるときのAnimator
     */
    protected ValueAnimator mDropBounceHorizontalAnimator;

    /**
     * 落ちる円の中心座標のAnimator
     */
    protected ValueAnimator mDropCircleAnimator;

    /**
     * 落ちた円を消すためのAnimator
     */
    protected ValueAnimator mDisappearCircleAnimator;

    /**
     * 帰ってくる波ののAnimator
     */
    protected ValueAnimator mWaveReverseAnimator;

    /**
     * ベジェ曲線を引く際の座標
     * 左側の2つのアンカーポイントでいい感じに右側にも
     */
    protected static final float[][] BEGIN_PHASE_POINTS = {
            //1
            {0.1655f, 0},           //ハンドル
            {0.4188f, -0.0109f},    //ハンドル
            {0.4606f, -0.0049f},    //アンカーポイント

            //2
            {0.4893f, 0.f},         //ハンドル
            {0.4893f, 0.f},         //ハンドル
            {0.5f, 0.f}             //アンカーポイント
    };

    protected static final float[][] APPEAR_PHASE_POINTS = {
            //1
            {0.1655f, 0.f},         //ハンドル
            {0.5237f, 0.0553f},     //ハンドル
            {0.4557f, 0.0936f},     //アンカーポイント

            //2
            {0.3908f, 0.1302f},     //ハンドル
            {0.4303f, 0.2173f},     //ハンドル
            {0.5f, 0.2173f}         //アンカーポイント
    };

    protected static final float[][] EXPAND_PHASE_POINTS = {
            //1
            {0.1655f, 0.f},         //ハンドル
            {0.5909f, 0.0000f},     //ハンドル
            {0.4557f, 0.1642f},     //アンカーポイント

            //2
            {0.3941f, 0.2061f},     //ハンドル
            {0.4303f, 0.2889f},     //ハンドル
            {0.5f, 0.2889f}         //アンカーポイント
    };

    /**
     * 各AnimatorのAnimatorUpdateListener
     */
    protected ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    final View thisView = WaveView.this;
                    thisView.postInvalidate();
                }
            };

    /**
     * Constructor
     * {@inheritDoc}
     */
    public WaveView(Context context) {
        super(context);
//        setUpPaint();
        final View thisView = this;
        float density = thisView.getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(0xff2196F3);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer((int) (0.5f + 2.0f * density), 0f, 0f, SHADOW_COLOR);

//        setUpPath();
        mWavePath = new Path();
        mDropTangentPath = new Path();
        mDropCirclePath = new Path();
        mShadowPath = new Path();

        resetAnimator();

        mDropRect = new RectF();
        thisView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        thisView.getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     * Viewのサイズが決まったら {@link WaveView#mWidth} に横幅
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mDropCircleRadius = w / 14.4f;
        final View thisView = this;
        updateMaxDropHeight((int) Math.min(Math.min(w, h), thisView.getHeight() - mDropCircleRadius));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 描画されてから {@link WaveView#mMaxDropHeight} を更新する
     * {@inheritDoc}
     */
    @Override
    public boolean onPreDraw() {
        final View thisView = this;
        thisView.getViewTreeObserver().removeOnPreDrawListener(this);
        if (mDropHeightUpdated) {
            updateMaxDropHeight(mUpdateMaxDropHeight);
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //引っ張ってる最中の波と終わったあとの波
//    canvas.drawPath(mWavePath, mShadowPaint);
        final View thisView = this;
        canvas.drawPath(mWavePath, mPaint);
        if (!thisView.isInEditMode()) {
            mWavePath.rewind();
            //円が落ちる部分の描画
            mDropTangentPath.rewind();
            mDropCirclePath.rewind();
        }
        float circleCenterY = (Float) mDropCircleAnimator.getAnimatedValue();
        float circleCenterX = mWidth / 2.f;
//        mDropRect.setEmpty();
        //円の座標をRectFに保存
        float scale = (Float) mDisappearCircleAnimator.getAnimatedValue();
        float vertical = (Float) mDropBounceVerticalAnimator.getAnimatedValue();
        float horizontal = (Float) mDropBounceHorizontalAnimator.getAnimatedValue();
        mDropRect.set(circleCenterX - mDropCircleRadius * (1 + vertical) * scale
                        + mDropCircleRadius * horizontal / 2,
                circleCenterY + mDropCircleRadius * (1 + horizontal) * scale
                        - mDropCircleRadius * vertical / 2,
                circleCenterX + mDropCircleRadius * (1 + vertical) * scale
                        - mDropCircleRadius * horizontal / 2,
                circleCenterY - mDropCircleRadius * (1 + horizontal) * scale
                        + mDropCircleRadius * vertical / 2);
        float vertex = (Float) mDropVertexAnimator.getAnimatedValue();
        mDropTangentPath.moveTo(circleCenterX, vertex);
        //円の接点(p1,q),(p2,q)
        double q =
                (Math.pow(mDropCircleRadius, 2) + circleCenterY * vertex - Math.pow(circleCenterY, 2)) / (
                        vertex - circleCenterY);
        //2次方程式解くための解の公式
        double b = -2.0 * mWidth / 2;
        double c =
                Math.pow(q - circleCenterY, 2) + Math.pow(circleCenterX, 2) - Math.pow(mDropCircleRadius,
                        2);
        double p1 = (-b + Math.sqrt(b * b - 4 * c)) / 2;
        double p2 = (-b - Math.sqrt(b * b - 4 * c)) / 2;
        mDropTangentPath.lineTo((float) p1, (float) q);
        mDropTangentPath.lineTo((float) p2, (float) q);
        mDropTangentPath.close();
        mShadowPath.set(mDropTangentPath);
        mShadowPath.addOval(mDropRect, Path.Direction.CCW);
        mDropCirclePath.addOval(mDropRect, Path.Direction.CCW);
//        if (mDropVertexAnimator.isRunning()) {
////      canvas.drawPath(mShadowPath, mShadowPaint);
//        } else {
////      canvas.drawPath(mDropCirclePath, mShadowPaint);
//        }
        canvas.drawPath(mDropTangentPath, mPaint);
        canvas.drawPath(mDropCirclePath, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDisappearCircleAnimator != null) {
            mDisappearCircleAnimator.end();
            mDisappearCircleAnimator.removeAllUpdateListeners();
        }
        if (mDropCircleAnimator != null) {
            mDropCircleAnimator.end();
            mDropCircleAnimator.removeAllUpdateListeners();
        }
        if (mDropVertexAnimator != null) {
            mDropVertexAnimator.end();
            mDropVertexAnimator.removeAllUpdateListeners();
        }
        if (mWaveReverseAnimator != null) {
            mWaveReverseAnimator.end();
            mWaveReverseAnimator.removeAllUpdateListeners();
        }
        if (mDropBounceHorizontalAnimator != null) {
            mDropBounceHorizontalAnimator.end();
            mDropBounceHorizontalAnimator.removeAllUpdateListeners();
        }
        if (mDropBounceVerticalAnimator != null) {
            mDropBounceVerticalAnimator.end();
            mDropBounceVerticalAnimator.removeAllUpdateListeners();
        }
        super.onDetachedFromWindow();
    }

//    protected void setUpPaint() {
//        float density = getResources().getDisplayMetrics().density;
//        mPaint = new Paint();
//        mPaint.setColor(0xff2196F3);
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setShadowLayer((int) (0.5f + 2.0f * density), 0f, 0f, SHADOW_COLOR);
//
////    float density = getResources().getDisplayMetrics().density;
////    mShadowPaint = new Paint();
////    mShadowPaint.setAntiAlias(true);
////    mShadowPaint.setShadowLayer((int) (0.5f + 2.0f * density), 0f, 0f, SHADOW_COLOR);
//    }
//
//    protected void setUpPath() {
//        mWavePath = new Path();
//        mDropTangentPath = new Path();
//        mDropCirclePath = new Path();
//        mShadowPath = new Path();
//    }

    protected void resetAnimator() {
        mDropVertexAnimator = ValueAnimator.ofFloat(0.f, 0.f);
        mDropBounceVerticalAnimator = ValueAnimator.ofFloat(0.f, 0.f);
        mDropBounceHorizontalAnimator = ValueAnimator.ofFloat(0.f, 0.f);
        mDropCircleAnimator = ValueAnimator.ofFloat(-1000.f, -1000.f);
        mDropCircleAnimator.start();
        mDisappearCircleAnimator = ValueAnimator.ofFloat(1.f, 1.f);
        mDisappearCircleAnimator.setDuration(1); // immediately finish animation cycle
        mDisappearCircleAnimator.start();
    }

    protected void onPreDragWave() {
        if (mWaveReverseAnimator != null) {
            if (mWaveReverseAnimator.isRunning()) {
                mWaveReverseAnimator.cancel();
            }
        }
    }

    public void manualRefresh() {
        if (mIsManualRefreshing) {
            return;
        }
        mIsManualRefreshing = true;
        mDropCircleAnimator = ValueAnimator.ofFloat(mMaxDropHeight, mMaxDropHeight);
        mDropCircleAnimator.start();
        mDropVertexAnimator = ValueAnimator.ofFloat(mMaxDropHeight - mDropCircleRadius,
                mMaxDropHeight - mDropCircleRadius);
        mDropVertexAnimator.start();
        mCurrentCircleCenterY = mMaxDropHeight;
        final View thisView = WaveView.this;
        thisView.postInvalidate();
    }

    public void beginPhase(float move1) {
        onPreDragWave();
        //円を描画し始める前の引っ張ったら膨れる波の部分の描画
        mWavePath.moveTo(0, 0);
        //左半分の描画
        mWavePath.cubicTo(mWidth * BEGIN_PHASE_POINTS[0][0], BEGIN_PHASE_POINTS[0][1],
                mWidth * BEGIN_PHASE_POINTS[1][0], mWidth * (BEGIN_PHASE_POINTS[1][1] + move1),
                mWidth * BEGIN_PHASE_POINTS[2][0], mWidth * (BEGIN_PHASE_POINTS[2][1] + move1));
        mWavePath.cubicTo(mWidth * BEGIN_PHASE_POINTS[3][0],
                mWidth * (BEGIN_PHASE_POINTS[3][1] + move1), mWidth * BEGIN_PHASE_POINTS[4][0],
                mWidth * (BEGIN_PHASE_POINTS[4][1] + move1), mWidth * BEGIN_PHASE_POINTS[5][0],
                mWidth * (BEGIN_PHASE_POINTS[5][1] + move1));
        //右半分の描画
        mWavePath.cubicTo(mWidth - mWidth * BEGIN_PHASE_POINTS[4][0],
                mWidth * (BEGIN_PHASE_POINTS[4][1] + move1), mWidth - mWidth * BEGIN_PHASE_POINTS[3][0],
                mWidth * (BEGIN_PHASE_POINTS[3][1] + move1), mWidth - mWidth * BEGIN_PHASE_POINTS[2][0],
                mWidth * (BEGIN_PHASE_POINTS[2][1] + move1));
        mWavePath.cubicTo(mWidth - mWidth * BEGIN_PHASE_POINTS[1][0],
                mWidth * (BEGIN_PHASE_POINTS[1][1] + move1), mWidth - mWidth * BEGIN_PHASE_POINTS[0][0],
                BEGIN_PHASE_POINTS[0][1], mWidth, 0);

        final View thisView = this;
        if (Build.VERSION.SDK_INT >= 16) {
            thisView.postInvalidateOnAnimation();
        } else {
            thisView.invalidate();
        }
    }


    public void appearPhase(float move1, float move2) {
        onPreDragWave();
        mWavePath.moveTo(0, 0);
        //左半分の描画
        mWavePath.cubicTo(mWidth * APPEAR_PHASE_POINTS[0][0], mWidth * APPEAR_PHASE_POINTS[0][1],
                mWidth * Math.min(BEGIN_PHASE_POINTS[1][0] + move2, APPEAR_PHASE_POINTS[1][0]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[1][1] + move1 - move2, APPEAR_PHASE_POINTS[1][1]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[2][0] - move2, APPEAR_PHASE_POINTS[2][0]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[2][1] + move1 - move2, APPEAR_PHASE_POINTS[2][1]));
        mWavePath.cubicTo(
                mWidth * Math.max(BEGIN_PHASE_POINTS[3][0] - move2, APPEAR_PHASE_POINTS[3][0]),
                mWidth * Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[4][0] - move2, APPEAR_PHASE_POINTS[4][0]),
                mWidth * Math.min(BEGIN_PHASE_POINTS[4][1] + move1 + move2, APPEAR_PHASE_POINTS[4][1]),
                mWidth * APPEAR_PHASE_POINTS[5][0],
                mWidth * Math.min(BEGIN_PHASE_POINTS[0][1] + move1 + move2, APPEAR_PHASE_POINTS[5][1]));
        //右半分の描画
        mWavePath.cubicTo(
                mWidth - mWidth * Math.max(BEGIN_PHASE_POINTS[4][0] - move2, APPEAR_PHASE_POINTS[4][0]),
                mWidth * Math.min(BEGIN_PHASE_POINTS[4][1] + move1 + move2, APPEAR_PHASE_POINTS[4][1]),
                mWidth - mWidth * Math.max(BEGIN_PHASE_POINTS[3][0] - move2, APPEAR_PHASE_POINTS[3][0]),
                mWidth * Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1]),
                mWidth - mWidth * Math.max(BEGIN_PHASE_POINTS[2][0] - move2, APPEAR_PHASE_POINTS[2][0]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[2][1] + move1 - move2, APPEAR_PHASE_POINTS[2][1]));
        mWavePath.cubicTo(
                mWidth - mWidth * Math.min(BEGIN_PHASE_POINTS[1][0] + move2, APPEAR_PHASE_POINTS[1][0]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[1][1] + move1 - move2, APPEAR_PHASE_POINTS[1][1]),
                mWidth - mWidth * APPEAR_PHASE_POINTS[0][0], mWidth * APPEAR_PHASE_POINTS[0][1], mWidth, 0);
        mCurrentCircleCenterY =
                mWidth * Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1])
                        + mDropCircleRadius;

        final View thisView = this;
        if (Build.VERSION.SDK_INT >= 16) {
            thisView.postInvalidateOnAnimation();
        } else {
            thisView.invalidate();
        }
    }

    public void expandPhase(float move1, float move2, float move3) {
        onPreDragWave();
        mWavePath.moveTo(0, 0);
        //左半分の描画
        mWavePath.cubicTo(mWidth * EXPAND_PHASE_POINTS[0][0], mWidth * EXPAND_PHASE_POINTS[0][1],
                mWidth * Math.min(
                        Math.min(BEGIN_PHASE_POINTS[1][0] + move2, APPEAR_PHASE_POINTS[1][0]) + move3,
                        EXPAND_PHASE_POINTS[1][0]), mWidth * Math.max(
                        Math.max(BEGIN_PHASE_POINTS[1][1] + move1 - move2, APPEAR_PHASE_POINTS[1][1]) - move3,
                        EXPAND_PHASE_POINTS[1][1]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[2][0] - move2, EXPAND_PHASE_POINTS[2][0]),
                mWidth * Math.min(
                        Math.max(BEGIN_PHASE_POINTS[2][1] + move1 - move2, APPEAR_PHASE_POINTS[2][1]) + move3,
                        EXPAND_PHASE_POINTS[2][1]));
        mWavePath.cubicTo(mWidth * Math.min(
                Math.max(BEGIN_PHASE_POINTS[3][0] - move2, APPEAR_PHASE_POINTS[3][0]) + move3,
                EXPAND_PHASE_POINTS[3][0]), mWidth * Math.min(
                Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1]) + move3,
                EXPAND_PHASE_POINTS[3][1]),
                mWidth * Math.max(BEGIN_PHASE_POINTS[4][0] - move2, EXPAND_PHASE_POINTS[4][0]),
                mWidth * Math.min(
                        Math.min(BEGIN_PHASE_POINTS[4][1] + move1 + move2, APPEAR_PHASE_POINTS[4][1]) + move3,
                        EXPAND_PHASE_POINTS[4][1]), mWidth * EXPAND_PHASE_POINTS[5][0], mWidth * Math.min(
                        Math.min(BEGIN_PHASE_POINTS[0][1] + move1 + move2, APPEAR_PHASE_POINTS[5][1]) + move3,
                        EXPAND_PHASE_POINTS[5][1]));

        //右半分の描画
        mWavePath.cubicTo(
                mWidth - mWidth * Math.max(BEGIN_PHASE_POINTS[4][0] - move2, EXPAND_PHASE_POINTS[4][0]),
                mWidth * Math.min(
                        Math.min(BEGIN_PHASE_POINTS[4][1] + move1 + move2, APPEAR_PHASE_POINTS[4][1]) + move3,
                        EXPAND_PHASE_POINTS[4][1]), mWidth - mWidth * Math.min(
                        Math.max(BEGIN_PHASE_POINTS[3][0] - move2, APPEAR_PHASE_POINTS[3][0]) + move3,
                        EXPAND_PHASE_POINTS[3][0]), mWidth * Math.min(
                        Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1]) + move3,
                        EXPAND_PHASE_POINTS[3][1]),
                mWidth - mWidth * Math.max(BEGIN_PHASE_POINTS[2][0] - move2, EXPAND_PHASE_POINTS[2][0]),
                mWidth * Math.min(
                        Math.max(BEGIN_PHASE_POINTS[2][1] + move1 - move2, APPEAR_PHASE_POINTS[2][1]) + move3,
                        EXPAND_PHASE_POINTS[2][1]));
        mWavePath.cubicTo(mWidth - mWidth * Math.min(
                Math.min(BEGIN_PHASE_POINTS[1][0] + move2, APPEAR_PHASE_POINTS[1][0]) + move3,
                EXPAND_PHASE_POINTS[1][0]), mWidth * Math.max(
                Math.max(BEGIN_PHASE_POINTS[1][1] + move1 - move2, APPEAR_PHASE_POINTS[1][1]) - move3,
                EXPAND_PHASE_POINTS[1][1]), mWidth - mWidth * EXPAND_PHASE_POINTS[0][0],
                mWidth * EXPAND_PHASE_POINTS[0][1], mWidth, 0);
        mCurrentCircleCenterY = mWidth * Math.min(
                Math.min(BEGIN_PHASE_POINTS[3][1] + move1 + move2, APPEAR_PHASE_POINTS[3][1]) + move3,
                EXPAND_PHASE_POINTS[3][1]) + mDropCircleRadius;

        final View thisView = this;
        if (Build.VERSION.SDK_INT >= 16) {
            thisView.postInvalidateOnAnimation();
        } else {
            thisView.invalidate();
        }
    }

    /**
     * @param height 高さ
     */
    protected void updateMaxDropHeight(int height) {
        if (500 * (mWidth / 1440.f) > height) {
//            Log.w("WaveView", "DropHeight is more than " + 500 * (mWidth / 1440.f));
            return;
        }
        final View thisView = this;
        mMaxDropHeight = (int) Math.min(height, thisView.getHeight() - mDropCircleRadius);
        if (mIsManualRefreshing) {
            mIsManualRefreshing = false;
            manualRefresh();
        }
    }

    public void startDropAnimation() {
        // show dropBubble again
        mDisappearCircleAnimator = ValueAnimator.ofFloat(1.f, 1.f);
        mDisappearCircleAnimator.setDuration(1);
        mDisappearCircleAnimator.start();

        mDropCircleAnimator = ValueAnimator.ofFloat(500 * (mWidth / 1440.f), mMaxDropHeight);
        mDropCircleAnimator.setDuration(DROP_CIRCLE_ANIMATOR_DURATION);
        mDropCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentCircleCenterY = (float) animation.getAnimatedValue();
                final View thisView = WaveView.this;
                if (Build.VERSION.SDK_INT >= 16) {
                    thisView.postInvalidateOnAnimation();
                } else {
                    thisView.invalidate();
                }
            }
        });
        mDropCircleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mDropCircleAnimator.start();

        mDropVertexAnimator = ValueAnimator.ofFloat(0.f, mMaxDropHeight - mDropCircleRadius);
        mDropVertexAnimator.setDuration(DROP_VERTEX_ANIMATION_DURATION);
        mDropVertexAnimator.addUpdateListener(mAnimatorUpdateListener);
        mDropVertexAnimator.start();

        mDropBounceVerticalAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        mDropBounceVerticalAnimator.setDuration(DROP_BOUNCE_ANIMATOR_DURATION);
        mDropBounceVerticalAnimator.addUpdateListener(mAnimatorUpdateListener);
        mDropBounceVerticalAnimator.setInterpolator(new DropBounceInterpolator());
        mDropBounceVerticalAnimator.setStartDelay(DROP_VERTEX_ANIMATION_DURATION);
        mDropBounceVerticalAnimator.start();

        mDropBounceHorizontalAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        mDropBounceHorizontalAnimator.setDuration(DROP_BOUNCE_ANIMATOR_DURATION);
        mDropBounceHorizontalAnimator.addUpdateListener(mAnimatorUpdateListener);
        mDropBounceHorizontalAnimator.setInterpolator(new DropBounceInterpolator());
        mDropBounceHorizontalAnimator.setStartDelay(
                (long) (DROP_VERTEX_ANIMATION_DURATION + DROP_BOUNCE_ANIMATOR_DURATION * 0.25));
        mDropBounceHorizontalAnimator.start();
    }

    public void startDisappearCircleAnimation() {
        mDisappearCircleAnimator = ValueAnimator.ofFloat(1.f, 0.f);
        mDisappearCircleAnimator.addUpdateListener(mAnimatorUpdateListener);
        mDisappearCircleAnimator.setDuration(DROP_REMOVE_ANIMATOR_DURATION);
        mDisappearCircleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //アニメーション修旅時にAnimatorをリセットすることにより落ちてくる円の初期位置を-100.fにする
                resetAnimator();
                mIsManualRefreshing = false;
            }
        });
        mDisappearCircleAnimator.start();
    }

    /**
     * @param h 波が始まる高さ
     */
    public void startWaveAnimation(float h) {
        h = Math.min(h, MAX_WAVE_HEIGHT) * mWidth;
        mWaveReverseAnimator = ValueAnimator.ofFloat(h, 0.f);
        mWaveReverseAnimator.setDuration(WAVE_ANIMATOR_DURATION);
        mWaveReverseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float h = (Float) valueAnimator.getAnimatedValue();
                mWavePath.moveTo(0, 0);
                mWavePath.quadTo(0.25f * mWidth, 0, 0.333f * mWidth, h * 0.5f);
                mWavePath.quadTo(mWidth * 0.5f, h * 1.4f, 0.666f * mWidth, h * 0.5f);
                mWavePath.quadTo(0.75f * mWidth, 0, mWidth, 0);
                final View thisView = WaveView.this;
                thisView.postInvalidate();
            }
        });
        mWaveReverseAnimator.setInterpolator(new BounceInterpolator());
        mWaveReverseAnimator.start();
    }

    public void animationDropCircle() {
        if (mDisappearCircleAnimator.isRunning()) {
            return;
        }
        startDropAnimation();
        startWaveAnimation(0.1f);
    }

    public float getCurrentCircleCenterY() {
        return mCurrentCircleCenterY;
    }

//    /**
//     * @param maxDropHeight ある程度の高さ
//     */
//    public void setMaxDropHeight(int maxDropHeight) {
//        if (mDropHeightUpdated) {
//            updateMaxDropHeight(maxDropHeight);
//        } else {
//            mUpdateMaxDropHeight = maxDropHeight;
//            mDropHeightUpdated = true;
//            if (getViewTreeObserver().isAlive()) {
//                getViewTreeObserver().removeOnPreDrawListener(this);
//                getViewTreeObserver().addOnPreDrawListener(this);
//            }
//        }
//    }
//
//    public boolean isDisappearCircleAnimatorRunning() {
//        return mDisappearCircleAnimator.isRunning();
//    }
//
//    /**
//     * @param radius 影の深さ
//     */
//    public void setShadowRadius(int radius) {
////    mShadowPaint.setShadowLayer(radius, 0.0f, 2.0f, SHADOW_COLOR);
//        mPaint.setShadowLayer(radius, 0f, 0f, SHADOW_COLOR);
//    }

    /**
     * @param radius 影の深さ
     * @param color 阴影颜色
     */
    public void setShadow(int radius, int color) {
        mPaint.setShadowLayer(radius, 0f, 0f, color);
    }

    /**
     * WaveView is colored by given color (including alpha)
     *
     * @param color ARGB color. WaveView will be colored by Black if rgb color is provided.
     * @see Paint#setColor(int)
     */
    public void setWaveColor(@ColorInt int color) {
        mPaint.setColor(color);
        final View thisView = this;
        thisView.invalidate();
    }

//    public void setWaveARGBColor(int a, int r, int g, int b) {
//        mPaint.setARGB(a, r, g, b);
//        invalidate();
//    }
}