package com.scwang.refreshlayout.fragment.practice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.util.StatusBarUtil;
import com.scwang.refreshlayout.widget.TwoLevelHeader;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/**
 * 淘宝二楼
 * Created by SCWANG on 2017/12/4.
 */

public class SecondFloorPracticeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_secondfloor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final View floor = root.findViewById(R.id.secondfloor);
        final Toolbar toolbar = (Toolbar)root.findViewById(R.id.toolbar);
        final TwoLevelHeader header = (TwoLevelHeader)root.findViewById(R.id.header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        final RefreshLayout refreshLayout = (RefreshLayout)root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(@NonNull RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                toolbar.setAlpha(1 - Math.min(percent, 1));
                floor.setTranslationY(Math.min(offset - floor.getHeight() + toolbar.getHeight(), refreshLayout.getLayout().getHeight() - floor.getHeight()));
            }
            @Override
            public void onHeaderReleasing(@NonNull RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                toolbar.setAlpha(1 - Math.min(percent, 1));
                floor.setTranslationY(Math.min(offset - floor.getHeight() + toolbar.getHeight(), refreshLayout.getLayout().getHeight() - floor.getHeight()));
            }
        });

        header.setOnTwoLevelListener(new TwoLevelHeader.OnTwoLevelListener() {
            @Override
            public boolean onTwoLevel(RefreshLayout refreshLayout) {
                Toast.makeText(getContext(),"触发二楼事件",Toast.LENGTH_SHORT).show();
                root.findViewById(R.id.secondfloor_content).animate().alpha(1).setDuration(2000);
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        header.finishTwoLevel();
                        root.findViewById(R.id.secondfloor_content).animate().alpha(0).setDuration(1000);
                    }
                },5000);
                return true;//true 将会展开二楼状态 false 关闭刷新
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Toast.makeText(getContext(),"触发刷新事件",Toast.LENGTH_SHORT).show();
                refreshLayout.finishRefresh(2000);
            }
        });

        //状态栏透明和间距处理
        StatusBarUtil.immersive(getActivity());
        StatusBarUtil.setMargin(getActivity(),  root.findViewById(R.id.classics));
        StatusBarUtil.setPaddingSmart(getActivity(), root.findViewById(R.id.toolbar));
        StatusBarUtil.setPaddingSmart(getActivity(), root.findViewById(R.id.contentPanel));
    }
}
