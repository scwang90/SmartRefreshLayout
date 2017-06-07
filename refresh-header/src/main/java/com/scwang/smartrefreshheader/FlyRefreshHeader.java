package com.scwang.smartrefreshheader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.scwang.smartrefreshheader.flyrefresh.FlyView;
import com.scwang.smartrefreshheader.flyrefresh.MountanScenceView;
import com.scwang.smartrefreshlayout.SmartRefreshLayout.LayoutParams;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.api.RefreshLayout;
import com.scwang.smartrefreshlayout.api.SizeObserver;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.scwang.smartrefreshlayout.util.DensityUtil.dp2px;

/**
 * 纸飞机和山丘
 * Created by SCWANG on 2017/6/6.
 */

public class FlyRefreshHeader implements RefreshHeader, SizeObserver {

    private View mFalsifyHeaderView;
    private float mCurrentPercent;
    private FlyView mFlyView;
    private MountanScenceView mScenceView;
    private AnimatorSet mFlyAnimator;
    private RefreshLayout mRefreshLayout;
    private boolean mIsRefreshing = false;
    private int mOffset = 0;

    public FlyRefreshHeader(Context context) {
        mFalsifyHeaderView = new View(context);
        mFalsifyHeaderView.setLayoutParams(new LayoutParams(MATCH_PARENT, dp2px(50)));
    }

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
            mFlyView.setRotation((-45f) * offset / (headHeight + extendHeight));
        }
    }


    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        if (!mIsRefreshing) {
            onPullingDown(percent, offset, headHeight, extendHeight);
        }
    }

    @Override
    public void startAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        layout.finisRefresh(0);
        if (mCurrentPercent > 0) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentPercent, 0);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(animation -> onPullingDown((float)animation.getAnimatedValue(), 0, 0, 0));
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


            ObjectAnimator transX = ObjectAnimator.ofFloat(mFlyView, "translationX", 0, ((View) mRefreshLayout).getWidth()-mFlyView.getLeft());
            ObjectAnimator transY = ObjectAnimator.ofFloat(mFlyView, "translationY", 0, -(mFlyView.getTop() - mOffset) * 2 / 3);
            transY.setInterpolator(PathInterpolatorCompat.create(0.7f, 1f));
            ObjectAnimator rotation = ObjectAnimator.ofFloat(mFlyView, "rotation", -45, 0);
            rotation.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator rotationX = ObjectAnimator.ofFloat(mFlyView, "rotationX", 0, 60);
            rotationX.setInterpolator(new DecelerateInterpolator());

            AnimatorSet flyUpAnim = new AnimatorSet();
            flyUpAnim.setDuration(800);
            flyUpAnim.playTogether(transX
                    ,transY
                    ,rotation
                    ,rotationX
                    ,ObjectAnimator.ofFloat(mFlyView, "scaleX", 1, 0.5f)
                    ,ObjectAnimator.ofFloat(mFlyView, "scaleY", 1, 0.5f)
            );

            mFlyAnimator = flyUpAnim;
            mFlyAnimator.start();
        }
    }

    @Override
    public void onStateChanged(RefreshState state) {
    }

    @Override
    public void onFinish(RefreshLayout layout) {

    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            if (mScenceView != null) {
                mScenceView.setPrimaryColor(colors[0]);
            }
        }
    }

    @NonNull
    @Override
    public View getView() {
        return mFalsifyHeaderView;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.FixedBehind;
    }

    @Override
    public void onSizeDefined(RefreshLayout layout, int height, int extendHeight) {
        mRefreshLayout = layout;
    }

    //</editor-fold>

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

    private void clearAnimator(View v) {
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        // @TODO https://code.google.com/p/android/issues/detail?id=80863
        // ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        v.setPivotY(v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null);
    }

    public void finishRefresh(AnimatorListenerAdapter listenerAdapter) {
        if (mFlyView == null || !mIsRefreshing) {
            return;
        }
        if (mFlyAnimator != null) {
            mFlyAnimator.end();
            mFlyView.clearAnimation();
        }

        mIsRefreshing = false;
        mRefreshLayout.setEnableRefresh(true);

        final int offDistX = -mFlyView.getRight();
        final int offDistY = -DensityUtil.dp2px(10);
        AnimatorSet flyDownAnim = new AnimatorSet();
        flyDownAnim.setDuration(800);
        ObjectAnimator transX1 = ObjectAnimator.ofFloat(mFlyView, "translationX", ((View) mRefreshLayout).getWidth() - mFlyView.getLeft(), offDistX);
        ObjectAnimator transY1 = ObjectAnimator.ofFloat(mFlyView, "translationY", -(mFlyView.getTop() - mOffset) * 2 / 3, offDistY);
        transY1.setInterpolator(PathInterpolatorCompat.create(0.1f, 1f));
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(mFlyView, "rotation", mFlyView.getRotation(), 0);
        rotation1.setInterpolator(new AccelerateInterpolator());
        flyDownAnim.playTogether(transX1, transY1,
                ObjectAnimator.ofFloat(mFlyView, "scaleX", 0.5f, 0.9f),
                ObjectAnimator.ofFloat(mFlyView, "scaleY", 0.5f, 0.9f),
                rotation1
        );
        flyDownAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlyView.setRotationY(180);
            }
        });
        AnimatorSet flyInAnim = new AnimatorSet();
        flyInAnim.setDuration(400);
        flyInAnim.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator tranX2 = ObjectAnimator.ofFloat(mFlyView, "translationX", offDistX, 0);
        ObjectAnimator tranY2 = ObjectAnimator.ofFloat(mFlyView, "translationY", offDistY, 0);
        ObjectAnimator rotationX2 = ObjectAnimator.ofFloat(mFlyView, "rotationX", 30, 0);
        flyInAnim.playTogether(tranX2, tranY2, rotationX2,
                ObjectAnimator.ofFloat(mFlyView, "scaleX", 0.9f, 1f),
                ObjectAnimator.ofFloat(mFlyView, "scaleY", 0.9f, 1f));
        flyInAnim.setStartDelay(100);
        flyInAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlyView.setRotationY(0);
            }
        });

        if (listenerAdapter != null) {
            flyInAnim.addListener(listenerAdapter);
        }
        mFlyAnimator = new AnimatorSet();
        mFlyAnimator.playSequentially(flyDownAnim, flyInAnim);
        mFlyAnimator.start();
    }
}
