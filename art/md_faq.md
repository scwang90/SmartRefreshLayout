# 常见问题

## 1.获取当前状态？isRefreshing(),isLoading() 不见了？

版本的迭代，刷新的状态越来越多，仅仅由 isRefreshing(),isLoading()  已经无法满足要求，再1.0.5版本之后本库直
接将 内部 State 开放出来，并标记 isRefreshing(),isLoading() 过期，鼓励大家使用 getState 来代替。getState
比前两个方法更有用，具体参考下面代码。
~~~java
    refreshLayout.getState() == RefreshState.None //空闲状态
    refreshLayout.getState() == RefreshState.Loading//代替 isLoading
    refreshLayout.getState() == RefreshState.Refreshing//代替 isRefreshing

    refreshLayout.getState().isDragging //判断是否正在拖拽刷新控件（非拖拽列表）
    refreshLayout.getState().isFinishing //判断动画是否正在结束
    refreshLayout.getState().isHeader //判断当前是否处于 Header 的一系列状态中
    refreshLayout.getState().isFooter //判断当前是否处于 Footer 的一系列状态中
    refreshLayout.getState().isOpening // 等同于 isLoading || isRefreshing
~~~

## 2.嵌套WebView，还没滚动到顶部就开始下拉刷新了？

WebView 的问题多由内部Html中采用了绝对坐标导致的，所以问题很难从java层面区解决这个问题，对于这个问题，我建议
直接再Html内部实现下拉刷新，或者采用自定义滚动边界，参考#

