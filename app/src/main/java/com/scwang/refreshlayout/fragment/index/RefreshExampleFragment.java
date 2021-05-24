package com.scwang.refreshlayout.fragment.index;


import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.activity.FragmentActivity;
import com.scwang.refreshlayout.activity.example.BasicExampleActivity;
import com.scwang.refreshlayout.activity.example.CustomExampleActivity;
import com.scwang.refreshlayout.activity.example.I18nExampleActivity;
import com.scwang.refreshlayout.activity.example.ListenerExampleActivity;
import com.scwang.refreshlayout.activity.example.NestedLayoutExampleActivity;
import com.scwang.refreshlayout.activity.example.SnapHelperExampleActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.fragment.example.BottomSheetExampleFragment;
import com.scwang.refreshlayout.fragment.example.DisallowInterceptExampleFragment;
import com.scwang.refreshlayout.fragment.example.EmptyLayoutExampleFragment;
import com.scwang.refreshlayout.fragment.example.FlexBoxLayoutManagerFragment;
import com.scwang.refreshlayout.fragment.example.HorizontalExampleFragment;
import com.scwang.refreshlayout.fragment.example.NestedScrollExampleFragment;
import com.scwang.refreshlayout.fragment.example.NoMoreDataExampleFragment;
import com.scwang.refreshlayout.fragment.example.PureScrollExampleFragment;
import com.scwang.refreshlayout.fragment.example.SpecifyStyleExampleFragment;
import com.scwang.refreshlayout.fragment.example.StaggeredGridExampleFragment;
import com.scwang.refreshlayout.fragment.example.ViewPagerExampleFragment;
import com.scwang.refreshlayout.util.StatusBarUtil;

import java.util.Arrays;

/**
 * 使用示例
 * A simple {@link Fragment} subclass.
 */
public class RefreshExampleFragment extends Fragment implements AdapterView.OnItemClickListener {

    private enum Item {
        Basic(R.string.index_example_basic, BasicExampleActivity.class),
        NoMoreData(R.string.index_example_style, NoMoreDataExampleFragment.class),
        SpecifyStyle(R.string.index_example_style, SpecifyStyleExampleFragment.class),
        EmptyLayout(R.string.index_example_empty, EmptyLayoutExampleFragment.class),
        NestedLayout(R.string.index_example_layout, NestedLayoutExampleActivity.class),
        NestedScroll(R.string.index_example_nested, NestedScrollExampleFragment.class),
        PureScroll(R.string.index_example_scroll, PureScrollExampleFragment.class),
        Listener(R.string.index_example_listener, ListenerExampleActivity.class),
        Custom(R.string.index_example_custom, CustomExampleActivity.class),
        I18N(R.string.index_example_custom, I18nExampleActivity.class),
        SnapHelper(R.string.index_example_snap_helper, SnapHelperExampleActivity.class),
        ViewPager(R.string.index_example_pager, ViewPagerExampleFragment.class),
        BottomSheet(R.string.index_example_bottom_sheet,BottomSheetExampleFragment.class),
        FlexBoxLayout(R.string.index_example_flex_box, FlexBoxLayoutManagerFragment.class),
        Horizontal(R.string.index_example_horizontal, HorizontalExampleFragment.class),
        DisallowIntercept(R.string.index_example_disallow_intercept, DisallowInterceptExampleFragment.class),
        StaggeredGrid(R.string.index_example_disallow_intercept, StaggeredGridExampleFragment.class),
//        ThreeLevel(R.string.index_example_level, ThreeLevelExampleFragment.class),
        ;
        public int nameId;
        public Class<?> clazz;

        Item(@StringRes int nameId, Class<?> clazz) {
            this.nameId = nameId;
            this.clazz = clazz;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        StatusBarUtil.setPaddingSmart(getContext(), root.findViewById(R.id.toolbar));

        View view = root.findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(Arrays.asList(Item.values()), simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, getString(model.nameId));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = Item.values()[position];
        if (Activity.class.isAssignableFrom(item.clazz)) {
            startActivity(new Intent(getContext(), item.clazz));
        } else if (Fragment.class.isAssignableFrom(item.clazz)) {
            FragmentActivity.start(this, item.clazz);
        }
    }
}
