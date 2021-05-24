package com.scwang.smart.refresh.header;

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
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.scwang.smart.drawable.PathsDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smartrefresh.header.R;

/**
 * DropBoxRefresh
 * Created by scwang on 2017/6/24.
 * design https://dribbble.com/shots/3470499-DropBox-Refresh
 */
public class DropBoxHeader extends SimpleComponent implements RefreshHeader {

    //<editor-fold desc="static">
    protected static String[] drawable1Paths = new String[]{
            "M3 2h18v20h-18z",
            "m4,1c-1.105,0 -2,0.895 -2,2v3,11 3,1c0,1.105 0.895,2 2,2h2,12 2c1.105,0 2,-0.895 2,-2v-1,-3 -11,-3c0,-1.105 -0.895,-2 -2,-2h-2,-12 -2zM3.5,3h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,3h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,6h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,6h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,9h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,9h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,12h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,12h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,15h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,15h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM3.5,18h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5zM19.5,18h1c0.276,0 0.5,0.224 0.5,0.5v1c0,0.276 -0.224,0.5 -0.5,0.5h-1c-0.276,0 -0.5,-0.224 -0.5,-0.5v-1c0,-0.276 0.224,-0.5 0.5,-0.5z"
    };
    protected static int[] drawable1Colors = new int[]{
            0xffecf0f1,
            0xfffc4108
    };
    protected static String[] drawable2Paths = new String[]{
            "M49,16.5l-14,-14l-27,0l0,53l41,0z",
            "m16,23.5h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1L16,21.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1z",
            "m16,15.5h10c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1L16,13.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1z",
            "M41,29.5L16,29.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
            "M41,37.5L16,37.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
            "M41,45.5L16,45.5c-0.55,0 -1,0.45 -1,1 0,0.55 0.45,1 1,1h25c0.55,0 1,-0.45 1,-1 0,-0.55 -0.45,-1 -1,-1z",
            "M49,16.5l-14,-14l0,14z"
    };
    protected static int[] drawable2Colors = new int[]{
            0xfffed469,
            0xffd5ae57
    };
    protected static String[] drawable3Paths = new String[]{
            "M6.021,2.188L6.021,11.362C5.46,11.327 4.843,11.414 4.229,11.663C2.624,12.312 1.696,13.729 2.155,14.825C2.62,15.924 4.294,16.284 5.898,15.634C7.131,15.134 7.856,14.184 7.965,13.272L7.958,4.387L15.02,3.028L15.02,9.406C14.422,9.343 13.746,9.432 13.076,9.703C11.471,10.353 10.544,11.77 11.004,12.866C11.467,13.964 13.141,14.325 14.746,13.675C15.979,13.174 16.836,12.224 16.947,11.313L16.958,0.002L6.021,2.188L6.021,2.188Z"
    };
    protected static int[] drawable3Colors = new int[]{
            0xff98d761
    };
    //</editor-fold>

    //<editor-fold desc="Field">
    protected Path mPath;
    protected Paint mPaint;
    protected BoxBody mBoxBody;
    protected int mHeight;
    protected int mAccentColor;
    protected int mHeaderHeight;
    protected int mBackgroundColor;
    protected boolean mDropOutOverFlow;
    protected Drawable mDrawable1;
    protected Drawable mDrawable2;
    protected Drawable mDrawable3;
    protected float mDropOutPercent;
    protected float mReboundPercent;
    protected ValueAnimator mReboundAnimator;
    protected ValueAnimator mDropOutAnimator;
    protected RefreshState mState;
    protected RefreshKernel mKernel;
    //</editor-fold>

    //<editor-fold desc="View">
    public DropBoxHeader(Context context) {
        this(context, null);
    }

    public DropBoxHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        mPath = new Path();
        mPaint = new Paint();
        mBoxBody = new BoxBody();
        mPaint.setAntiAlias(true);
        mAccentColor = 0xff6ea9ff;
        mBackgroundColor = 0xff283645;
        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(150));

        mSpinnerStyle = SpinnerStyle.FixedBehind;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropBoxHeader);
        if (ta.hasValue(R.styleable.DropBoxHeader_dhDrawable1)) {
            mDrawable1 = ta.getDrawable(R.styleable.DropBoxHeader_dhDrawable1);
        } else if (ta.hasValue(R.styleable.DropBoxHeader_srlDrawable1)) {
            mDrawable1 = ta.getDrawable(R.styleable.DropBoxHeader_srlDrawable1);
        } else {
            PathsDrawable drawable1 = new PathsDrawable();
            drawable1.parserColors(drawable1Colors);
            if (!drawable1.parserPaths(drawable1Paths)) {
                drawable1.declareOriginal(2, 1, 20, 22);
            }
//            drawable1.printOriginal("drawable1");
            mDrawable1 = drawable1;
        }
        if (ta.hasValue(R.styleable.DropBoxHeader_dhDrawable2)) {
            mDrawable2 = ta.getDrawable(R.styleable.DropBoxHeader_dhDrawable2);
        } else if (ta.hasValue(R.styleable.DropBoxHeader_srlDrawable2)) {
            mDrawable2 = ta.getDrawable(R.styleable.DropBoxHeader_srlDrawable2);
        } else {
            PathsDrawable drawable2 = new PathsDrawable();
            drawable2.parserColors(drawable2Colors);
            if (!drawable2.parserPaths(drawable2Paths)) {
                drawable2.declareOriginal(8, 3, 41, 53);
            }
//            drawable2.printOriginal("drawable2");
            mDrawable2 = drawable2;
        }
        if (ta.hasValue(R.styleable.DropBoxHeader_dhDrawable3)) {
            mDrawable3 = ta.getDrawable(R.styleable.DropBoxHeader_dhDrawable3);
        } else if (ta.hasValue(R.styleable.DropBoxHeader_srlDrawable3)) {
            mDrawable3 = ta.getDrawable(R.styleable.DropBoxHeader_srlDrawable3);
        } else {
            PathsDrawable drawable3 = new PathsDrawable();
            drawable3.parserColors(drawable3Colors);
            if (!drawable3.parserPaths(drawable3Paths)) {
                drawable3.declareOriginal(2, 0, 15, 16);
            }
//            drawable3.printOriginal("drawable3");
            mDrawable3 = drawable3;
        }
        ta.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AccelerateInterpolator interpolator = new AccelerateInterpolator();
        mReboundAnimator = ValueAnimator.ofFloat(0, 1, 0);
        mReboundAnimator.setInterpolator(interpolator);
        mReboundAnimator.setDuration(300);
        mReboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final View thisView = DropBoxHeader.this;
                mReboundPercent = (float) animation.getAnimatedValue();
                thisView.invalidate();
            }
        });
        mReboundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState == RefreshState.Refreshing) {
                    if (mDropOutAnimator != null) {
                        mDropOutAnimator.start();
                    }
                } else {
                    mDropOutPercent = 0;
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
                final View thisView = DropBoxHeader.this;
                thisView.invalidate();
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

    @Override
    protected void dispatchDraw(Canvas canvas) {

        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = mHeight;//thisView.getHeight();
        final int sideLength = generateSideLength();
        //noinspection EqualsBetweenInconvertibleTypes
        final boolean footer = mKernel != null && (this.equals(mKernel.getRefreshLayout().getRefreshFooter()));

        if (footer) {
            canvas.save();
            canvas.translate(0, thisView.getHeight() - mHeight);
        }

        BoxBody body = generateBoxBody(width, height, sideLength);

        mPaint.setColor(ColorUtils.setAlphaComponent(mAccentColor, 150));
        canvas.drawPath(generateBoxBodyPath(body), mPaint);

        mPaint.setColor(mAccentColor);
        canvas.drawPath(generateBoxCoverPath(body), mPaint);

        if (thisView.isInEditMode()) {
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

        if (footer) {
            canvas.restore();
        }

        super.dispatchDraw(canvas);
    }
    //</editor-fold>

    //<editor-fold desc="路径绘制">
    protected int generateSideLength() {
        return mHeaderHeight / 5;
    }

    @NonNull
    protected Path generateClipPath(BoxBody body, int width) {
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
    protected BoxBody generateBoxBody(int width, int height, int sideLength) {
        final int margin = sideLength / 2;
        return mBoxBody.measure(width, height, sideLength, margin);
    }

    @NonNull
    protected Path generateBoxCoverPath(BoxBody body) {
        mPath.reset();
        final int sideLength = (body.boxCenterX - body.boxLeft) * 4 / 5;

        final double offsetAngle = mReboundPercent * (Math.PI * 2 / 5);

        /*
         * 开始画左上的盖子
         */
        final float offsetLeftTopX = sideLength * (float) Math.sin(Math.PI / 3 - offsetAngle / 2);
        final float offsetLeftTopY = sideLength * (float) Math.cos(Math.PI / 3 - offsetAngle / 2);
        mPath.moveTo(body.boxLeft, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxCenterX - offsetLeftTopX, body.boxTop - offsetLeftTopY);
        mPath.lineTo(body.boxLeft - offsetLeftTopX, body.boxCenterTop - offsetLeftTopY);
        mPath.close();

        /*
         * 开始画左下的盖子
         */
        final float offsetLeftBottomX = sideLength * (float) Math.sin(Math.PI / 3 + offsetAngle);
        final float offsetLeftBottomY = sideLength * (float) Math.cos(Math.PI / 3 + offsetAngle);
        mPath.moveTo(body.boxLeft, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, (body.boxBottom + body.boxTop) / 2f);
        mPath.lineTo(body.boxCenterX - offsetLeftBottomX, (body.boxBottom + body.boxTop) / 2f + offsetLeftBottomY);
        mPath.lineTo(body.boxLeft - offsetLeftBottomX, body.boxCenterTop + offsetLeftBottomY);
        mPath.close();

        /*
         * 开始画右上的盖子
         */
        final float offsetRightTopX = sideLength * (float) Math.sin(Math.PI / 3 - offsetAngle / 2);
        final float offsetRightTopY = sideLength * (float) Math.cos(Math.PI / 3 - offsetAngle / 2);
        mPath.moveTo(body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxCenterX + offsetRightTopX, body.boxTop - offsetRightTopY);
        mPath.lineTo(body.boxRight + offsetRightTopX, body.boxCenterTop - offsetRightTopY);
        mPath.close();

        /*
         * 开始画右下的盖子
         */
        final float offsetRightBottomX = sideLength * (float) Math.sin(Math.PI / 3 + offsetAngle);
        final float offsetRightBottomY = sideLength * (float) Math.cos(Math.PI / 3 + offsetAngle);
        mPath.moveTo(body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, (body.boxBottom + body.boxTop) / 2f);
        mPath.lineTo(body.boxCenterX + offsetRightBottomX, (body.boxBottom + body.boxTop) / 2f + offsetRightBottomY);
        mPath.lineTo(body.boxRight + offsetRightBottomX, body.boxCenterTop + offsetRightBottomY);
        mPath.close();

        return mPath;
    }

    @NonNull
    protected Path generateBoxBodyPath(BoxBody body) {
        mPath.reset();

        mPath.moveTo(body.boxLeft, body.boxCenterBottom);
        mPath.lineTo(body.boxCenterX, body.boxBottom);
        mPath.lineTo(body.boxRight, body.boxCenterBottom);
        mPath.quadTo(body.boxRight + body.boxSideLength / 2f * mReboundPercent, body.boxCenterY, body.boxRight, body.boxCenterTop);
        mPath.lineTo(body.boxCenterX, body.boxTop);
        mPath.lineTo(body.boxLeft, body.boxCenterTop);
        mPath.quadTo(body.boxLeft - body.boxSideLength / 2f * mReboundPercent, body.boxCenterY, body.boxLeft, body.boxCenterBottom);

        mPath.close();
        return mPath;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mKernel = kernel;
        mHeaderHeight = height;
        kernel.requestDrawBackgroundFor(this, mBackgroundColor);
        final int sideLength = generateSideLength();
        mDrawable1.setBounds(0, 0, sideLength, sideLength);
        mDrawable2.setBounds(0, 0, sideLength, sideLength);
        mDrawable3.setBounds(0, 0, sideLength, sideLength);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        mHeight = offset;
        if (!isDragging || mState != RefreshState.Refreshing) {
            mReboundPercent = 1f * Math.max(0, offset - height) / maxDragHeight;
        }
        this.invalidate();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mState = newState;
        if (newState == RefreshState.None) {
            mDropOutOverFlow = false;
        }
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mBackgroundColor = colors[0];
            if (mKernel != null) {
                mKernel.requestDrawBackgroundFor(this, mBackgroundColor);
            }
            if (colors.length > 1) {
                mAccentColor = colors[1];
            }
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mDropOutAnimator != null) {
            mDropOutAnimator.start();
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mDropOutPercent = 0;
        return 0;
    }
    //</editor-fold>

    protected static class BoxBody {

        int boxCenterX;
        int boxCenterY;
        int boxBottom;
        int boxTop;
        int boxLeft;
        int boxCenterTop;
        int boxCenterBottom;
        int boxRight;
        int boxSideLength;

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
