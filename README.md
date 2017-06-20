# SmartRefreshLayout
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/scwang90/maven/SmartRefreshLayout/images/download.svg) ](https://bintray.com/scwang90/maven/SmartRefreshLayout/_latestVersion)

&emsp;&emsp;正如名字所说，这是一个“聪明的下拉刷新布局”，由于它的聪明，他不只是如其他的刷新布局所说的支持所有的View，还支持多层嵌套的视图结构，下文会对这个详细说明。

&emsp;&emsp;除了“聪明”之外，SmartRefreshLayout还具备了很多的特点。它继承至ViewGroup 而不是其他的Layout，提高了性能。

&emsp;&emsp;吸取了现在流行的各种刷新布局的优点，包括谷歌官方的 SwipeRefreshLayout，现在非常流行的 [TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout) 、[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)。还集成了各种炫酷的 Header 和 Footer。

&emsp;&emsp;下面列出了SwipeRefreshLayout所有的特点功能:

 - 支持所有的 View（AbsListView、RecyclerView、WebView....View） 和多层嵌套的 Layout（详细）
 - 支持自定义并且已经集成了很多炫酷的 Header 和 Footer （图）.
 - 支持和ListView的同步滚动 和 RecyclerView、AppBarLayout、CoordinatorLayout 的嵌套滚动 NestedScrolling.
 - 支持在Android Studio Xml 编辑器中预览 效果（图）
 - 支持分别在 Default（默认）、Xml、JavaCode、中设置 Header 和 Footer.
 - 支持自动刷新、自动上拉加载（自动检测列表滚动到底部，而不用手动上拉）.
 - 支持通用的刷新监听器 OnRefreshListener 和更详细的滚动监听 OnMultiPurposeListener.
 - 支持自定义回弹动画的插值器，实现各种炫酷的动画效果.
 - 支持设置主题来适配任何场景的App，不会出现炫酷但很尴尬的情况.
 - 支持设置多种滑动方式来适配各种效果的Header和Footer：位置平移、尺寸拉伸、背后固定、顶层固定、全屏
 - 支持内容尺寸自适应 Content-wrap_content
 - [1.语法示例](#1)
 
## Demo
[下载 APK-Demo](art/app-debug.apk)

![](art/gif_BezierRadar.gif) 
![](art/gif_Circle.gif)
![](art/gif_FlyRefresh.gif)
![](art/gif_Classics.gif)
![](art/gif_Phoenix.gif)
![](art/gif_Taurus.gif)
![](art/gif_BattleCity.gif)
![](art/gif_HitBlock.gif)
![](art/gif_WaveSwipe.gif)
![](art/gif_Material.gif)
![](art/gif_StoreHouse.gif)
![](art/gif_WaterDrop.gif)


&emsp;&emsp;看到这么多炫酷的Header，是不是觉得很棒？这时你或许会担心这么多的Header集成在一起，但是平时只会用到一个，是不是要引入很多无用的代码和资源？

&emsp;&emsp;请放心，我已经把刷新布局分成三个包啦，用到的时候自行引用就可以啦！

 - SmartRefreshLayout 刷新布局核心实现，自带ClassicsHeader（经典）、BezierRadarHeader（贝塞尔雷达）两个 Header.
 - SmartRefreshHeader 各种Header的集成，除了Layout自带的Header，其他都在这个包中.
 - SmartRefreshFooter 各种Footer的集成，除了Layout自带的Footer，其他都在这个包中.

## 简单用例
#### 1.在 buld.gradle 中添加依赖
```
compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.0-alpha-1'
compile 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.0-alpha-1'//如果使用了特殊的Header
compile 'com.scwang.smartrefresh:SmartRefreshFooter:1.0.0-alpha-1'//如果使用了特殊的Footer
```

#### 2.在XML布局文件中添加 SmartRefreshLayout
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.scwang.smartrefresh.layout.SmartRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:overScrollMode="never"
        android:background="#fff" />
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
```

#### 3.在 Activity 或者 Fragment 中添加代码
```java
RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
refreshLayout.setOnRefreshListener(new OnRefreshListener() {
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.finishRefresh(2000);
    }
});
refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
    @Override
    public void onLoadmore(SmartRefreshLayout refreshlayout) {
        refreshlayout.finishLoadmore(2000);
    }
});
```

## 使用指定的 Header 和 Footer

#### 1.方法一 全局设置
```java
//设置全局的Header构建器
SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
//设置全局的Footer构建器
SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context);//指定为经典Footer，默认是 BallPulseFooter
            }
        });
```

注意：方法一 设置的Header和Footer的优先级是最低的，如果同时还使用了方法二、三，将会被其他方法取代


#### 1.方法二 XML布局文件指定
```xml
<com.scwang.smartrefresh.layout.SmartRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:overScrollMode="never"
        android:background="#fff" />
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
```

注意：方法一 设置的Header和Footer的优先级是最低的，如果同时还使用了方法二、三，将会被其他方法取代
