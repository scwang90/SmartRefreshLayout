package com.scwang.smartrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.ColorUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * DropboxRefresh
 * https://dribbble.com/shots/3470499-Dropbox-Refresh
 * Created by SCWANG on 2017/6/24.
 */

public class DropboxHeader extends View implements RefreshHeader {

    //<editor-fold desc="Field">
    private Path mPath;
    private Paint mPaint;
    private BoxBody mBoxBody;
    private int mAccentColor;
    private int mHeaderHeight;
    private boolean mDropOutOverFlow;
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private Drawable mDrawable3;
    private float mDropOutPercent;
    private float mReboundPercent;
    private ValueAnimator mReboundAnimator;
    private ValueAnimator mDropOutAnimator;
    private RefreshState mState;
    //</editor-fold>

    //<editor-fold desc="View">
    public DropboxHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public DropboxHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public DropboxHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public DropboxHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private void initView(Context context, AttributeSet attrs) {
        mPath = new Path();
        mPaint = new Paint();
        mBoxBody = new BoxBody();
        mPaint.setAntiAlias(true);
        mAccentColor = 0xff6ea9ff;
        setBackgroundColor(0xff283645);
        setMinimumHeight(DensityUtil.dp2px(150));


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropboxHeader);
        if (ta.hasValue(R.styleable.DropboxHeader_dhDrawable1)) {
            mDrawable1 = ta.getDrawable(R.styleable.DropboxHeader_dhDrawable1);
        } else {
            PathsDrawable drawable1 = new PathsDrawable();
            drawable1.parserPaths(
                    "M3 2h18v20h-18z",
                    "m4,1c-1.105,0 -2,0.895 -2,2v3,11 3,1c0,1.105 0.895,2 2,2h2,12 2c1.105,0 2,-0.895 2,-2v-1,-3 -11,-3c0,-1.105 -0.895,-2 -2,-2h-2,-12 -2zM3.5,3h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,3h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,6h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,6h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,9h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,9h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,12h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,12h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,15h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,15h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,18h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,18h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5z"
            );
            drawable1.parserColors(
                    0xffecf0f1,
                    0xfffc4108
            );
            mDrawable1 = drawable1;
        }
        if (ta.hasValue(R.styleable.DropboxHeader_dhDrawable2)) {
            mDrawable2 = ta.getDrawable(R.styleable.DropboxHeader_dhDrawable2);
        } else {
            PathsDrawable drawable2 = new PathsDrawable();
            drawable2.parserPaths(
                    "M49,16.5l-14,-14l-27,0l0,53l41,0z",
                    "m16,23.5h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1L16,21.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1z",
                    "m16,15.5h10c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1L16,13.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1z",
                    "M41,29.5L16,29.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
                    "M41,37.5L16,37.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
                    "M41,45.5L16,45.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
                    "M49,16.5l-14,-14l0,14z"
            );
            drawable2.parserColors(
                    0xfffed469,
                    0xffd5ae57
            );
            mDrawable2 = drawable2;
        }
        if (ta.hasValue(R.styleable.DropboxHeader_dhDrawable3)) {
            mDrawable3 = ta.getDrawable(R.styleable.DropboxHeader_dhDrawable3);
        } else {
            PathsDrawable drawable3 = new PathsDrawable();
            drawable3.parserPaths("M6.021,2.188L6.021,11.362C5.46,11.327 4.843,11.414 4.229,11.663C2.624,12.312 1.696,13.729 2.155,14.825C2.62,15.924 4.294,16.284 5.898,15.634C7.131,15.134 7.856,14.184 7.965,13.272L7.958,4.387L15.02,3.028L15.02,9.406C14.422,9.343 13.746,9.432 13.076,9.703C11.471,10.353 10.544,11.77 11.004,12.866C11.467,13.964 13.141,14.325 14.746,13.675C15.979,13.174 16.836,12.224 16.947,11.313L16.958,0.002L6.021,2.188L6.021,2.188Z");
            drawable3.parserColors(0xff98d761);
            mDrawable3 = drawable3;
        }
        ta.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mReboundAnimator != null) {
            mReboundAnimator.removeAllUpdateListeners();
            mReboundAnimator.removeAllListeners();
            mReboundAnimator = null;
        }
        if (mDropOutAnimator != null) {
            mDropOutAnimator.removeAllUpdateListeners();
            mDropOutAnimator.removeAllListeners();
            mDropOutAnimator = null;
        }
    }

    private void initAnimator() {
        AccelerateInterpolator interpolator = new AccelerateInterpolator();
        mReboundAnimator = ValueAnimator.ofFloat(0, 1, 0);
        mReboundAnimator.setInterpolator(interpolator);
        mReboundAnimator.setDuration(300);
        mReboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mReboundPercent = (float) animation.getAnimatedValue();
                DropboxHeader.this.invalidate();
            }
        });
        mReboundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState == RefreshState.Refreshing) {
                    if (mDropOutAnimator != null) {
                        mDropOutAnimator.start();
                    }
                }
            }
        });

        mDropOutAnimator = ValueAnimator.ofFloat(0, 1);
        mDropOutAnimator.setInterpolator(interpolator);
        mDropOutAnimator.setDuration(300);
        mDropOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mDropOutPercent < 1 || mDropOutPercent >= 3) {
                    mDropOutPercent = (float) animation.getAnimatedValue();
                } else if (mDropOutPercent < 2) {
                    mDropOutPercent = 1 + (float) animation.getAnimatedValue();
                } else if (mDropOutPercent < 3) {
                    mDropOutPercent = 2 + (float) animation.getAnimatedValue();
                    if (mDropOutPercent == 3) {
                        mDropOutOverFlow = true;
                    }
                }
                DropboxHeader.this.invalidate();
            }
        });
        mDropOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mReboundAnimator != null) {
                    mReboundAnimator.start();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();

        final int sideLength = generateSideLength();
        BoxBody body = generateBoxBody(width, height, sideLength);

        mPaint.setColor(ColorUtils.setAlphaComponent(mAccentColor, 150));
        canvas.drawPath(generateBoxBodyPath(body), mPaint);

        mPaint.setColor(mAccentColor);
        canvas.drawPath(generateBoxCoverPath(body), mPaint);

        if (isInEditMode()) {
            mDropOutPercent = 2.5f;
        }
        if (mDropOutPercent > 0) {
            canvas.clipPath(generateClipPath(body, width));

            final float percent1 = Math.min(mDropOutPercent, 1);
            Rect bounds1 = mDrawable1.getBounds();
            bounds1.offsetTo(width / 2 - bounds1.width() / 2, (int)((body.boxCenterY - bounds1.height() / 2 + bounds1.height()) * percent1) - bounds1.height());
            mDrawable1.draw(canvas);

            final float percent2 = Math.min(Math.max(mDropOutPercent - 1, 0), 1);
            Rect bounds2 = mDrawable2.getBounds();
            bounds2.offsetTo(width / 2 - bounds2.width() / 2, (int)((body.boxCenterY - bounds2.height() / 2 + bounds2.height()) * percent2) - bounds2.height());
            mDrawable2.draw(canvas);

            final float percent3 = Math.min(Math.max(mDropOutPercent - 2, 0), 1);
            Rect bounds3 = mDrawable3.getBounds();
            bounds3.offsetTo(width / 2 - bounds3.width() / 2, (int) ((body.boxCenterY - bounds3.height() / 2 + bounds3.height()) * percent3) - bounds3.height());
            mDrawable3.draw(canvas);

            if (mDropOutOverFlow) {
                bounds1.offsetTo(width / 2 - bounds1.width() / 2, ((body.boxCenterY - bounds1.height() / 2)));
                mDrawable1.draw(canvas);

                bounds2.offsetTo(width / 2 - bounds2.width() / 2, ((body.boxCenterY - bounds2.height() / 2)));
                mDrawable2.draw(canvas);

                bounds3.offsetTo(width / 2 - bounds3.width() / 2, ((body.boxCenterY - bounds3.height() / 2)));
                mDrawable3.draw(canvas);
            }
        }

    }

    //</editor-fold>

    //<editor-fold desc="路径绘制">
    private int generateSideLength() {
        return mHeaderHeight / 5;
    }

    @NonNull
    private Path generateClipPath(BoxBody body, int width) {
        mPath.reset();
        mPath.lineTo(0, body.boxCenterTop);
        mPath.lineTo(body.boxLeft, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxCenterY);
        mPath.lineTo(body.boxRight, body.boxCenterTop);
        mPath.lineTo(width, body.boxCenterTop);
        mPath.lineTo(width, 0);
        mPath.close();
        return mPath;
    }

    @NonNull
    private BoxBody generateBoxBody(int width, int height, int sideLength) {
        final int margin = sideLength / 2;
        return mBoxBody.measure(width, height, sideLength, margin);
    }

    @NonNull
    private Path generateBoxCoverPath(BoxBody body) {
        mPath.reset();
        final int sideLength = (body.boxCenterX - body.boxLeft) * 4 / 5;

        final double offsetAngle = mReboundPercent * (Math.PI * 2 / 5);

        /**
         * 开始画左上的盖子
         */
        final float offsetLeftTopX = sideLength * (float) Math.sin(Math.PI / 3 - offsetAngle / 2);
        final float offsetLeftTopY = sideLength * (float) Math.cos(Math.PI / 3 - offsetAngle / 2);
        mPath.moveTo(body.boxLeft, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxCenterX - offsetLeftTopX, body.boxTop - offsetLeftTopY);
        mPath.lineTo(body.boxLeft - offsetLeftTopX, body.boxCenterTop - offsetLeftTopY);
        mPath.close();

        /**
         * 开始画左下的盖子
         */
        final float offsetLeftBottomX = sideLength * (float) Math.sin(Math.PI / 3 + offsetAngle);
        final float offsetLeftBottomY = sideLength * (float) Math.cos(Math.PI / 3 + offsetAngle);
        mPath.moveTo(body.boxLeft, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, (body.boxBottom + body.boxTop) / 2);
        mPath.lineTo(body.boxCenterX - offsetLeftBottomX, (body.boxBottom + body.boxTop) / 2 + offsetLeftBottomY);
        mPath.lineTo(body.boxLeft - offsetLeftBottomX, body.boxCenterTop + offsetLeftBottomY);
        mPath.close();

        /**
         * 开始画右上的盖子
         */
        final float offsetRightTopX = sideLength * (float) Math.sin(Math.PI / 3 - offsetAngle / 2);
        final float offsetRightTopY = sideLength * (float) Math.cos(Math.PI / 3 - offsetAngle / 2);
        mPath.moveTo(body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxCenterX + offsetRightTopX, body.boxTop - offsetRightTopY);
        mPath.lineTo(body.boxRight + offsetRightTopX, body.boxCenterTop - offsetRightTopY);
        mPath.close();

        /**
         * 开始画右下的盖子
         */
        final float offsetRightBottomX = sideLength * (float) Math.sin(Math.PI / 3 + offsetAngle);
        final float offsetRightBottomY = sideLength * (float) Math.cos(Math.PI / 3 + offsetAngle);
        mPath.moveTo(body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, (body.boxBottom + body.boxTop) / 2);
        mPath.lineTo(body.boxCenterX + offsetRightBottomX, (body.boxBottom + body.boxTop) / 2 + offsetRightBottomY);
        mPath.lineTo(body.boxRight + offsetRightBottomX, body.boxCenterTop + offsetRightBottomY);
        mPath.close();

        return mPath;
    }

    @NonNull
    private Path generateBoxBodyPath(BoxBody body) {
        mPath.reset();

        mPath.moveTo(body.boxLeft, body.boxCenterBottom);
        mPath.lineTo(body.boxCenterX, body.boxBottom);
        mPath.lineTo(body.boxRight, body.boxCenterBottom);
        mPath.quadTo(body.boxRight + body.boxSideLength / 2 * mReboundPercent, body.boxCenterY, body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxLeft, body.boxCenterTop);
        mPath.quadTo(body.boxLeft - body.boxSideLength / 2 * mReboundPercent, body.boxCenterY, body.boxLeft, body.boxCenterBottom);

        mPath.close();
        return mPath;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        if (mState != RefreshState.Refreshing) {
            mReboundPercent = 1f * Math.max(0, offset - headerHeight) / extendHeight;
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
        mReboundPercent = 1f * Math.max(0, offset - headerHeight) / extendHeight;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
        if (newState == RefreshState.None) {
            mDropOutOverFlow = false;
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setBackgroundColor(colors[0]);
            if (colors.length > 1) {
                mAccentColor = colors[1];
            }
        }
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mHeaderHeight = height;
        final int sideLength = generateSideLength();
        mDrawable1.setBounds(0, 0, sideLength, sideLength);
        mDrawable2.setBounds(0, 0, sideLength, sideLength);
        mDrawable3.setBounds(0, 0, sideLength, sideLength);
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
        if (mDropOutAnimator != null) {
            mDropOutAnimator.start();
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mDropOutPercent = 0;
        return 0;
    }
    //</editor-fold>

    private static class BoxBody {

        private int boxCenterX;
        private int boxCenterY;
        private int boxBottom;
        private int boxTop;
        private int boxLeft;
        private int boxCenterTop;
        private int boxCenterBottom;
        private int boxRight;
        private int boxSideLength;

        BoxBody measure(int width, int height, int sideLength, int margin) {
            boxSideLength = sideLength;
            boxCenterX = width / 2;
            boxBottom = height - margin;
            boxTop = boxBottom - 2 * sideLength;
            boxLeft = boxCenterX - (int) (sideLength * Math.sin(Math.PI / 3));
            boxCenterTop = boxTop + sideLength / 2;
            boxCenterBottom = boxBottom - sideLength / 2;
            boxRight = width - boxLeft;
            boxCenterY = boxBottom - sideLength;
            return this;
        }
    }

}
