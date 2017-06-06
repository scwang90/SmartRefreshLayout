package com.scwang.refreshlayout;

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
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefreshheader.FlyRefreshHeader;
import com.scwang.smartrefreshheader.flyrefresh.FlyView;
import com.scwang.smartrefreshheader.flyrefresh.MountanScenceView;
import com.scwang.smartrefreshlayout.SmartRefreshLayout;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.api.RefreshLayout;
import com.scwang.smartrefreshlayout.listener.OnRefreshListener;
import com.scwang.smartrefreshlayout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

public class FlyRefreshActivity extends AppCompatActivity implements OnRefreshListener {

    private RecyclerView mListView;
    private SmartRefreshLayout mFlylayout;

    private ItemAdapter mAdapter;

    private FlyView mFlyView;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private FlyRefreshHeader mFlyRefreshHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_refresh);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDataSet();

        mAdapter = new ItemAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mFlyRefreshHeader = new FlyRefreshHeader(this);
        mFlyView = (FlyView) findViewById(R.id.flyview);
        mFlyRefreshHeader.setUp((MountanScenceView) findViewById(R.id.flyrefresh), mFlyView);

        mFlylayout = (SmartRefreshLayout) findViewById(R.id.smart);
        mFlylayout.setOnRefreshListener(this);
        mFlylayout.setRefreshHeader(mFlyRefreshHeader);
        mFlylayout.autoRefresh();
        mListView = (RecyclerView) findViewById(R.id.recycler);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new SampleItemAnimator());

        final CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            int index = 0;
            int[] ids = new int[]{
                    android.R.color.holo_green_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_blue_bright,
            };
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(getApplication(), ids[index % ids.length]);
                mFlylayout.autoRefresh();
                mFlylayout.setPrimaryColors(color);
                fab.setBackgroundColor(color);
                fab.setBackgroundTintList(ColorStateList.valueOf(color));
                layout.setContentScrimColor(color);
                index++;
            }
        });
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        mFlylayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                appBar.setTranslationY(offset);
            }
            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                appBar.setTranslationY(offset);
            }
        });
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    fab.animate().scaleX(0).scaleY(0);
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
                    fab.animate().scaleX(1).scaleY(1);
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
    }

    private void initDataSet() {
        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.drawable.ic_poll_white_24dp, "Meeting Minutes", new Date(2014 - 1900, 2, 9)));
        mDataSet.add(new ItemData(Color.GRAY, R.drawable.ic_folder_white_24dp, "Favorites Photos", new Date(2014 - 1900, 1, 3)));
        mDataSet.add(new ItemData(Color.GRAY, R.drawable.ic_folder_white_24dp, "Photos", new Date(2014 - 1900, 0, 9)));
    }

    private void addItemData() {
        ItemData itemData = new ItemData(Color.parseColor("#FFC970"), R.drawable.ic_smartphone_white_24dp, "Magic Cube Show", new Date());
        mDataSet.add(0, itemData);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        View child = mListView.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlyRefreshHeader.finishRefresh(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        addItemData();
                    }
                });
            }
        }, 2000);
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

        public ItemAdapter(Context context) {
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
        }

    }
    /**
     * Created by Jing on 15/5/27.
     */
    public class ItemData {
        int color;
        public int icon;
        public String title;
        public Date time;

        public ItemData(int color, int icon, String title, Date time) {
            this.color = color;
            this.icon = icon;
            this.title = title;
            this.time = time;
        }

        public ItemData(int icon, String title) {
            this(Color.DKGRAY, icon, title, new Date());
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
}
