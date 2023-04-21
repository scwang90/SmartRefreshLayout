package com.scwang.smartrefresh.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 智能刷新布局
 * Intelligent RefreshLayout
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
@SuppressWarnings({"unused"})
public class SmartRefreshLayout extends com.scwang.smart.refresh.layout.SmartRefreshLayout implements RefreshLayout {

    //<editor-fold desc="构造方法 construction methods">
    public SmartRefreshLayout(Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>

    //<editor-fold desc="开放接口 open interface">

    /**
     * Set the header of RefreshLayout.
     * 设置指定的 Header
     * @param header RefreshHeader 刷新头
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header) {
        return setRefreshHeader(header, MATCH_PARENT, WRAP_CONTENT);
    }

    /**
     * Set the header of RefreshLayout.
     * 设置指定的 Header
     * @param header RefreshHeader 刷新头
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height) {
        super.setRefreshHeader(header, width, height);
        return this;
    }

    /**
     * Set the footer of RefreshLayout.
     * 设置指定的 Footer
     * @param footer RefreshFooter 刷新尾巴
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer) {
        return setRefreshFooter(footer, MATCH_PARENT, WRAP_CONTENT);
    }

    /**
     * Set the footer of RefreshLayout.
     * 设置指定的 Footer
     * @param footer RefreshFooter 刷新尾巴
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height) {
        super.setRefreshFooter(footer, width, height);
        return this;
    }

    /**
     * Get footer of RefreshLayout
     * 获取当前 Footer
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshFooter getRefreshFooter() {
        return mRefreshFooter instanceof RefreshFooter ? (RefreshFooter) mRefreshFooter : null;
    }

    /**
     * Get header of RefreshLayout
     * 获取当前 Header
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshHeader getRefreshHeader() {
        return mRefreshHeader instanceof RefreshHeader ? (RefreshHeader) mRefreshHeader : null;
    }

//    /**
//     * Get the current state of RefreshLayout
//     * 获取当前状态
//     * @return RefreshLayout
//     */
//    @NonNull
//    @Override
//    public RefreshState getState() {
//        return mState;
//    }


    /**
     * Set refresh listener separately.
     * 单独设置刷新监听器
     * @param listener OnRefreshListener 刷新监听器
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnRefreshListener(final OnRefreshListener listener) {
        super.setOnRefreshListener(new com.scwang.smart.refresh.layout.listener.OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
                listener.onRefresh(SmartRefreshLayout.this);
            }
        });
        return this;
    }

    /**
     * Set load more listener separately.
     * 单独设置加载监听器
     * @param listener OnLoadMoreListener 加载监听器
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnLoadMoreListener(final OnLoadMoreListener listener) {
        super.setOnLoadMoreListener(new com.scwang.smart.refresh.layout.listener.OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
                listener.onLoadMore(SmartRefreshLayout.this);
            }
        });
        return this;
    }

    /**
     * Set refresh and load listeners at the same time.
     * 同时设置刷新和加载监听器
     * @param listener OnRefreshLoadMoreListener 刷新加载监听器
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnRefreshLoadMoreListener(final OnRefreshLoadMoreListener listener) {
        super.setOnRefreshLoadMoreListener(new com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
                listener.onLoadMore(SmartRefreshLayout.this);
            }

            @Override
            public void onRefresh(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
                listener.onRefresh(SmartRefreshLayout.this);
            }
        });
        return this;
    }

    /**
     * Set up a multi-function listener.
     * Recommended {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * 设置多功能监听器
     * 建议使用 {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * @param listener OnMultiPurposeListener 多功能监听器
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener) {
        super.setOnMultiListener(new SimpleMultiPurposeListener(listener, this));
        return this;
    }

    /**
     * Set the scroll boundary Decider, Can customize when you can refresh.
     * Recommended {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * 设置滚动边界判断器
     * 建议使用 {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * @param boundary ScrollBoundaryDecider 判断器
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setScrollBoundaryDecider(final ScrollBoundaryDecider boundary) {
        super.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
                return boundary.canRefresh(content);
            }

            @Override
            public boolean canLoadMore(View content) {
                return boundary.canLoadMore(content);
            }
        });
        return this;
    }

    /**
     * 设置默认 Header 构建器
     * @param creator Header构建器
     */
    public static void setDefaultRefreshHeaderCreator(@NonNull final DefaultRefreshHeaderCreator creator) {
        com.scwang.smart.refresh.layout.SmartRefreshLayout.setDefaultRefreshHeaderCreator(new com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public com.scwang.smart.refresh.layout.api.RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull com.scwang.smart.refresh.layout.api.RefreshLayout layout) {
                if (layout instanceof RefreshLayout) {
                    return creator.createRefreshHeader(context, (RefreshLayout) layout);
                } else {
                    return new BezierRadarHeader(context);
                }
            }
        });
    }

    /**
     * 设置默认 Footer 构建器
     * @param creator Footer构建器
     */
    public static void setDefaultRefreshFooterCreator(@NonNull final DefaultRefreshFooterCreator creator) {
        com.scwang.smart.refresh.layout.SmartRefreshLayout.setDefaultRefreshFooterCreator(new com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public com.scwang.smart.refresh.layout.api.RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull com.scwang.smart.refresh.layout.api.RefreshLayout layout) {
                if (layout instanceof RefreshLayout) {
                    return creator.createRefreshFooter(context, (RefreshLayout) layout);
                } else {
                    return new BallPulseFooter(context);
                }
            }
        });
    }

    /**
     * 设置默认 Refresh 初始化器
     * @param initializer 全局初始化器
     */
    public static void setDefaultRefreshInitializer(@NonNull final DefaultRefreshInitializer initializer) {
        com.scwang.smart.refresh.layout.SmartRefreshLayout.setDefaultRefreshInitializer(new com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull com.scwang.smart.refresh.layout.api.RefreshLayout layout) {
                if (layout instanceof RefreshLayout) {
                    initializer.initialize(context, (RefreshLayout)layout);
                }
            }
        });
    }
    //</editor-fold>

}
