package com.scwang.smartrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.scwang.smartrefresh.header.flyrefresh.FlyView;
import com.scwang.smartrefresh.header.flyrefresh.MountanScenceView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.SizeObserver;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.impl.RefreshLayoutHeaderHooker;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * 纸飞机和山丘
 * Created by SCWANG on 2017/6/6.
 */

public class FlyRefreshHeader extends FalsifyHeader implements RefreshHeader, SizeObserver {

    private FlyView mFlyView;
    private AnimatorSet mFlyAnimator;
    private MountanScenceView mScenceView;
    private RefreshLayout mRefreshLayout;
    private RefreshKernel mRefreshKernel;
    private float mCurrentPercent;
    private boolean mIsRefreshing = false;
    private int mOffset = 0;

    //<editor-fold desc="View">
    public FlyRefreshHeader(Context context) {
        super(context);
    }

    public FlyRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlyRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRefreshLayout = null;
        mRefreshKernel = null;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        if (offset < 0) {
            if (mOffset > 0) {
                offset = 0;
                percent = 0;
            } else {
                return;
            }
        }
        mOffset = offset;
        mCurrentPercent = percent;
        if (mScenceView != null) {
            mScenceView.updatePercent(percent);
            mScenceView.postInvalidate();
        }
        if (mFlyView != null) {
            if (headHeight + extendHeight > 0) {
                mFlyView.setRotation((-45f) * offset / (headHeight + extendHeight));
            } else {
                mFlyView.setRotation((-45f) * percent);
            }
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        if (!mIsRefreshing) {
            onPullingDown(percent, offset, headHeight, extendHeight);
        }
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        /**
         * 提前关闭 下拉视图偏移
         */
        mRefreshKernel.animSpinner(0);

        if (mCurrentPercent > 0) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentPercent, 0);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(animation -> {
                onPullingDown((float) animation.getAnimatedValue(), 0, 0, 0);
            });
            valueAnimator.start();
            mCurrentPercent = 0;
        }
        if (mFlyView != null && !mIsRefreshing) {
            if (mFlyAnimator != null) {
                mFlyAnimator.end();
                mFlyView.clearAnimation();
            }
            mIsRefreshing = true;
            layout.setEnableRefresh(false);


            final int offDistX = ((View) mRefreshLayout).getWidth()-mFlyView.getLeft();
            final int offDistY = -(mFlyView.getTop() - mOffset) * 2 / 3;
            ObjectAnimator transX = ObjectAnimator.ofFloat(mFlyView, "translationX", 0, offDistX);
            ObjectAnimator transY = ObjectAnimator.ofFloat(mFlyView, "translationY", 0, offDistY);
            transY.setInterpolator(PathInterpolatorCompat.create(0.7f, 1f));
            ObjectAnimator rotation = ObjectAnimator.ofFloat(mFlyView, "rotation", mFlyView.getRotation(), 0);
            rotation.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator rotationX = ObjectAnimator.ofFloat(mFlyView, "rotationX", mFlyView.getRotationX(), 50);
            rotationX.setInterpolator(new DecelerateInterpolator());

            AnimatorSet flyUpAnim = new AnimatorSet();
            flyUpAnim.setDuration(800);
            flyUpAnim.playTogether(transX
                    ,transY
                    ,rotation
                    ,rotationX
                    ,ObjectAnimator.ofFloat(mFlyView, "scaleX", mFlyView.getScaleX(), 0.5f)
                    ,ObjectAnimator.ofFloat(mFlyView, "scaleY", mFlyView.getScaleY(), 0.5f)
            );

            mFlyAnimator = flyUpAnim;
            mFlyAnimator.start();
        }
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            if (mScenceView != null) {
                mScenceView.setPrimaryColor(colors[0]);
            }
        }
    }

    @Override
    public void onSizeDefined(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshLayout = kernel.getRefreshLayout();
        mRefreshKernel.registHeaderHook(new RefreshLayoutHeaderHooker() {
            @Override
            public void onHookFinishRefresh(SuperMethod supper, RefreshLayout layout) {
                if (mIsRefreshing) {
                    finishRefresh(null);
                } else {
                    supper.invoke();
                }
            }
        });
    }

    //</editor-fold>

    //<editor-fold desc="API">
    public void setUpMountanScenceView(MountanScenceView scenceView){
        mScenceView = scenceView;
    }

    public void setUpFlyView(FlyView flyView) {
        mFlyView = flyView;
    }

    public void setUp(MountanScenceView scenceView, FlyView flyView) {
        setUpFlyView(flyView);
        setUpMountanScenceView(scenceView);
    }

    public void finishRefresh() {
        finishRefresh(null);
    }

    public void finishRefresh(AnimatorListenerAdapter listenerAdapter) {
        if (mFlyView == null || !mIsRefreshing || mRefreshLayout == null) {
            return;
        }
        if (mFlyAnimator != null) {
            mFlyAnimator.end();
            mFlyView.clearAnimation();
        }

        mIsRefreshing = false;
        mRefreshLayout.finishRefresh(0);

        final int offDistX = -mFlyView.getRight();
        final int offDistY = -DensityUtil.dp2px(10);
        AnimatorSet flyDownAnim = new AnimatorSet();
        flyDownAnim.setDuration(800);
        ObjectAnimator transX1 = ObjectAnimator.ofFloat(mFlyView, "translationX", mFlyView.getTranslationX(), offDistX);
        ObjectAnimator transY1 = ObjectAnimator.ofFloat(mFlyView, "translationY", mFlyView.getTranslationY(), offDistY);
        transY1.setInterpolator(PathInterpolatorCompat.create(0.1f, 1f));
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(mFlyView, "rotation", mFlyView.getRotation(), 0);
        ObjectAnimator rotationX1 = ObjectAnimator.ofFloat(mFlyView, "rotationX", mFlyView.getRotationX(), 30);
        rotation1.setInterpolator(new AccelerateInterpolator());
        flyDownAnim.playTogether(transX1, transY1
                , rotation1
                , rotationX1
                , ObjectAnimator.ofFloat(mFlyView, "scaleX", mFlyView.getScaleX(), 0.9f)
                , ObjectAnimator.ofFloat(mFlyView, "scaleY", mFlyView.getScaleY(), 0.9f)
        );
        flyDownAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlyView.setRotationY(180);
            }
        });
        AnimatorSet flyInAnim = new AnimatorSet();
        flyInAnim.setDuration(800);
        flyInAnim.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator tranX2 = ObjectAnimator.ofFloat(mFlyView, "translationX", offDistX, 0);
        ObjectAnimator tranY2 = ObjectAnimator.ofFloat(mFlyView, "translationY", offDistY, 0);
        ObjectAnimator rotationX2 = ObjectAnimator.ofFloat(mFlyView, "rotationX", 30, 0);
        flyInAnim.playTogether(tranX2, tranY2
                , rotationX2
                , ObjectAnimator.ofFloat(mFlyView, "scaleX", 0.9f, 1f)
                , ObjectAnimator.ofFloat(mFlyView, "scaleY", 0.9f, 1f)
        );
        flyInAnim.setStartDelay(100);
        flyInAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlyView.setRotationY(0);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRefreshLayout.setEnableRefresh(true);
                if (listenerAdapter != null) {
                    listenerAdapter.onAnimationEnd(animation);
                }
            }
        });

        mFlyAnimator = new AnimatorSet();
        mFlyAnimator.playSequentially(flyDownAnim, flyInAnim);
        mFlyAnimator.start();
    }
    //</editor-fold>
}
