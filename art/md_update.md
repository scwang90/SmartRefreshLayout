# 更新日志

## V 2.0.0 (开发中)
>添加：srlStyle 主题支持  
>分包：layout = kernel + two-level + header-classics + header-radar + header-falsify + footer-classics + footer-ball  
>修改：方法 OnMultiPurposeListener -> setOnMultiListener
>修改：包名 com.scwang.smartrefresh -> com.scwang.smart.refresh  
>修改：类名 smartrefresh.layout.listener.OnMultiPurposeListener -> smart.refresh.layout.listener.OnMultiListener  
>修改：类名 smartrefresh.layout.listener.SimpleMultiPurposeListener -> smart.refresh.layout.simple.SimpleMultiListener
>添加：属性 srlBottomPullUpToCloseRate setBottomPullUpToCloseRate
>回归：回归部分被去掉的API如：isRefreshing isLoading  

## V 1.1.0

>添加：finishRefreshWithNoMoreData 方法  
>添加：DefaultRefreshInitializer 全局初始化  
>添加：srlHeaderTranslationViewId 属性，指定下拉Header时偏移的视图Id  
>添加：srlFooterTranslationViewId 属性，指定上拉Footer时偏移的视图Id  
>添加：setDefaultRefreshInitializer 方法，采用优先级最低的配置全局设置  
>添加：closeHeaderOrFooter 方法，可以关闭正在打开的Header或者Footer  
>添加：autoLoadMoreAnimationOnly 方法，只显示动画不执行加载  
>添加：autoRefreshAnimationOnly 方法，只显示动画不执行刷新  
>添加：TwoLevelHeader.openTwoLevel 方法，主动打开二楼  
>添加：水平滚动刷新支持，demo版本  
>添加：对 ViewPager2，MotionLayout，BottomSheet，AndroidX 的兼容支持  
>添加：MaterialHeader 添加圆盘背景颜色修改支持  
>集成：类似淘宝二楼的二级下拉刷新 TwoLevelHeader    
>删除：部分冗余的API接口  
>删除：1.0.5 中标记过时的API接口  
>优化：淘宝二楼展开中途可以被拦截的问题  
>优化：极度优化算法，使得dex-method从1366降低到788  
>精简：极度精简代码，是的dex-size从139kb降低到121kb  
>兼容：修改算法使得可以在BottomSheetDialog内部使用  
>修改：把仿苹果越界拖动功能默认为关闭，需要手动打开  
>修改：部分Header的命名修改为严格骆驼峰  
>修改：标记 Scale 样式过时，原因是 Scale 再拖动是会不停【测量】（header）和 【布局】（layout）性能低下  
>修改：将之前自带Header中 Scale 样式全部采用 FixedBehind 代替，用户如需替换可以参考这些demo  
>修复：修复聊天下拉加载没有惯性问题自动加载问题  
>修复：BallPulseFooter在Xml初始化颜色无效问题  
>修复：EnableLoadMoreWhenContentNotFull=false导致无法加载的问题  
>修复：onDetachedFromWindow 报NPE错误问题  

## V 1.0.5 (1.1.0过度版)
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
