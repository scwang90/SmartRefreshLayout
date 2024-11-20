package com.scwang.refresh.layout.fragment.example;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.refresh.layout.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalExampleFragment extends Fragment {

    private BaseRecyclerAdapter<Void> mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example_horizontal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        requireActivity().getWindow().setStatusBarColor(0);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(simple_list_item_2) {

            final int[] colorIds = {
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark,
                    android.R.color.holo_orange_dark,
            };

            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                holder.itemView.getLayoutParams().width = -2;//SmartUtil.dp2px(100);
                holder.itemView.getLayoutParams().height = -1;
                holder.itemView.setBackgroundResource(colorIds[position%colorIds.length]);
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.textColorId(android.R.id.text1, android.R.color.white);

            }
        });

        mAdapter.refresh(initData());

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(() -> {
                    mAdapter.refresh(initData());
                    refreshLayout.finishRefresh();
                }, 2000);
            }

            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                recyclerView.stopScroll();
                recyclerView.stopNestedScroll();
                refreshLayout.getLayout().postDelayed(() -> {
                    mAdapter.loadMore(initData());
                    refreshLayout.finishLoadMore();
                }, 2000);
            }
        });
    }

    private Collection<Void> initData() {
        return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
}
