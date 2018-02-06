# 更新日志

## V 1.1.0 (开发中)
>添加：类似淘宝二楼的二级下拉刷新 TwoLevelHeader  
>添加：srlEnableScrollContentWhenRefreshed 属性和对应方法  
>添加：srlEnableClipHeaderWhenFixedBehind  属性和对应方法  
>添加：srlEnableClipFooterWhenFixedBehind  属性和对应方法  
>添加：srlHeaderInsetStart 属性和对应方法  
>添加：srlFooterInsetStart 属性和对应方法  
>添加：setNoMoreData(boolean) 方法  
>优化：优化越界回弹的效果  
>优化：优化Header和Footer与列表的惯性连续  
>解决：去掉XML预览功能对 Design 兼容包的依赖  
>修复：仿苹果越界拖动在特定条件下不连续问题  
>修复：finishLoadMoreWithNoMoreData 显示顺序错乱问题  
>修复：老版本种使用错误的单词 creator loadMore  
>修复：Xml预览模式在没有Header和Footer时的显示问题  
>修复：AppbarLayout 嵌套滚动时的bug  

## V 1.0.4
>添加：finishLoadMoreWithNoMoreData 方法 完成加载并标记没有更多数据  
>添加：resetNoMoreData 方法 恢复没有更多数据的原始状态  
>添加：setRefreshContent 方法 设置刷新Content（用于动态替换空布局）  
>添加：srlHeaderTriggerRate 属性和对应的set方法 设置 Header触发刷新距离 与 HeaderHeight 的比率（默认1）  
>添加：srlFooterTriggerRate 属性和对应的set方法 设置 Footer触发加载距离 与 FooterHeight 的比率（默认1）  
>添加：srlEnableOverScrollDrag 属性和对应的set方法 设置 是否启用越界拖动（仿苹果效果）  
>添加：srlEnableFooterFollowWhenLoadFinished 属性和对应的set方法 设置 是否在全部加载结束之后Footer跟随内容  
>添加：refreshHeader 添加 setLastUpdateText 方法 手动设置更新时间  
>添加：refreshHeader 添加 onRefreshReleased 方法 手势释放时调用  
>添加：refreshFooter 添加 onLoadMoreReleased 方法 手势释放时调用
>修改：修改Header 名称 Circle 为 BezierCircle  
>修改：改变 onStartAnimator 的调用时机为 释放之后会回弹到标准高度调用  
>修改：srlEnableLoadMoreWhenContentNotFull 的默认值 由 false 改成 true  


## V 1.0.3
>添加：下拉和上拉时，支持多点触摸，手势不冲突  
>添加：当 内容视图不满一页时，默认不能上拉加载更多，不过必要时，通过设置还是可以上拉的  
>添加：为 Header和Footer添加拖动时水平方向坐标 x，实现左右拖动Header的效果  
>添加：为 RefreshLayout 添加多点触摸支持，在多个手指触摸式不会发生冲突，并且随意拖动  
>添加：为 RefreshLayout 添加 EnableLoadMoreWhenContentNotFull 功能  
>添加：为 RefreshLayout 添加 srlEnabledNestedScroll 属性 和 srlEnabledNestedScroll 方法  
>添加：为 RefreshHeader 添加 srlEnableHorizontalDrag 属性 和 setEnableHorizontalDrag 方法  
>添加：为 ClassicsHeader 添加 srlEnableLastTime 属性 和 setEnableLastTime 方法 控制时间显示  
>添加：为 ClassicsHeader 添加 srlDrawableArrow 属性 和 setArrowResource 方法 改变箭头图片  
>添加：为 ClassicsHeader 添加 srlProgressDrawable 属性 和 setProgressResource 改变转动图片  
>添加：为 ClassicsHeader 添加 srlTextSizeTime 属性 和 setTextSizeTime 方法 设置字体大小  
>添加：为 ClassicsHeader 添加 srlTextTimeMarginTop 属性 时间文字的上间距  
>添加：为 ClassicsFooter 添加 srlDrawableArrow 属性 和 setArrowResource 方法 改变箭头图片  
>添加：为 ClassicsFooter 添加 srlDrawableProgress 属性 和 setProgressResource 改变转动图片  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlFinishDuration 属性 和 setFinishDuration 方法  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlDrawableMarginRight 属性 设置图片间距  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlTextSizeTitle 属性 和 setTextSizeTitle 方法  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlDrawableSize 属性 和 setDrawableSize 方法  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlDrawableArrowSize 属性 和 setDrawableArrowSize 方法  
>添加：为 ClassicsHeader 和 ClassicsFooter 添加 srlDrawableProgressSize 属性 和 setDrawableProgressSize 方法  
>修改：改 EnableLoadMore 由默认true->变为false，并增加智能开启功能  
>修改：改 EnableNestedScrolling 由默认true->变为false，并增加智能开启功能  
>修复：在 ClassicsFooter 加载失败时，显示成了加载完成的错误  
>修复：正在刷新时，向上拖动导致的栈溢出崩溃  
>修复：autoRefresh(0) 的 延时为没有延时  
>修复：StaggeredGridLayoutManager 导致的 autoLoadMore 无效
>修复：列表监听滚动无效的问题  
>修复：内存泄漏问题  
>修复：隐式去除对 support-design 支持包的依赖  

## V 1.0.2
>添加：AbsListView 和 RecyclerView 的越界回弹  
>添加：srlFixedHeaderViewId 属性，指定固定的视图Id  
>添加：srlFixedFooterViewId 属性，指定固定的视图Id  
>添加：srlEnablePureScrollMode 属性，是否开启纯滚动模式  
>添加：srlEnableNestedScrolling 属性，是否开启嵌套滚动NestedScrolling  
>添加：srlEnableScrollContentWhenLoaded 属性，是否在加载完成之后滚动内容显示新数据  
>添加：setScrollBoundaryDecider 方法，设置滚动边界判断  
>添加：finishRefresh(boolean success);方法，完成刷新,并设置是否成功  
>添加：finishLoadMore(boolean success);方法，完成加载,并设置是否成功
>修复：DeliveryHeader,DropboxHeader 在API-17以下显示不全的问题  

## V 1.0.1
>添加：srlEnableAutoLoadMore 属性  
>修改：srlExtendHeaderRate 属性为：srlHeaderMaxDragRate  
>修改：srlExtendFooterRate 属性为：srlFooterMaxDragRate  
>修复：DeliveryHeader,DropboxHeader 在API-21以下崩溃的问题  

## V 1.0.0
>添加：DeliveryHeader,DropboxHeader  
