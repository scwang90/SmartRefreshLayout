# 更新日志

## V 1.0.4 (开发中)  
>添加：类似淘宝二楼的二级下拉刷新  
>添加：srlEnableBounceShowHeaderAndFooter 属性和对应的set方法，控制越界回弹时候是否显示Header和Footer  

## V 1.0.3
>添加：下拉和上拉时，支持多点触摸，手势不冲突  
>添加：当 内容视图不满一页时，默认不能上拉加载更多，不过必要时，通过设置还是可以上拉的  
>添加：为 Heaer和Footer添加拖动时水平方向坐标 x，实现左右拖动Header的效果  
>添加：为 Refreshlayout 添加多点触摸支持，在多个手指触摸式不会发生冲突，并且随意拖动  
>添加：为 Refreshlayout 添加 EnableLoadmoreWhenContentNotFull 功能  
>添加：为 Refreshlayout 添加 srlEnabledNestedScroll 属性 和 srlEnabledNestedScroll 方法  
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
>修复：StaggeredGridLayoutManager 导致的 autoLoadmore 无效  
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
>添加：finishLoadmore(boolean success);方法，完成加载,并设置是否成功  
>修复：DeliveryHeader,DropboxHeader 在API-17以下显示不全的问题  

## V 1.0.1
>添加：srlEnableAutoLoadmore 属性  
>修改：srlExtendHeaderRate 属性为：srlHeaderMaxDragRate  
>修改：srlExtendFooterRate 属性为：srlFooterMaxDragRate  
>修复：DeliveryHeader,DropboxHeader 在API-21以下崩溃的问题  

## V 1.0.0
>添加：DeliveryHeader,DropboxHeader  
