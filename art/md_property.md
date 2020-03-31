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

        refreshLayout.setHeaderHeight(100);//Header标准高度（显示下拉高度>=标准高度 触发刷新）
        refreshLayout.setHeaderHeightPx(100);//同上-像素为单位 （V1.1.0删除）
        refreshLayout.setFooterHeight(100);//Footer标准高度（显示上拉高度>=标准高度 触发加载）
        refreshLayout.setFooterHeightPx(100);//同上-像素为单位 （V1.1.0删除）

        refreshLayout.setFooterHeaderInsetStart(0);//设置 Header 起始位置偏移量 1.0.5
        refreshLayout.setFooterHeaderInsetStartPx(0);//同上-像素为单位 1.0.5 （V1.1.0删除）
        refreshLayout.setFooterFooterInsetStart(0);//设置 Footer 起始位置偏移量 1.0.5
        refreshLayout.setFooterFooterInsetStartPx(0);//同上-像素为单位 1.0.5 （V1.1.0删除）

        refreshLayout.setHeaderMaxDragRate(2);//最大显示下拉高度/Header标准高度
        refreshLayout.setFooterMaxDragRate(2);//最大显示下拉高度/Footer标准高度
        refreshLayout.setHeaderTriggerRate(1);//触发刷新距离 与 HeaderHeight 的比率1.0.4
        refreshLayout.setFooterTriggerRate(1);//触发加载距离 与 FooterHeight 的比率1.0.4

        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(false);//是否启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
        refreshLayout.setEnablePureScrollMode(false);//是否启用纯滚动模式
        refreshLayout.setEnableNestedScroll(false);//是否启用嵌套滚动
        refreshLayout.setEnableOverScrollBounce(true);//是否启用越界回弹
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableHeaderTranslationContent(true);//是否下拉Header的时候向下平移列表或者内容
        refreshLayout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setEnableFooterFollowWhenLoadFinished(false);//是否在全部加载结束之后Footer跟随内容1.0.4
        refreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4

        refreshLayout.setEnableScrollContentWhenRefreshed(true);//是否在刷新完成时滚动列表显示新的内容 1.0.5
        refreshLayout.srlEnableClipHeaderWhenFixedBehind(true);//是否剪裁Header当时样式为FixedBehind时1.0.5
        refreshLayout.srlEnableClipFooterWhenFixedBehind(true);//是否剪裁Footer当时样式为FixedBehind时1.0.5

        refreshLayout.setDisableContentWhenRefresh(false);//是否在刷新的时候禁止列表的操作
        refreshLayout.setDisableContentWhenLoading(false);//是否在加载的时候禁止列表的操作

        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener());//设置多功能监听器
        refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider());//设置滚动边界判断
        refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDeciderAdapter());//自定义滚动边界

        refreshLayout.setRefreshHeader(new ClassicsHeader(context));//设置Header
        refreshLayout.setRefreshFooter(new ClassicsFooter(context));//设置Footer
        refreshLayout.setRefreshContent(new View(context));//设置刷新Content（用于非xml布局代替addView）1.0.4

        refreshLayout.autoRefresh();//自动刷新
        refreshLayout.autoLoadMore();//自动加载
        refreshLayout.autoRefreshAnimationOnly();//自动刷新，只显示动画不执行刷新
        refreshLayout.autoLoadMoreAnimationOnly();//自动加载，只显示动画不执行加载
        refreshLayout.autoRefresh(400);//延迟400毫秒后自动刷新
        refreshLayout.autoLoadMore(400);//延迟400毫秒后自动加载
        refreshLayout.finishRefresh();//结束刷新
        refreshLayout.finishLoadMore();//结束加载
        refreshLayout.finishRefresh(3000);//延迟3000毫秒后结束刷新
        refreshLayout.finishLoadMore(3000);//延迟3000毫秒后结束加载
        refreshLayout.finishRefresh(false);//结束刷新（刷新失败）
        refreshLayout.finishLoadMore(false);//结束加载（加载失败）
        refreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
        refreshLayout.closeHeaderOrFooter();//关闭正在打开状态的 Header 或者 Footer（1.1.0）
        refreshLayout.resetNoMoreData();//恢复没有更多数据的原始状态 1.0.4（1.1.0删除）
        refreshLayout.setNoMoreData(false);//恢复没有更多数据的原始状态 1.0.5

    }
}

//全局一次性设置默认属性和默认Header
public class App extends Application {
    static {//使用static代码段可以防止内存泄漏

        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                layout.setReboundDuration(1000);
                layout.setReboundInterpolator(new DropBounceInterpolator());
                layout.setFooterHeight(100);
                layout.setDisableContentWhenLoading(false);
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            }
        });

        //全局设置默认的 Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //开始设置全局的基本参数（这里设置的属性只跟下面的MaterialHeader绑定，其他Header不会生效，能覆盖DefaultRefreshInitializer的属性和Xml设置的属性）
                layout.setEnableHeaderTranslationContent(false);
                return new MaterialHeader(context).setColorSchemeResources(R.color.colorRed,R.color.colorGreen,R.color.colorBlue);
            }
        });
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smart.refresh.layout.SmartRefreshLayout
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:srlAccentColor="@android:color/white"
    app:srlPrimaryColor="@color/colorPrimary"
    app:srlReboundDuration="300"
    app:srlDragRate="0.5"

    app:srlHeaderMaxDragRate="2"
    app:srlFooterMaxDragRate="2"
    app:srlHeaderTriggerRate="1"
    app:srlFooterTriggerRate="1"

    app:srlHeaderHeight="100dp"
    app:srlFooterHeight="100dp"
    app:srlHeaderInsetStart="0dp"
    app:srlFooterInsetStart="0dp"

    app:srlEnableRefresh="true"
    app:srlEnableLoadMore="true"
    app:srlEnableAutoLoadMore="true"
    app:srlEnablePureScrollMode="false"
    app:srlEnableNestedScrolling="false"
    app:srlEnableOverScrollDrag="true"
    app:srlEnableOverScrollBounce="true"
    app:srlEnablePreviewInEditMode="true"
    app:srlEnableScrollContentWhenLoaded="true"
    app:srlEnableScrollContentWhenRefreshed="true"
    app:srlEnableHeaderTranslationContent="true"
    app:srlEnableFooterTranslationContent="true"
    app:srlEnableLoadMoreWhenContentNotFull="false"
    app:srlEnableFooterFollowWhenLoadFinished="false"

    app:srlEnableClipHeaderWhenFixedBehind="true"
    app:srlEnableClipFooterWhenFixedBehind="true"

    app:srlDisableContentWhenRefresh="false"
    app:srlDisableContentWhenLoading="false"

    app:srlFixedFooterViewId="@+id/header_fixed"
    app:srlFixedHeaderViewId="@+id/footer_fixed"
    app:srlHeaderTranslationViewId="@+id/header_translation"
    app:srlFooterTranslationViewId="@+id/footer_translation"
    />
    <!--srlAccentColor:强调颜色-->
    <!--srlPrimaryColor:主题颜色-->
    <!--srlEnablePreviewInEditMode:是否启用Android Studio编辑xml时预览效果-->
    <!--srlFixedFooterViewId:指定一个View在内容列表滚动时固定-->
    <!--srlFixedHeaderViewId:指定一个View在内容列表滚动时固定-->
    <!--srlHeaderTranslationViewId:指定下拉Header时偏移的视图Id-->
    <!--srlFooterTranslationViewId:指定上拉Footer时偏移的视图Id-->
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
        ClassicsHeader.REFRESH_HEADER_SECONDARY = "释放进入二楼";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "上次更新 M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "'Last update' M-d HH:mm";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = getString(R.string.header_pulldown);//"下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.header_refreshing);//"正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.header_loading);//"正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.header_release);//"释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.header_finish);//"刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.header_failed);//"刷新失败";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = getString(R.string.header_secondary);//"释放进入二楼";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = getString(R.string.header_lasttime);//"上次更新 M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = getString(R.string.header_lasttime);//"'Last update' M-d HH:mm"
        //下面示例中的值等于默认值
        ClassicsHeader header = (ClassicsHeader)findViewById(R.id.header);
        header.setAccentColor(android.R.color.white);//设置强调颜色
        header.setPrimaryColor(R.color.colorPrimary);//设置主题颜色
        header.setTextSizeTitle(16);//设置标题文字大小（sp单位）
        header.setTextSizeTitle(16, TypedValue.COMPLEX_UNIT_SP);//同上（1.1.0版本删除）
        header.setTextSizeTime(10);//设置时间文字大小（sp单位）
        header.setTextSizeTime(10, TypedValue.COMPLEX_UNIT_SP);//同上（1.1.0版本删除）
        header.setTextTimeMarginTop(10);//设置时间文字的上边距（dp单位）
        header.setTextTimeMarginTopPx(10);//同上-像素单位（1.1.0版本删除）
        header.setEnableLastTime(true);//是否显示时间
        header.setFinishDuration(500);//设置刷新完成显示的停留时间（设为0可以关闭停留功能）
        header.setDrawableSize(20);//同时设置箭头和图片的大小（dp单位）
        header.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
        header.setDrawableProgressSize(20);//设置图片的大小（dp单位）
        header.setDrawableMarginRight(20);//设置图片和箭头和文字的间距（dp单位）
        header.setDrawableSizePx(20);//同上-像素单位
        header.setDrawableArrowSizePx(20);//同上-像素单位（1.1.0版本删除）
        header.setDrawableProgressSizePx(20);//同上-像素单位（1.1.0版本删除）
        header.setDrawableMarginRightPx(20);//同上-像素单位（1.1.0版本删除）
        header.setArrowBitmap(bitmap);//设置箭头位图（1.1.0版本删除）
        header.setArrowDrawable(drawable);//设置箭头图片
        header.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
        header.setProgressBitmap(bitmap);//设置图片位图（1.1.0版本删除）
        header.setProgressDrawable(drawable);//设置图片
        header.setProgressResource(R.drawable.ic_progress);//设置图片资源
        header.setTimeFormat(new DynamicTimeFormat("上次更新 %s"));//设置时间格式化（时间会自动更新）
        header.setLastUpdateText("上次更新 3秒前");//手动更新时间文字设置（将不会自动更新时间）
        header.setSpinnerStyle(SpinnerStyle.Translate);//设置移动样式（不支持：MatchLayout）
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smart.refresh.layout.SmartRefreshLayout>
    <com.scwang.smart.refresh.footer.ClassicsHeader
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
        app:srlClassicsSpinnerStyle="Translate"
        app:srlTextPulling="@string/srl_header_pulling"
        app:srlTextLoading="@string/srl_header_loading"
        app:srlTextRelease="@string/srl_header_release"
        app:srlTextFinish="@string/srl_header_finish"
        app:srlTextFailed="@string/srl_header_failed"
        app:srlTextUpdate="@string/srl_header_update"
        app:srlTextSecondary="@string/srl_header_secondary"
        app:srlTextRefreshing="@string/srl_header_refreshing"/>
</com.scwang.smart.refresh.layout.SmartRefreshLayout>
~~~


## ClassicsFooter
java代码设置
~~~java
public class RefreshActivity extends Activity {
    static {
        ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.footer_pulling);//"上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.footer_release);//"释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.footer_refreshing);//"正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.footer_loading);//"正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.footer_finish);//"加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.footer_failed);//"加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.footer_nothing);//"没有更多数据了";

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
        footer.setDrawableSizePx(20);//同上-像素单位（1.1.0版本删除）
        footer.setDrawableArrowSizePx(20);//同上-像素单位（1.1.0版本删除）
        footer.setDrawableProgressSizePx(20);//同上-像素单位（1.1.0版本删除）
        footer.setDrawableMarginRightPx(20);//同上-像素单位（1.1.0版本删除）
        footer.setArrowBitmap(bitmap);//设置箭头位图（1.1.0版本删除）
        footer.setArrowDrawable(drawable);//设置箭头图片
        footer.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
        footer.setProgressBitmap(bitmap);//设置图片位图（1.1.0版本删除）
        footer.setProgressDrawable(drawable);//设置图片
        footer.setProgressResource(R.drawable.ic_progress);//设置图片资源
        footer.setSpinnerStyle(SpinnerStyle.Translate);//设置移动样式（不支持：MatchLayout）
    }
}
~~~
xml代码设置
~~~xml
<!-- 下面示例中的值等于默认值 -->
<com.scwang.smart.refresh.layout.SmartRefreshLayout>
    <com.scwang.smart.refresh.footer.ClassicsFooter
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
        app:srlClassicsSpinnerStyle="Translate"
        app:srlTextPulling="@string/srl_footer_pulling"
        app:srlTextRelease="@string/srl_footer_release"
        app:srlTextLoading="@string/srl_footer_loading"
        app:srlTextRefreshing="@string/srl_footer_refreshing"
        app:srlTextFinish="@string/srl_footer_finish"
        app:srlTextFailed="@string/srl_footer_failed"
        app:srlTextNothing="@string/srl_footer_nothing"/>
</com.scwang.smart.refresh.layout.SmartRefreshLayout>
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
|srlHeaderInsetStart|dimension|Header的起始偏移量（dp）V1.0.5|
|srlFooterInsetStart|dimension|Footer的起始偏移量（dp）V1.0.5|
|srlDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|srlHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|srlFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|srlHeaderTriggerRate|float|Header触发刷新距离 与 HeaderHeight 的比率（默认1）|
|srlFooterTriggerRate|float|Footer触发加载距离 与 FooterHeight 的比率（默认1）|
|srlEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|srlEnableLoadMore|boolean|是否开启加上拉加载功能（默认false-智能开启）|
|srlEnableAutoLoadMore|boolean|是否监听列表惯性滚动到底部时触发加载事件（默认true）|
|srlEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|srlEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|srlEnablePreviewInEditMode|boolean|是否在编辑模式时显示预览效果（默认true）|
|srlEnablePureScrollMode|boolean|是否开启纯滚动模式（默认false-开启时只支持一个子视图）|
|srlEnableOverScrollDrag|boolean|是否启用越界拖动（仿苹果效果）V1.0.4|
|srlEnableOverScrollBounce|boolean|设置是否开启越界回弹功能（默认true）|
|srlEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling(默认false-智能开启)|
|srlEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据（默认-true）|
|srlEnableScrollContentWhenRefreshed|boolean|是否在刷新成功之后滚动内容显示新数据（默认-true）|
|srlEnableLoadMoreWhenContentNotFull|boolean|在内容不满一页的时候，是否可以上拉加载更多（默认-false）|
|srlEnableFooterFollowWhenLoadFinished|boolean|是否在全部加载结束之后Footer跟随内容|
|srlEnableClipHeaderWhenFixedBehind|boolean|是否剪裁Header当时样式为FixedBehind时V1.0.5|
|srlEnableClipFooterWhenFixedBehind|boolean|是否剪裁Footer当时样式为FixedBehind时V1.0.5|
|srlDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|srlDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|srlFixedHeaderViewId|id|指定固定顶部的视图Id|
|srlFixedFooterViewId|id|指定固定底部的视图Id|
|srlHeaderTranslationViewId|id|指定下拉Header时偏移的视图Id|
|srlFooterTranslationViewId|id|指定上拉Footer时偏移的视图Id|

## Method

|name|format|description|
|:---:|:---:|:---:|
|setPrimaryColors|colors|主题\强调颜色|
|setPrimaryColorsId|colors|主题\强调颜色资源Id|
|setReboundDuration|integer|释放后回弹动画时长（默认250毫秒）|
|setHeaderHeight|dimension|Header的标准高度（px/dp 两个版本）|
|setFooterHeight|dimension|Footer的标准高度（px/dp 两个版本）|
|setHeaderInsetStart|dimension|Header起始位置偏移量（px/dp 两个版本）V1.0.5|
|setFooterInsetStart|dimension|Footer起始位置偏移量（px/dp 两个版本）V1.0.5|
|setDragRate|float|显示拖动高度/真实拖动高度（默认0.5，阻尼效果）|
|setHeaderMaxDragRate|float|Header最大拖动高度/Header标准高度（默认2，要求>=1）|
|setFooterMaxDragRate|float|Footer最大拖动高度/Footer标准高度（默认2，要求>=1）|
|setHeaderTriggerRate|float|Header触发刷新距离 与 HeaderHeight 的比率（默认1）|
|setFooterTriggerRate|float|Footer触发加载距离 与 FooterHeight 的比率（默认1）|
|setEnableRefresh|boolean|是否开启下拉刷新功能（默认true）|
|setEnableLoadMore|boolean|是否开启加上拉加载功能（默认false-智能开启）|
|setEnableHeaderTranslationContent|boolean|拖动Header的时候是否同时拖动内容（默认true）|
|setEnableFooterTranslationContent|boolean|拖动Footer的时候是否同时拖动内容（默认true）|
|setEnableAutoLoadMore|boolean|是否监听列表惯性滚动到底部时触发加载事件（默认true）|
|setEnablePureScrollMode|boolean|是否开启纯滚动模式（默认false-开启时只支持一个子视图）|
|setEnableOverScrollDrag|boolean|是否启用越界拖动（仿苹果效果）V1.0.4|
|setEnableOverScrollBounce|boolean|设置是否开启越界回弹功能（默认true）|
|setEnableNestedScrolling|boolean|是否开启嵌套滚动NestedScrolling（默认false-智能开启）|
|setEnableScrollContentWhenLoaded|boolean|是否在加载完成之后滚动内容显示新数据（默认-true）|
|setEnableScrollContentWhenRefreshed|boolean|是否在刷新成功之后滚动内容显示新数据（默认-true）V1.0.5|
|setEnableLoadMoreWhenContentNotFull|boolean|在内容不满一页的时候，是否可以上拉加载更多（默认-false）|
|setEnableFooterFollowWhenLoadFinished|boolean|是否在全部加载结束之后Footer跟随内容|
|setEnableClipHeaderWhenFixedBehind|boolean|是否剪裁Header当时样式为FixedBehind时V1.0.5|
|setEnableClipFooterWhenFixedBehind|boolean|是否剪裁Footer当时样式为FixedBehind时V1.0.5|
|setDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|setDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|
|setReboundInterpolator|Interpolator|设置回弹动画的插值器（默认减速）|
|setRefreshHeader|RefreshHeader|设置指定的Header（默认贝塞尔雷达）|
|setRefreshFooter|RefreshFooter|设置指定的Footer（默认球脉冲）|
|setRefreshContent|View|设置刷新Content（用于动态替换空布局）|
|setOnRefreshListener|OnRefreshListener|设置刷新监听器（不设置，默认3秒后关刷新）|
|setOnLoadMoreListener|OnLoadMoreListener|设置加载监听器（不设置，默认3秒后关加载）|
|setOnRefreshLoadMoreListener|OnRefreshLoadMoreListener|同时设置上面两个监听器|
|setOnMultiPurposeListener|OnMultiPurposeListener|设置多功能监听器|
|setLoadMoreFinished|boolean|设置全部数据加载完成，之后不会触发加载事件|
|setScrollBoundaryDecider|boundary|设置滚动边界判断|
|finishRefresh|(int delayed)|完成刷新，结束刷新动画|
|finishLoadMore|(int delayed)|完成加载，结束加载动画|
|finishRefresh|(boolean success)|完成刷新，并设置是否成功|
|finishLoadMore|(boolean success)|完成加载，并设置是否成功|
|finishLoadMoreWithNoMoreData||完成加载并标记没有更多数据(V1.0.4)|
|closeHeaderOrFooter||关闭 Header 或者 Footer（1.1.0）|
|resetNoMoreData||V1.0.4（V1.1.0删除，用 setNoMoreData(false) 代替）|
|setNoMoreData|boolean|设置更多数据状态V1.0.5|
|getRefreshHeader|RefreshHeader|获取Header|
|getRefreshFooter|RefreshFooter|获取Footer|
|getState|RefreshState|获取当前状态|
|isRefreshing|boolean|(V1.1.0删除，版本用 getState==Refreshing 代替)|
|isLoading|boolean|(V1.1.0删除，版本用 getState==Loading 代替)|
|autoRefresh|(int delayed)|触发自动刷新|
|autoLoadMore|(int delayed)|触发自动加载|
|autoRefreshAnimationOnly| |触发自动刷新，只显示动画不执行刷新 |
|autoLoadMoreAnimationOnly| |触发自动加载，只显示动画不执行加载  |

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
|setArrowBitmap|bitmap|设置箭头图片(V1.1.0版本删除)|
|setProgressBitmap|bitmap|设置转动图片(V1.1.0版本删除)|
|setArrowResource|int|设置箭头图片|
|setProgressResource|int|设置转动图片|
|setSpinnerStyle|enum|变换样式：参考属性srlSpinnerStyle|
|setClassicsSpinnerStyle|enum|变换样式：参考属性srlClassicsSpinnerStyle|
|setFinishDuration|int|设置动画结束时，显示完成状态停留的时间（毫秒）|
|setEnableLastTime|boolean|是否显示上次更新时间（默认true）|
|setTextSizeTitle|dimension|标题文字大小（默认16sp）|
|setTextSizeTime|dimension|时间文字大小（默认12sp）|
|setLastUpdateText|string|手动设置更新时间，将不会自动更新时间|