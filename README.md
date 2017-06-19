# SmartRefreshLayout
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/scwang90/maven/SmartRefreshLayout/images/download.svg) ](https://bintray.com/scwang90/maven/SmartRefreshLayout/_latestVersion)

&emsp;&emsp;正如名字所说，这是一个“聪明的下拉刷新布局”，由于它的聪明，他不只是如其他的刷新布局所说的支持所有的View，还支持多层嵌套的视图结构，下文会对这个详细说明。

&emsp;&emsp;除了“聪明”之外，SmartRefreshLayout还具备了很多的特点。它继承至ViewGroup 而不是其他的Layout，提高了性能。

&emsp;&emsp;吸取了现在流行的各种刷新布局的优点，包括谷歌官方的 SwipeRefreshLayout，现在非常流行的 [TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout) 、[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)。还集成了各种炫酷的 Header 和 Footer。

&emsp;&emsp;下面列出了SwipeRefreshLayout所有的特点功能:

 - 支持所有的 View（AbsListView、RecyclerView、WebView....View） 和多层嵌套的 Layout（详细）
 - 支持自定义并且已经集成了很多炫酷的 Header 和 Footer （ 图）.
 - 支持和ListView的同步滚动 和 RecyclerView、AppBarLayout、CoordinatorLayout 的嵌套滚动 NestedScrolling.
 - 支持在Android Studio Xml 编辑器中预览 效果（图）
 - 支持分别在 Default（默认）、Xml、JavaCode、中设置 Header 和 Footer.
 - 支持自动刷新、自动上拉加载（自动检测列表滚动到底部，而不用手动上拉）.r.
 - 支持通用的刷新监听器 OnRefreshListener 和更详细的滚动监听 OnMultiPurposeListener.
 - 支持自定义回弹动画的插值器，实现各种炫酷的动画效果.
 - 支持设置主题来适配任何场景的App，不会出现炫酷但很尴尬的情况.
 
## Demo
[下载 APK-Demo](art/app-debug.apk)

![](art/gif_BezierRadar.gif) 
![](art/gif_Circle.gif)
![](art/gif_Phoenix.gif)
![](art/gif_Taurus.gif)
![](art/gif_BattleCity.gif)
![](art/gif_HitBlock.gif)
![](art/gif_WaveSwipe.gif)
![](art/gif_Material.gif)
![](art/gif_StoreHouse.gif)
![](art/gif_WaterDrop.gif)
![](art/gif_Classics.gif)
![](art/gif_FlyRefresh.gif)


## 简单用例
#### 1.添加依赖
```
compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.0-alpha-1'
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

To get better effect, you'd better add code `android:overScrollMode="never"` to the childView.

#### 3.在 Activity 或者 Fragment 中添加代码
```
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

