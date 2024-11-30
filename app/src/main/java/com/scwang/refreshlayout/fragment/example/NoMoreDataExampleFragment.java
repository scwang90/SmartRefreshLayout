package com.scwang.refreshlayout.fragment.example;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoMoreDataExampleFragment extends Fragment {

    private BaseRecyclerAdapter<Void> mAdapter;

    public NoMoreDataExampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_more_data_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getActivity().finish());

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
            recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                    holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                    holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        if (refreshLayout != null) {

            ArrayList<View> views = new ArrayList<>(1);
            toolbar.findViewsWithText(views, toolbar.getTitle(), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION|View.FIND_VIEWS_WITH_TEXT);
            if (views.size() > 0) {
                views.get(0).setOnClickListener(v-> refreshLayout.autoRefresh());
            }

            refreshLayout.autoRefresh();
            refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
            refreshLayout.setOnRefreshListener((OnRefreshListener) refreshLayout1 -> refreshLayout1.getLayout().postDelayed((Runnable) () -> {
                Collection<Void> data = loadData();
                mAdapter.refresh(data);
                if (data.size() < 9) {
                    refreshLayout1.finishRefreshWithNoMoreData();
                } else {
                    refreshLayout1.finishRefresh();
                }
            }, 1000));
            refreshLayout.setOnLoadMoreListener((OnLoadMoreListener) refreshLayout12 -> refreshLayout12.getLayout().postDelayed((Runnable) () -> {
                Collection<Void> list = loadData();
                mAdapter.loadMore(list);
                if (list.size() < 10) {
                    refreshLayout12.finishLoadMoreWithNoMoreData();
                } else {
                    refreshLayout12.finishLoadMore();
                }
            }, 1000));
        }
    }

    private final Random random = new Random();

    private Collection<Void> loadData() {
        int count = 3 + random.nextInt(10);
        List<Void> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(null);
        }
        return list;
    }
}
