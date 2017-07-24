# 更新日志

## V 1.0.0
>添加：DeliveryHeader,DropboxHeader  

## V 1.0.1
>添加：srlEnableAutoLoadmore 属性  
>修改：srlExtendHeaderRate 属性为：srlHeaderMaxDragRate  
>修改：srlExtendFooterRate 属性为：srlFooterMaxDragRate  
>修复：DeliveryHeader,DropboxHeader 在API-21以下崩溃的问题  

## V 1.0.2
>添加：AbsListView 和 RecyclerView 的越界回弹  
>添加：srlFixedHeaderViewId 属性，指定固定的视图Id  
>添加：srlFixedFooterViewId 属性，指定固定的视图Id  
>添加：srlEnablePureScrollMode 属性，是否开启纯滚动模式  
>添加：srlEnableNestedScrolling 属性，是否开启嵌套滚动NestedScrolling  
>添加：srlEnableScrollContentWhenLoaded 属性，是否在加载完成之后滚动内容显示新数据  
>添加：setRefreshScrollBoundary 方法，设置滚动边界判断  
>添加：finishRefresh(boolean success);方法，完成刷新,并设置是否成功  
>添加：finishLoadmore(boolean success);方法，完成加载,并设置是否成功  
>修复：DeliveryHeader,DropboxHeader 在API-17以下显示不全的问题  

## V 1.0.3(开发中)
>添加: 为Heaer和Footer添加拖动时水平方向坐标 x，实现左右拖动Header的效果  
>添加：为Header添加 srlEnableHorizontalDrag 属性和 setEnableHorizontalDrag 方法
>添加：为ClassicsHeader 添加 setArrowResource 方法改变箭头图片  
>添加：为ClassicsHeader 添加 setProgressResource 方法改变转动图片  
>添加：为ClassicsHeader 添加 srlArrowDrawable 属性改变箭头图片  
>添加：为ClassicsHeader 添加 srlProgressDrawable 属性改变转动图片  
>修复：为ClassicsFooter加载失败时，显示成了加载完成  
>修复：正在刷新时，向上拖动导致的栈溢出崩溃  
>修复：autoRefresh(0) 的 延时为没有延时  
>修复：StaggeredGridLayoutManager 导致的 autoLoadmore 无效  
>修复：列表监听滚动无效的问题  

 

