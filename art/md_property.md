
## Attributes

|name|format|description|
|:---:|:---:|:---:|
|srlPrimaryColor|color|主题颜色|
|srlAccentColor|color|强调颜色|
|srlReboundDuration|integer|释放后回弹动画时长|
|srlHeaderHeight|dimension|Header的标准高度|
|srlFooterHeight|dimension|Footer的标准高度|
|srlDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|srlHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|srlFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|srlEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|srlEnableLoadmore|boolean|是否开启加上拉加载功能（默认true）|
|srlEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|srlEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|srlEnablePreviewInEditMode|boolean|是否在编辑模式时显示预览效果（默认true）|
|srlEnablePureScrollMode|boolean|是否开启纯滚动模式|
|srlEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling|
|srlEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据|
|srlDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|srlDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|srlFixedHeaderViewId|id|指定固定的视图Id|
|srlFixedFooterViewId|id|指定固定的视图Id|

## Method

|name|format|description|
|:---:|:---:|:---:|
|setPrimaryColors|colors|主题\强调颜色|
|setPrimaryColorsId|colors|主题\强调颜色资源Id|
|setReboundDuration|integer|释放后回弹动画时长|
|setHeaderHeight|dimension|Header的标准高度（px/dp 两个版本）|
|setFooterHeight|dimension|Footer的标准高度（px/dp 两个版本）|
|setDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|setHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|setFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|setEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|setEnableLoadmore|boolean|是否开启加上拉加载功能（默认true）|
|setEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|setEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|setEnableAutoLoadmore|boolean|是否监听列表滚动到底部时触发加载事件|
|setEnablePureScrollMode|boolean|是否开启纯滚动模式|
|setEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling|
|setEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据|
|setDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|setDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|setReboundInterpolator|Interpolator|设置回弹动画的插值器|
|setRefreshHeader|RefreshHeader|设置指定的Header|
|setRefreshFooter|RefreshFooter|设置指定的Footer|
|setOnRefreshListener|OnRefreshListener|设置刷新监听器|
|setOnLoadmoreListener|OnLoadmoreListener|设置加载监听器|
|setOnRefreshLoadmoreListener|OnRefreshLoadmoreListener|同时设置上面两个监听器|
|setOnMultiPurposeListener|OnMultiPurposeListener|设置多功能监听器|
|setLoadmoreFinished|boolean|设置全部数据加载完成，之后不会触发加载事件|
|setRefreshScrollBoundary|boundary|设置滚动边界判断|
|finishRefresh|(int delayed)|完成刷新，结束刷新动画|
|finishLoadmore|(int delayed)|完成加载，结束加载动画|
|finishRefresh|(boolean success)|完成刷新，并设置是否成功|
|finishLoadmore|(boolean success)|完成加载，并设置是否成功|
|getRefreshHeader|RefreshHeader|获取Header|
|getRefreshFooter|RefreshFooter|获取Footer|
|getState|RefreshState|获取当前状态|
|isRefreshing|boolean|是否正在刷新|
|isLoading|boolean|是否正在加载|
|autoRefresh|(int delayed)|触发自动刷新|
|autoLoadmore|(int delayed)|触发自动加载|

## Header-Attributes

|name|format|description|
|:---:|:---:|:---:|
|srlPrimaryColor|color|主题颜色|
|srlAccentColor|color|强调颜色|
|srlArrowDrawable|drawable|箭头图片|
|srlProgressDrawable|drawable|转动图片|
|srlClassicsSpinnerStyle|enum|变换样式：Translate(平行移动)、Scale（拉伸形变）、FixedBehind（固定在背后）|
|srlSpinnerStyle|enum|变换样式：srlClassicsSpinnerStyle的全部、FixedFront（固定在前面或全屏）|

## Header-Method
|name|format|description|
|:---:|:---:|:---:|
|setPrimaryColors|colors|设置主题\强调颜色|
|setArrowDrawable|drawable|设置箭头图片|
|setProgressDrawable|drawable|设置转动图片|
|setSpinnerStyle|enum|变换样式：参考属性srlSpinnerStyle|
|setClassicsSpinnerStyle|enum|变换样式：参考属性srlClassicsSpinnerStyle|
