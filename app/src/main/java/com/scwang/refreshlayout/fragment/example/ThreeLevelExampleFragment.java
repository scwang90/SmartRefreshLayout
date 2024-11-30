package com.scwang.refreshlayout.fragment.example;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_2;

/**
 * 三级刷新
 * A simple {@link Fragment} subclass.
 */
public class ThreeLevelExampleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example_three_level, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getActivity().finish());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setAdapter(new BaseRecyclerAdapter<Void>(initData(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                    holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                    holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                    holder.textColorId(android.R.id.text1, android.R.color.white);
                    holder.textColorId(android.R.id.text2, android.R.color.white);
                }
            });
        }

        RefreshLayout refreshLayout1 = view.findViewById(R.id.refreshLayout1);
        if (refreshLayout1 != null) {
            refreshLayout1.setOnRefreshListener((OnRefreshListener) refreshLayout -> refreshLayout.finishRefresh(1000));
        }
        RefreshLayout refreshLayout2 = view.findViewById(R.id.refreshLayout2);
        if (refreshLayout2 != null) {
            refreshLayout2.getLayout().setEnabled(false);
            refreshLayout2.setOnRefreshListener((OnRefreshListener) refreshLayout -> refreshLayout.finishRefresh(2000));
        }
    }

    private List<Void> initData() {
        return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
}
