package com.scwang.refreshlayout.fragment.index;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.scwang.refreshlayout.R.id.recyclerView;

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
import com.scwang.refreshlayout.activity.style.BezierCircleStyleActivity;
import com.scwang.refreshlayout.activity.style.BezierRadarStyleActivity;
import com.scwang.refreshlayout.activity.style.ClassicsStyleActivity;
import com.scwang.refreshlayout.activity.style.DeliveryStyleActivity;
import com.scwang.refreshlayout.activity.style.DropBoxStyleActivity;
import com.scwang.refreshlayout.activity.style.FlyRefreshStyleActivity;
import com.scwang.refreshlayout.activity.style.FunGameBattleCityStyleActivity;
import com.scwang.refreshlayout.activity.style.FunGameHitBlockStyleActivity;
import com.scwang.refreshlayout.activity.style.MaterialStyleActivity;
import com.scwang.refreshlayout.activity.style.PhoenixStyleActivity;
import com.scwang.refreshlayout.activity.style.StoreHouseStyleActivity;
import com.scwang.refreshlayout.activity.style.TaurusStyleActivity;
import com.scwang.refreshlayout.activity.style.WaterDropStyleActivity;
import com.scwang.refreshlayout.activity.style.WaveSwipeStyleActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.util.StatusBarUtil;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.BezierCircleHeader;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.header.DeliveryHeader;
import com.scwang.smart.refresh.header.DropBoxHeader;
import com.scwang.smart.refresh.header.FunGameHitBlockHeader;
import com.scwang.smart.refresh.header.PhoenixHeader;
import com.scwang.smart.refresh.header.TaurusHeader;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smart.refresh.layout.wrapper.RefreshFooterWrapper;
import com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper;

import java.util.Arrays;

/**
 * 风格展示
 * A simple {@link Fragment} subclass.
 */
public class RefreshStylesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private enum Item {
        Hidden(R.string.title_activity_style_delivery,DeliveryStyleActivity.class),
        Delivery(R.string.title_activity_style_delivery,DeliveryStyleActivity.class),
        DropBox(R.string.title_activity_style_drop_box, DropBoxStyleActivity.class),
        WaveSwipe(R.string.title_activity_style_wave_swipe, WaveSwipeStyleActivity.class),
        FlyRefresh(R.string.title_activity_style_fly_refresh, FlyRefreshStyleActivity.class),
        WaterDrop(R.string.title_activity_style_water_drop, WaterDropStyleActivity.class),
        Material(R.string.title_activity_style_material, MaterialStyleActivity.class),
        Phoenix(R.string.title_activity_style_phoenix, PhoenixStyleActivity.class),
        Taurus(R.string.title_activity_style_taurus, TaurusStyleActivity.class),
        Bezier(R.string.title_activity_style_bezier, BezierRadarStyleActivity.class),
        Circle(R.string.title_activity_style_circle, BezierCircleStyleActivity.class),
        FunGameHitBlock(R.string.title_activity_style_hit_block, FunGameHitBlockStyleActivity.class),
        FunGameBattleCity(R.string.title_activity_style_battle_city, FunGameBattleCityStyleActivity.class),
        StoreHouse(R.string.title_activity_style_storehouse, StoreHouseStyleActivity.class),
        Classics(R.string.title_activity_style_classics, ClassicsStyleActivity.class),
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
        return inflater.inflate(R.layout.fragment_refresh_styles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        StatusBarUtil.setPaddingSmart(getContext(), root.findViewById(R.id.toolbar));

        View view = root.findViewById(recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(Arrays.asList(Item.values()), simple_list_item_2,this) {
                @NonNull
                @Override
                public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    SmartViewHolder holder = super.onCreateViewHolder(parent, viewType);
                    if (viewType == 0) {
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                    }
                    return holder;
                }

                @Override
                public int getViewTypeCount() {
                    return 2;
                }

                @Override
                public int getItemViewType(int position) {
                    return position == 0 ? 0 : 1;
                }

                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.nameId);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }


        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        if (refreshLayout != null) {
            refreshLayout.setOnMultiListener(new SimpleMultiListener() {
                @Override
                public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                    refreshLayout.finishRefresh(3000);
                }
                @Override
                public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                    refreshLayout.finishLoadMore(2000);
                }
                @Override
                public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                    if (newState == RefreshState.None) {
                        if (oldState == RefreshState.LoadFinish) {
                            RefreshFooter refreshFooter = refreshLayout.getRefreshFooter();
                            if (refreshFooter instanceof RefreshFooterWrapper) {
                                View footerView = refreshFooter.getView();
                                if (footerView instanceof TaurusHeader) {
                                    refreshLayout.setRefreshFooter(new RefreshFooterWrapper(new DropBoxHeader(getContext())));
                                } else if (footerView instanceof DropBoxHeader) {
                                    refreshLayout.setRefreshFooter(new RefreshFooterWrapper(new DeliveryHeader(getContext())));
                                } else if (footerView instanceof DeliveryHeader) {
                                    refreshLayout.setRefreshFooter(new RefreshFooterWrapper(new BezierCircleHeader(getContext())));
                                } else {
                                    refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()));
                                }
                            }
                        } else if (oldState == RefreshState.RefreshFinish) {
                            RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
                            if (refreshHeader instanceof RefreshHeaderWrapper) {
                                refreshLayout.setRefreshHeader(new PhoenixHeader(getContext()), ViewGroup.LayoutParams.MATCH_PARENT, SmartUtil.dp2px(100));
                            } else if (refreshHeader instanceof PhoenixHeader) {
                                refreshLayout.setRefreshHeader(new DropBoxHeader(getContext()), ViewGroup.LayoutParams.MATCH_PARENT, SmartUtil.dp2px(150));
                            } else if (refreshHeader instanceof DropBoxHeader) {
                                refreshLayout.setRefreshHeader(new FunGameHitBlockHeader(getContext()));
                            } else if (refreshHeader instanceof FunGameHitBlockHeader) {
                                refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
                            } else {
                                refreshLayout.setRefreshHeader(new RefreshHeaderWrapper(new BallPulseFooter(getContext())));
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getContext(), Item.values()[position].clazz));
    }
}
