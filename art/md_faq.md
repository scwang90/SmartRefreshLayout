# 常见问题

## 0.恢复经典模式

SmartRefresh默认使用了比较新的功能，如：越界回弹、越界拖动、自动加载更多，这些效果都是比较流行，并且具有仿苹果的效果，如果之前使用很早的经典刷新库的童鞋感觉不适应，这些功能都是可以关闭的。

代码设置
~~~java
    refreshLayout.setEnableAutoLoadMore(false);//使上拉加载具有弹性效果
    refreshLayout.setEnableOverScrollDrag(false);//禁止越界拖动（1.0.4以上版本）
    refreshLayout.setEnableOverScrollBounce(false);//关闭越界回弹功能
~~~
XML属性
~~~xml
    <com.scwang.smart.refresh.layout.SmartRefreshLayout
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:srlEnableOverScrollDrag="false"
        app:srlEnableAutoLoadMore="false"
        app:srlEnableOverScrollBounce="false">
    </com.scwang.smart.refresh.layout.SmartRefreshLayout>
~~~

## 1.获取当前状态？isRefreshing(),isLoading() 不见了？（1.1.0以上版本，2.x 回归）

版本的迭代，刷新的状态越来越多，仅仅 isRefreshing(),isLoading() 已经无法满足要求，在1.0.5版本之后本库直接将
内部 State 开放出来，并在1.0.5版本标记 isRefreshing(),isLoading() 过期，鼓励大家使用 getState 来代替。将在
1.1.0版本删除这两个API。
getState 比前两个方法更有用，具体参考下面代码。
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

> WebView 的问题多由内部Html中采用了绝对坐标导致的，所以问题很难从java层面解决这个问题。
我建议直接再Html内部实现下拉刷新，或者采用自定义滚动边界，参考 [#394](https://github.com/scwang90/SmartRefreshLayout/issues/394)。
另外 [SCDN](https://blog.csdn.net/niuzhijun66/article/details/86290182) 博客中有人给出解决方案

## 3.列表内容才几条，却可以上拉加载？

SmartRefresh提供了对数据不满一页判断处理，可以通过EnableLoadMoreWhenContentNotFull来控制

代码设置
~~~java
    refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
~~~
XML属性
~~~xml
    <com.scwang.smart.refresh.layout.SmartRefreshLayout
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:srlEnableLoadMoreWhenContentNotFull="false">
    </com.scwang.smart.refresh.layout.SmartRefreshLayout>
~~~


## 4.如何修改经典刷新文字？
SmartRefresh的经典文字自带了国际化（中/英），如需自定义文字显示，可以通过以下两种方法设置：

代码设置（APP在运行是，更改系统语言，本方法无效）

ClassicsHeader 和 ClassicsFooter 的描述文字是可以修改的，不要看错成了常量哦~

~~~java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ClassicsHeader.REFRESH_HEADER_PULLING = getString(R.string.header_pulling);//"下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.header_refreshing);//"正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.header_loading);//"正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.header_release);//"释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.header_finish);//"刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.header_failed);//"刷新失败";
        ClassicsHeader.REFRESH_HEADER_UPDATE = getString(R.string.header_update);//"上次更新 M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_UPDATE = getString(R.string.header_update);//"'Last update' M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = getString(R.string.header_secondary);//"释放进入二楼"

        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.footer_pulling);//"上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.footer_release);//"释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.footer_loading);//"正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.footer_refreshing);//"正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.footer_finish);//"加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.footer_failed);//"加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.footer_nothing);//"全部加载完成";
    }
}
~~~

资源覆盖（1.1.0 以上版本）
~~~xml
<resources>
    <string name="srl_header_pulling">下拉可以刷新</string>
    <string name="srl_header_refreshing">正在刷新…</string>
    <string name="srl_header_loading">正在加载…</string>
    <string name="srl_header_release">释放立即刷新</string>
    <string name="srl_header_finish">刷新完成</string>
    <string name="srl_header_failed">刷新失败</string>
    <string name="srl_header_update">上次更新 M-d HH:mm</string>
    <string name="srl_header_secondary">释放进入二楼</string>

    <string name="srl_footer_pulling">上拉加载更多</string>
    <string name="srl_footer_release">释放立即加载</string>
    <string name="srl_footer_loading">正在加载…</string>
    <string name="srl_footer_refreshing">正在刷新…</string>
    <string name="srl_footer_finish">加载完成</string>
    <string name="srl_footer_failed">加载失败</string>
    <string name="srl_footer_nothing">没有更多数据了</string>
</resources>
~~~

XML直接指定（1.1.0 最新版）
~~~xml
    <com.scwang.smart.refresh.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:srlTextPulling="@string/srl_header_pulling"
        app:srlTextLoading="@string/srl_header_loading"
        app:srlTextRelease="@string/srl_header_release"
        app:srlTextFinish="@string/srl_header_finish"
        app:srlTextFailed="@string/srl_header_failed"
        app:srlTextUpdate="@string/srl_header_update"
        app:srlTextSecondary="@string/srl_header_secondary"
        app:srlTextRefreshing="@string/srl_header_refreshing"/>

    <com.scwang.smart.refresh.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:srlTextPulling="@string/srl_footer_pulling"
        app:srlTextRelease="@string/srl_footer_release"
        app:srlTextLoading="@string/srl_footer_loading"
        app:srlTextRefreshing="@string/srl_footer_refreshing"
        app:srlTextFinish="@string/srl_footer_finish"
        app:srlTextFailed="@string/srl_footer_failed"
        app:srlTextNothing="@string/srl_footer_nothing"/>
~~~

#### 注意：上次更新时间的英文格式需要加单引号如： 'Last update' M-d HH:mm

## 5.触发下拉刷新的距离

SmartRefresh触发下拉刷新的距离就是 Header 的高度乘以比率：HeaderHeight*HeaderTriggerRate，下拉的距离超过这个值释放时就可以触发刷新事件，否则回弹到原始状态。

HeaderTriggerRate 默认是 1，改成0.5，那么再下拉到一半的时候就可以刷新了
改变这个距离就是 setHeaderHeight，footer 类推

##### 相关方法
|         name         |  format   |            description            |
| :------------------: | :-------: | :-------------------------------: |
|   setHeaderHeight    | dimension |            Header的标准高度            |
|   setFooterHeight    | dimension |            Footer的标准高度            |
| setHeaderTriggerRate |   float   | Header触发刷新距离与HeaderHeight的比率（默认1） |
| setFooterTriggerRate |   float   | Footer触发加载距离与FooterHeight的比率（默认1） |

##### 相关属性
|         name         |  format   |            description            |
| :------------------: | :-------: | :-------------------------------: |
|   srlHeaderHeight    | dimension |          Header的标准高度（dp）          |
|   srlFooterHeight    | dimension |          Footer的标准高度（dp）          |
| srlHeaderTriggerRate |   float   | Header触发刷新距离与HeaderHeight的比率（默认1） |
| srlFooterTriggerRate |   float   | Footer触发加载距离与FooterHeight的比率（默认1） |

## 6.阻尼效果参数

SmartRefresh的阻尼相关参数有两个
> DragRate = 显示拖动距离 / 手指真实拖动距离 （要求<= 1，越小阻尼越大）  
> MaxDragRate = 最大拖动距离 / Header或者Footer的高度 （要求>=1,越大阻尼越小）

##### 相关方法
|         name         |  format   |             description             |
| :------------------: | :-------: | :---------------------------------: |
|     setDragRate      | dimension |               设置拖动比率                |
| setHeaderMaxDragRate |   float   | Header最大拖动距离与HeaderHeight的比率（默认2.5） |
| setFooterMaxDragRate |   float   | Footer最大拖动距离与FooterHeight的比率（默认2.5） |

##### 相关属性
|         name         |  format   |             description             |
| :------------------: | :-------: | :---------------------------------: |
|     srlDragRate      | dimension |               设置拖动比率                |
| srlHeaderMaxDragRate |   float   | Header最大拖动距离与HeaderHeight的比率（默认2.5） |
| srlFooterMaxDragRate |   float   | Footer最大拖动距离与FooterHeight的比率（默认2.5） |

## 7.全局设置基本参数

SmartRefresh提供的全局设置方法不仅可以设置 Header 和 Footer 的样式，其他的参数也可以直接设置如下：

~~~java
public class App extends Application {
    static {//使用static代码段可以防止内存泄漏

        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                //开始设置全局的基本参数
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

## 8.没有上拉，快速滚动列表，Footer自己冒上来了？

这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。
如果不想要这个功能，是可以关闭的：

代码设置
~~~java
refreshlayout.setEnableAutoLoadMore(false);
~~~
XML属性
~~~xml
    <com.scwang.smart.refresh.layout.SmartRefreshLayout
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:srlEnableAutoLoadMore="false">
    </com.scwang.smart.refresh.layout.SmartRefreshLayout>
~~~

## 8.还没调用 finishRefresh ，刷新就自动结束了？

解决办法：setRefreshListener
~~~java
    refreshLayout.setRefreshListener(listener);
~~~

实际项目中都会设置监听器的，所以不设置的都是demo，为了demo方便就默认3秒之后关闭了，如果一直不关闭，体验不好

## 10.添加固定的头布局

本库支持添加固定的头布局，参考  Demo-实战-微博列表

![image](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/gif_practive_feedlist.gif)

注意：是**固定**的头布局，如果是想添加可以滚动的头布局，请使用开源的Adapter实现

## 11.显示没有更多数据，并不再触发加载更事件

~~~java
//加载结束之后的逻辑
if (/*加载更多成功*/) {
    if (/*服务器还有更多数据*/){
        refreshlayout.finishLoadMore();
    } else {
        refreshlayout.finishLoadMoreWidthNoMoreData();//显示全部加载完成，并不再触发加载更事件
    }
} else {
    refreshlayout.finishLoadMore(false);//表示加载失败
}

//刷新结束之后
if (/*刷新成功*/) {
    refreshlayout.finishRefresh();
    refreshlayout.setNoMoreData(/*服务器不再有更多数据*/)//复原状态
} else {
    refreshlayout.finishRefresh(false);//表示刷新失败（不会更新时间）
}
~~~

## 12.加载更多一直显示？不能触发下拉刷新？

~~~java
//尝试在 setNoMoreData 之前加上 finishLoadMore
refreshlayout.finishLoadMore();
refreshlayout.setNoMoreData(false);
~~~

## 13.AndroidStudio 不能预览

这个问题可能会出现在 1.1.0 版本之前，这个问题并不会影响运行效果，如果无法忍受预览失效请加上
~~~
compile 'com.android.support:design:25.3.1'
~~~

## 14.不显示“加载完成”和“刷新完成”，直接因此 Header或者Footer。

Smart 可以修改 “加载完成”和“刷新完成” 的显示时间，所以想不现实它们，直接把显示时间设置为0即可。

```
header.setFinishDuration(0);//设置Footer 的 “刷新完成” 显示时间为0
footer.setFinishDuration(0);//设置Footer 的 “加载完成” 显示时间为0
```

## 15.RecyclerView，ListVIew，ScrollView，NestScrollView，滚动冲突

1. 如果是 RecyclerView 和 NestScrollView 先尝试打开 Smart 的嵌套滚动功能
2. 如果是 ListVIew，ScrollView，可以尝试 同时打开 ScrollView，ScrollView，Smart 的嵌套滚动功能
3. 如果 1，2 都无效，这需要自定义滚动边界自己实现 canRefresh 和 canLoadMore，自己用代码告诉Smart 什么时候可以 刷新，什么时候可以加载

## 16.finishLoadMoreWithNoMoreData /没有更多数据 账号，Footer 还显示了loading/转圈

1.最常见的原因是 finishLoadMore 和 finishLoadMoreWithNoMoreData 同时调用导致的。
他们都有关闭 Footer 的功能，所以 finishLoadMore 会导致 finishLoadMoreWithNoMoreData 功能异常。
这样解释会比较清楚 finishLoadMoreWithNoMoreData = finishLoadMore + setNoMoreData(true)
所以解决办法是去掉 finishLoadMoreWithNoMoreData 前面的 finishLoadMore 如下：
```java
    //refreshLayout.finishLoadMore(); //前面的 finishLoadMore 要删除
    if(true/*没有更多数据*/) {
        refreshLayout.finishLoadMoreWithNoMoreData();
    } else {
        refreshLayout.finishLoadMore(); //在 else 中添加 finishLoadMore
    }
```

2.少见原因（1.1.0版本以前） 只调用了 setNoMoreData(true) 未调用 finishLoadMore
setNoMoreData 的关闭 Footer 功能是 1.1.0 后面添加的，所以之前的版本
setNoMoreData 必须和 finishLoadMore 一起使用如：
```java
    if(true/*没有更多数据*/) {
        refreshLayout.setNoMoreData(true);
        refreshLayout.finishLoadMore()// setNoMoreData 后面必须加finishLoadMore（1.1.0版本以前）
        //refreshLayout.finishLoadMoreWithNoMoreData(); 也可以用 finishLoadMoreWithNoMoreData 代替上面两行
    } else {
        refreshLayout.finishLoadMore();
    }
```