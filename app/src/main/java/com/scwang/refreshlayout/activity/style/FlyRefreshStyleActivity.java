package com.scwang.refreshlayout.activity.style;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.util.StatusBarUtil;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.flyrefresh.FlyView;
import com.scwang.smartrefresh.header.flyrefresh.MountainSceneView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

public class FlyRefreshStyleActivity extends AppCompatActivity {

    private RecyclerView mListView;
    private RefreshLayout mRefreshLayout;

    private ItemAdapter mAdapter;

    private FlyView mFlyView;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private FlyRefreshHeader mFlyRefreshHeader;
    private CollapsingToolbarLayout mToolbarLayout;
    private FloatingActionButton mActionButton;
    private View.OnClickListener mThemeListener;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_refresh);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*-----------------------------------------------------------
         * 关键代码-开始
         *----------------------------------------------------------*/

        MountainSceneView mSceneView = (MountainSceneView) findViewById(R.id.mountain);
        mFlyView = (FlyView) findViewById(R.id.flyView);
        mFlyRefreshHeader = (FlyRefreshHeader)findViewById(R.id.flyRefresh);
        mFlyRefreshHeader.setUp(mSceneView, mFlyView);//绑定场景和纸飞机
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setReboundInterpolator(new ElasticOutInterpolator());//设置回弹插值器，会带有弹簧震动效果
        mRefreshLayout.setReboundDuration(800);//设置回弹动画时长
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                View child = mListView.getChildAt(0);
                if (child != null) {
                    //开始刷新的时候个第一个item设置动画效果
                    bounceAnimateView(child.findViewById(R.id.icon));
                }
                updateTheme();//改变主题颜色
                mRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //通知刷新完成，这里改为通知Header，让纸飞机飞回来
                        mFlyRefreshHeader.finishRefresh(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                addItemData();//在纸飞机回到原位之后添加数据效果更真实
                            }
                        });
                    }
                }, 2000);//模拟两秒的后台数据加载
            }
        });
        //设置 让 AppBarLayout 和 RefreshLayout 的滚动同步 并不保持 toolbar 位置不变
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(@NonNull RefreshHeader header, float percent, int offset, int footerHeight, int extendHeight) {
                appBar.setTranslationY(offset);
                toolbar.setTranslationY(-offset);
            }
            @Override
            public void onHeaderReleasing(@NonNull RefreshHeader header, float percent, int offset, int footerHeight, int extendHeight) {
                appBar.setTranslationY(offset);
                toolbar.setTranslationY(-offset);
            }
        });
        /*-----------------------------------------------------------
         * 关键代码-结束
         *----------------------------------------------------------*/

        if (isFirstEnter) {
            isFirstEnter = false;
            mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }

        /*
         * 初始化列表数据
         */
        initDataSet();
        mAdapter = new ItemAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mListView = (RecyclerView) findViewById(R.id.recyclerView);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new SampleItemAnimator());
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayout);
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        /*
         * 设置点击 ActionButton 时候触发自动刷新 并改变主题颜色
         */
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTheme();
                mRefreshLayout.autoRefresh();
            }
        });
        /*
         * 监听 AppBarLayout 的关闭和开启 给 FlyView（纸飞机） 和 ActionButton 设置关闭隐藏动画
         */
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    mActionButton.animate().scaleX(0).scaleY(0);
                    mFlyView.animate().scaleX(0).scaleY(0);
                    ValueAnimator animator = ValueAnimator.ofInt(mListView.getPaddingTop(), 0);
                    animator.setDuration(300);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mListView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
                        }
                    });
                    animator.start();
                }
                if (fraction > 0.8 && !misAppbarExpand) {
                    misAppbarExpand = true;
                    mActionButton.animate().scaleX(1).scaleY(1);
                    mFlyView.animate().scaleX(1).scaleY(1);
                    ValueAnimator animator = ValueAnimator.ofInt(mListView.getPaddingTop(), DensityUtil.dp2px(25));
                    animator.setDuration(300);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mListView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
                        }
                    });
                    animator.start();
                }
            }
        });


        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
    }

    private void updateTheme() {
        if (mThemeListener == null) {
            mThemeListener = new View.OnClickListener() {
                int index = 0;
                int[] ids = new int[]{
                        R.color.colorPrimary,
                        android.R.color.holo_green_light,
                        android.R.color.holo_red_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_blue_bright,
                };
                @Override
                public void onClick(View v) {
                    int color = ContextCompat.getColor(getApplication(), ids[index % ids.length]);
                    mRefreshLayout.setPrimaryColors(color);
                    mActionButton.setBackgroundColor(color);
                    mActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
                    mToolbarLayout.setContentScrimColor(color);
                    index++;
                }
            };
        }
        mThemeListener.onClick(null);
    }

    private void initDataSet() {
        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.drawable.ic_fly_refresh_poll, "Meeting Minutes", new Date(2014 - 1900, 2, 9)));
        mDataSet.add(new ItemData(Color.GRAY, R.drawable.ic_fly_refresh_folder, "Favorites Photos", new Date(2014 - 1900, 1, 3)));
        mDataSet.add(new ItemData(Color.GRAY, R.drawable.ic_fly_refresh_folder, "Photos", new Date(2014 - 1900, 0, 9)));
    }

    private void addItemData() {
        ItemData itemData = new ItemData(Color.parseColor("#FFC970"), R.drawable.ic_fly_refresh_smartphone, "Magic Cube Show", new Date());
        mDataSet.add(0, itemData);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    private void bounceAnimateView(final View view) {
        if (view == null) {
            return;
        }

        ValueAnimator swing = ValueAnimator.ofFloat(0, 60, -40, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setRotationX((float)animation.getAnimatedValue());
            }
        });
        swing.start();
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private LayoutInflater mInflater;
        private DateFormat dateFormat;

        ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.activity_fly_refresh_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
            final ItemData data = mDataSet.get(i);
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);
            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(dateFormat.format(data.time));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        TextView subTitle;

        ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
        }

    }

    public class ItemData {
        int color;
        int icon;
        Date time;
        public String title;

        ItemData(int color, int icon, String title, Date time) {
            this.color = color;
            this.icon = icon;
            this.title = title;
            this.time = time;
        }
    }

    public class SampleItemAnimator extends BaseItemAnimator {

        @Override
        protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
            View icon = holder.itemView.findViewById(R.id.icon);
            icon.setRotationX(30);
            View right = holder.itemView.findViewById(R.id.right);
            right.setPivotX(0);
            right.setPivotY(0);
            right.setRotationY(90);
        }

        @Override
        protected void animateRemoveImpl(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
            View target = holder.itemView;
            View icon = target.findViewById(R.id.icon);
            Animator swing = ObjectAnimator.ofFloat(icon, "rotationX", 45, 0);
            swing.setInterpolator(new OvershootInterpolator(5));

            View right = holder.itemView.findViewById(R.id.right);
            Animator rotateIn = ObjectAnimator.ofFloat(right, "rotationY", 90, 0);
            rotateIn.setInterpolator(new DecelerateInterpolator());

            AnimatorSet animator = new AnimatorSet();
            animator.setDuration(getAddDuration());
            animator.playTogether(swing, rotateIn);

            animator.start();
        }

    }

    public class ElasticOutInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float t) {
            if (t == 0) return 0;
            if (t >= 1) return 1;
            float p=.3f;
            float s=p/4;
            return ((float)Math.pow(2,-10*t) * (float)Math.sin( (t-s)*(2*(float)Math.PI)/p) + 1);
        }
    }

}
