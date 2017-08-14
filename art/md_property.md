# 代码示例

## SmartRefreshLayout
java代码设置
~~~java
public class RefreshActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //下面示例中的值等于默认值
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        refreshLayout.setDragRate(0.5f);//显示下拉高度/手指真实下拉高度=阻尼效果
        refreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）
        refreshLayout.setHeaderMaxDragRate(2);//最大显示下拉高度/Header标准高度
        refreshLayout.setFooterMaxDragRate(2);//最大显示下拉高度/Footer标准高度
        refreshLayout.setHeaderHeight(100);//Header标准高度（显示下拉高度>=标准高度 触发刷新）
        refreshLayout.setHeaderHeightPx(100);//同上-像素为单位
        refreshLayout.setFooterHeight(100);//Footer标准高度（显示上拉高度>=标准高度 触发加载）
        refreshLayout.setFooterHeightPx(100);//同上-像素为单位
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadmore(true);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadmore(true);//是否启用列表惯性滑动到底部时自动加载更多
        refreshLayout.setEnablePureScrollMode(false);//是否启用纯滚动模式
        refreshLayout.setEnableNestedScroll(false);//是否启用嵌套滚动
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableHeaderTranslationContent(true);//是否下拉Header的时候向下平移列表或者内容
        refreshLayout.setEnableFooterTranslationContent(true);//是否上啦Footer的时候向上平移列表或者内容
        refreshLayout.setEnableLoadmoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setDisableContentWhenRefresh(false);//是否在刷新的时候禁止列表的操作
        refreshLayout.setDisableContentWhenLoading(false);//是否在加载的时候禁止列表的操作
        refreshLayout.setOnMultiPurposeListener(new OnMultiPurposeListener());//设置多功能监听器
        refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider());//设置滚动边界判断
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));//设置Header
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));//设置Footer
        refreshLayout.autoRefresh();//自动刷新
        refreshLayout.autoLoadmore();//自动加载
        refreshLayout.autoRefresh(400);//延迟400毫秒后自动刷新
        refreshLayout.autoLoadmore(400);//延迟400毫秒后自动加载
        refreshlayout.finishRefresh();//结束刷新
        refreshlayout.finishLoadmore();//结束加载
        refreshlayout.finishRefresh(3000);//延迟3000毫秒后家属刷新
        refreshlayout.finishLoadmore(3000);//延迟3000毫秒后结束加载
        refreshlayout.finishRefresh(false);//结束刷新（刷新失败）
        refreshlayout.finishLoadmore(false);//结束加载（加载失败）
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smartrefresh.layout.SmartRefreshLayout
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:srlAccentColor="@android:color/white"
    app:srlPrimaryColor="@color/colorPrimary"
    app:srlReboundDuration="300"
    app:srlDragRate="0.5"
    app:srlHeaderMaxDragRate="2"
    app:srlFooterMaxDragRate="2"
    app:srlHeaderHeight="100dp"
    app:srlFooterHeight="100dp"
    app:srlEnableRefresh="true"
    app:srlEnableLoadmore="true"
    app:srlEnableAutoLoadmore="true"
    app:srlEnablePureScrollMode="false"
    app:srlEnableNestedScrolling="false"
    app:srlEnableOverScrollBounce="true"
    app:srlEnablePreviewInEditMode="true"
    app:srlEnableScrollContentWhenLoaded="true"
    app:srlEnableHeaderTranslationContent="true"
    app:srlEnableFooterTranslationContent="true"
    app:srlEnableLoadmoreWhenContentNotFull="false"
    app:srlDisableContentWhenRefresh="false"
    app:srlDisableContentWhenLoading="false"
    app:srlFixedFooterViewId="@+id/header_fixed"
    app:srlFixedHeaderViewId="@+id/footer_fixed"/>
    <!--srlAccentColor:强调颜色-->
    <!--srlPrimaryColor:主题颜色-->
    <!--srlEnablePreviewInEditMode:是否启用Android Studio编辑xml时预览效果-->
    <!--srlFixedFooterViewId:指定一个View在内容列表滚动时固定-->
    <!--srlFixedHeaderViewId:指定一个View在内容列表滚动时固定-->
    <!--未说明的：看上面的set方法说明-->
~~~

## ClassicsHeader
java代码设置
~~~java
public class RefreshActivity extends Activity {
    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = "刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = "刷新失败";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "上次更新 M-d HH:mm";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //下面示例中的值等于默认值
        ClassicsHeader header = (ClassicsHeader)findViewById(R.id.header);
        header.setAccentColor(android.R.color.white);//设置强调颜色
        header.setPrimaryColor(R.color.colorPrimary);//设置主题颜色
        header.setTextSizeTitle(16);//设置标题文字大小（sp单位）
        header.setTextSizeTitle(16, TypedValue.COMPLEX_UNIT_SP);//同上
        header.setTextSizeTime(10);//设置时间文字大小（sp单位）
        header.setTextSizeTime(10, TypedValue.COMPLEX_UNIT_SP);//同上
        header.setTextTimeMarginTop(10);//设置时间文字的上边距（dp单位）
        header.setTextTimeMarginTopPx(10);//同上-像素单位
        header.setEnableLastTime(true);//是否显示时间
        header.setFinishDuration(500);//设置刷新完成显示的停留时间
        header.setDrawableSize(20);//同时设置箭头和图片的大小（dp单位）
        header.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
        header.setDrawableProgressSize(20);//设置图片的大小（dp单位）
        header.setDrawableMarginRight(20);//设置图片和箭头和文字的间距（dp单位）
        header.setDrawableSizePx(20);//同上-像素单位
        header.setDrawableArrowSizePx(20);//同上-像素单位
        header.setDrawableProgressSizePx(20);//同上-像素单位
        header.setDrawableMarginRightPx(20);//同上-像素单位
        header.setArrowBitmap(bitmap);//设置箭头位图
        header.setArrowDrawable(drawable);//设置箭头图片
        header.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
        header.setProgressBitmap(bitmap);//设置图片位图
        header.setProgressDrawable(drawable);//设置图片
        header.setProgressResource(R.drawable.ic_progress);//设置图片资源
        header.setTimeFormat(new DynamicTimeFormat("上次更新 %s"));//设置时间格式化
        header.setSpinnerStyle(SpinnerStyle.Translate);//设置状态（不支持：MatchLayout）
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smartrefresh.layout.SmartRefreshLayout>
    <com.scwang.smartrefresh.layout.header.ClassicsHeader
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:srlAccentColor="@android:color/white"
        app:srlPrimaryColor="@color/colorPrimary"
        app:srlTextSizeTitle="16sp"
        app:srlTextSizeTime="10dp"
        app:srlTextTimeMarginTop="2dp"
        app:srlEnableLastTime="true"
        app:srlFinishDuration="500"
        app:srlDrawableSize="20dp"
        app:srlDrawableArrowSize="20dp"
        app:srlDrawableProgressSize="20dp"
        app:srlDrawableMarginRight="20dp"
        app:srlDrawableArrow="@drawable/ic_arrow"
        app:srlDrawableProgress="@drawable/ic_progress"
        app:srlClassicsSpinnerStyle="Translate"/>
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
~~~


## ClassicsFooter
java代码设置
~~~java
public class RefreshActivity extends Activity {
    static {
        ClassicsFooter.REFRESH_FOOTER_PULLUP = "上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
        ClassicsFooter.REFRESH_FOOTER_ALLLOADED = "全部加载完成";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //下面示例中的值等于默认值
        ClassicsFooter footer = (ClassicsFooter)findViewById(R.id.footer);
        footer.setAccentColor(android.R.color.white);//设置强调颜色
        footer.setPrimaryColor(R.color.colorPrimary);//设置主题颜色
        footer.setTextSizeTitle(16);//设置标题文字大小（sp单位）
        footer.setTextSizeTitle(16, TypedValue.COMPLEX_UNIT_SP);//同上
        footer.setFinishDuration(500);//设置刷新完成显示的停留时间
        footer.setDrawableSize(20);//同时设置箭头和图片的大小（dp单位）
        footer.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
        footer.setDrawableProgressSize(20);//设置图片的大小（dp单位）
        footer.setDrawableMarginRight(20);//设置图片和箭头和文字的间距（dp单位）
        footer.setDrawableSizePx(20);//同上-像素单位
        footer.setDrawableArrowSizePx(20);//同上-像素单位
        footer.setDrawableProgressSizePx(20);//同上-像素单位
        footer.setDrawableMarginRightPx(20);//同上-像素单位
        footer.setArrowBitmap(bitmap);//设置箭头位图
        footer.setArrowDrawable(drawable);//设置箭头图片
        footer.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
        footer.setProgressBitmap(bitmap);//设置图片位图
        footer.setProgressDrawable(drawable);//设置图片
        footer.setProgressResource(R.drawable.ic_progress);//设置图片资源
        footer.setSpinnerStyle(SpinnerStyle.Translate);//设置状态（不支持：MatchLayout）
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smartrefresh.layout.SmartRefreshLayout>
    <com.scwang.smartrefresh.layout.header.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:srlAccentColor="@android:color/white"
        app:srlPrimaryColor="@color/colorPrimary"
        app:srlTextSizeTitle="16sp"
        app:srlFinishDuration="500"
        app:srlDrawableSize="20dp"
        app:srlDrawableArrowSize="20dp"
        app:srlDrawableProgressSize="20dp"
        app:srlDrawableMarginRight="20dp"
        app:srlDrawableArrow="@drawable/ic_arrow"
        app:srlDrawableProgress="@drawable/ic_progress"
        app:srlClassicsSpinnerStyle="Translate"/>
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
~~~


# 属性表格
## Attributes

|name|format|description|
|:---:|:---:|:---:|
|srlPrimaryColor|color|主题颜色|
|srlAccentColor|color|强调颜色|
|srlReboundDuration|integer|释放后回弹动画时长（默认250毫秒）|
|srlHeaderHeight|dimension|Header的标准高度（dp）|
|srlFooterHeight|dimension|Footer的标准高度（dp）|
|srlDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|srlHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|srlFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|srlEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|srlEnableLoadmore|boolean|是否开启加上拉加载功能（默认false-智能开启）|
|srlEnableAutoLoadmore|boolean|是否监听列表惯性滚动到底部时触发加载事件（默认true）|
|srlEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|srlEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|srlEnablePreviewInEditMode|boolean|是否在编辑模式时显示预览效果（默认true）|
|srlEnablePureScrollMode|boolean|是否开启纯滚动模式（默认false-开启时只支持一个子视图）|
|srlEnableOverScrollBounce|boolean|设置是否开启越界回弹功能（默认true）|
|srlEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling(默认false-智能开启)|
|srlEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据（默认-true）|
|srlEnableLoadmoreWhenContentNotFull|boolean|在内容不满一页的时候，是否可以上拉加载更多（默认-false）|
|srlDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|srlDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|srlFixedHeaderViewId|id|指定固定的视图Id|
|srlFixedFooterViewId|id|指定固定的视图Id|

## Method

|name|format|description|
|:---:|:---:|:---:|
|setPrimaryColors|colors|主题\强调颜色|
|setPrimaryColorsId|colors|主题\强调颜色资源Id|
|setReboundDuration|integer|释放后回弹动画时长（默认250毫秒）|
|setHeaderHeight|dimension|Header的标准高度（px/dp 两个版本）|
|setFooterHeight|dimension|Footer的标准高度（px/dp 两个版本）|
|setDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|setHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|setFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|setEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|setEnableLoadmore|boolean|是否开启加上拉加载功能（默认false-智能开启）|
|setEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|setEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|setEnableAutoLoadmore|boolean|是否监听列表惯性滚动到底部时触发加载事件（默认true）|
|setEnablePureScrollMode|boolean|是否开启纯滚动模式（默认false-开启时只支持一个子视图）|
|setEnableOverScrollBounce|boolean|设置是否开启越界回弹功能（默认true）|
|setEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling（默认false-智能开启）|
|setEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据（默认-true）|
|setEnableLoadmoreWhenContentNotFull|boolean|在内容不满一页的时候，是否可以上拉加载更多（默认-false）|
|setDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|setDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|setReboundInterpolator|Interpolator|设置回弹动画的插值器（默认减速）|
|setRefreshHeader|RefreshHeader|设置指定的Header（默认贝塞尔雷达）|
|setRefreshFooter|RefreshFooter|设置指定的Footer（默认球脉冲）|
|setOnRefreshListener|OnRefreshListener|设置刷新监听器（默认3秒后关刷新）|
|setOnLoadmoreListener|OnLoadmoreListener|设置加载监听器（默认3秒后关加载）|
|setOnRefreshLoadmoreListener|OnRefreshLoadmoreListener|同时设置上面两个监听器|
|setOnMultiPurposeListener|OnMultiPurposeListener|设置多功能监听器|
|setLoadmoreFinished|boolean|设置全部数据加载完成，之后不会触发加载事件|
|setScrollBoundaryDecider|boundary|设置滚动边界判断|
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
|srlDrawableArrow|drawable|箭头图片|
|srlDrawableProgress|drawable|转动图片|
|srlClassicsSpinnerStyle|enum|变换样式：Translate(平行移动)、Scale（拉伸形变）、FixedBehind（固定在背后）|
|srlSpinnerStyle|enum|变换样式：srlClassicsSpinnerStyle的全部、FixedFront（固定在前面或全屏）|
|srlFinishDuration|int|动画结束时，显示完成状态停留的时间（毫秒）|
|srlEnableLastTime|boolean|是否显示上次更新时间（默认true）|
|srlDrawableMarginRight|dimension|图片相对右边文字的距离（默认20dp）|
|srlTextTimeMarginTop|dimension|更新时间相对上面标题的距离（默认2dp）|
|srlTextSizeTitle|dimension|标题文字大小（默认16sp）|
|srlTextSizeTime|dimension|时间文字大小（默认12sp）|

## Header-Method
|name|format|description|
|:---:|:---:|:---:|
|setPrimaryColor|color|主题颜色|
|setAccentColor|color|强调颜色|
|setArrowDrawable|drawable|设置箭头图片|
|setProgressDrawable|drawable|设置转动图片|
|setArrowBitmap|bitmap|设置箭头图片|
|setProgressBitmap|bitmap|设置转动图片|
|setArrowResource|int|设置箭头图片|
|setProgressResource|int|设置转动图片|
|setSpinnerStyle|enum|变换样式：参考属性srlSpinnerStyle|
|setClassicsSpinnerStyle|enum|变换样式：参考属性srlClassicsSpinnerStyle|
|setFinishDuration|int|设置动画结束时，显示完成状态停留的时间（毫秒）|
|setEnableLastTime|boolean|是否显示上次更新时间（默认true）|
|setTextSizeTitle|dimension|标题文字大小（默认16sp）|
|setTextSizeTime|dimension|时间文字大小（默认12sp）|