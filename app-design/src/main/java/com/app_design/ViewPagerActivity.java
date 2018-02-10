package com.app_design;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.support.v4.view.ViewCompat.OVER_SCROLL_NEVER;

public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            Fragment[] fragments = new Fragment[20];

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public Fragment getItem(int position) {
                if (fragments[position] == null) {
                    fragments[position] = new TabFragment();
                }
                return fragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return String.format(Locale.CHINA, "第%d页", position + 1);
            }
        });

        TabLayout tableLayout = (TabLayout) findViewById(R.id.tabLayout);
        tableLayout.setupWithViewPager(viewPager);

    }

    public static class TabFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private RefreshLayout mRefreshLayout;
        private TabListAdapter<Void> mAdapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (mRefreshLayout == null) {
                RefreshLayout refreshLayout = new SmartRefreshLayout(inflater.getContext());
                refreshLayout.setRefreshHeader(new ClassicsHeader(inflater.getContext()));
                refreshLayout.setRefreshFooter(new ClassicsFooter(inflater.getContext()));
                refreshLayout.setRefreshContent(mRecyclerView = new RecyclerView(inflater.getContext()));
                refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                refreshLayout.setDragRate(1);
                refreshLayout.setHeaderMaxDragRate(1.2f);
                refreshLayout.setEnableNestedScroll(false);
                mRefreshLayout = refreshLayout;
                mRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
                mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
            }
            return mRefreshLayout.getLayout();
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshLayout) {
                    mAdapter.refresh(initData());
                    refreshLayout.finishRefresh(2000);
                }
            });
            mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshLayout) {
                    mAdapter.loadMore(initData());
                    refreshLayout.finishLoadMore(2000);
                }
            });
            if (mAdapter == null) {
                mRecyclerView.setAdapter(mAdapter = new TabListAdapter<>());
                mRefreshLayout.autoRefresh();
            }
        }

        private List<Void> initData() {
            return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        }
    }

    static class TabListAdapter<T> extends RecyclerView.Adapter {

        List<T> list = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2,parent,false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView1 = (TextView) holder.itemView.findViewById(android.R.id.text1);
            TextView textView2 = (TextView) holder.itemView.findViewById(android.R.id.text2);
            textView1.setText(String.format(Locale.CHINA, "第%d条数据", position + 1));
            textView2.setText(String.format(Locale.CHINA, "这是第%d条数据测试内容", position + 1));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        void refresh(List<T> list) {
            this.list.clear();
            this.loadMore(list);
        }

        void loadMore(List<T> list) {
            this.list.addAll(list);
            this.notifyDataSetChanged();
        }
    }
}
