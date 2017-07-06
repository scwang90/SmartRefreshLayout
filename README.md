# SmartRefreshLayout
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Download](https://api.bintray.com/packages/scwang90/maven/SmartRefreshLayout/images/download.svg) ](https://bintray.com/scwang90/maven/SmartRefreshLayout/_latestVersion)

&emsp;&emsp;正如名字所说，这是一个“聪明”的下拉刷新布局，由于它的聪明，他不只是如其他的刷新布局所说的支持所有的View，还支持多层嵌套的视图结构，下文会对这个详细说明。

&emsp;&emsp;除了“聪明”之外，SmartRefreshLayout还具备了很多的特点。它继承至ViewGroup 而不是其他的Layout，提高了性能。

&emsp;&emsp;吸取了现在流行的各种刷新布局的优点，包括谷歌官方的 SwipeRefreshLayout，现在非常流行的 [TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout) 、[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)。还集成了各种炫酷的 Header 和 Footer。

&emsp;&emsp;下面列出了SmartRefreshLayout所有的特点功能:

 - 支持所有的 View（AbsListView、RecyclerView、WebView....View） 和多层嵌套的 Layout（详细）
 - 支持自定义并且已经集成了很多炫酷的 Header 和 Footer （图）.
 - 支持和ListView的同步滚动 和 RecyclerView、AppBarLayout、CoordinatorLayout 的嵌套滚动 NestedScrolling.
 - 支持在Android Studio Xml 编辑器中预览 效果（图）
 - 支持分别在 Default（默认）、Xml、JavaCode 三个中设置 Header 和 Footer.
 - 支持自动刷新、自动上拉加载（自动检测列表滚动到底部，而不用手动上拉）.
 - 支持通用的刷新监听器 OnRefreshListener 和更详细的滚动监听 OnMultiPurposeListener.
 - 支持自定义回弹动画的插值器，实现各种炫酷的动画效果.
 - 支持设置主题来适配任何场景的App，不会出现炫酷但很尴尬的情况.
 - 支持设置多种滑动方式来适配各种效果的Header和Footer：平移、拉伸、背后固定、顶层固定、全屏
 - 支持内容尺寸自适应 Content-wrap_content
 - 支持继承重写和扩展功能，内部实现没有 private 方法和字段，继承之后都可以重写覆盖
 - 支持越界回弹（Listview、RecyclerView、ScrollView、WebView...View）
 
## 传送门

 - [博客文章](https://segmentfault.com/a/1190000010066071) 
 - [聪明之处](art/md_smart.md)
 
## Demo
[下载 APK-Demo](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/app-debug.apk)

#### 项目演示
![](art/gif_practive_weibo.gif) 
![](art/gif_practive_feedlist.gif)

![](art/gif_practive_repast.gif) 
![](art/gif_practive_profile.gif)

#### 风格演示
![](art/gif_Delivery.gif) 
![](art/gif_Dropbox.gif)

上面这两个是我自己实现的Header，设计来自下面两个网址：[Refresh-your-delivery](https://dribbble.com/shots/2753803-Refresh-your-delivery)，[Dropbox-Refresh](https://dribbble.com/shots/3470499-Dropbox-Refresh)

下面的Header是我把github上其他优秀的Header进行的整理和集合还有优化：

![](art/gif_BezierRadar.gif) 
![](art/gif_Circle.gif)

整理来自：[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout/blob/master/art/gif_recyclerview2.gif)，[Pull Down To Refresh](https://dribbble.com/shots/1797373-Pull-Down-To-Refresh)

![](art/gif_FlyRefresh.gif)
![](art/gif_Classics.gif)

整理来自：[FlyRefresh](https://github.com/race604/FlyRefresh)，[ClassicsHeader](#1)

![](art/gif_Phoenix.gif)
![](art/gif_Taurus.gif)

整理来自：[Yalantis/Phoenix](https://github.com/Yalantis/Phoenix)，[Yalantis/Taurus](https://github.com/Yalantis/Taurus)

![](art/gif_BattleCity.gif)
![](art/gif_HitBlock.gif)

整理来自：[FunGame/BattleCity](https://github.com/Hitomis/FunGameRefresh)，[FunGame/HitBlock](https://github.com/Hitomis/FunGameRefresh)

![](art/gif_WaveSwipe.gif)
![](art/gif_Material.gif)

整理来自：[WaveSwipeRefreshLayout](https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout)，[MaterialHeader](#1)

![](art/gif_StoreHouse.gif)
![](art/gif_WaterDrop.gif)

整理来自：[Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)，[WaterDrop](https://github.com/THEONE10211024/WaterDropListView)


&emsp;&emsp;看到这么多炫酷的Header，是不是觉得很棒？这时你或许会担心这么多的Header集成在一起，但是平时只会用到一个，是不是要引入很多无用的代码和资源？

&emsp;&emsp;请放心，我已经把刷新布局分成三个包啦，用到的时候自行引用就可以啦！

 - SmartRefreshLayout 刷新布局核心实现，自带ClassicsHeader（经典）、BezierRadarHeader（贝塞尔雷达）两个 Header.
 - SmartRefreshHeader 各种Header的集成，除了Layout自带的Header，其他都在这个包中.
 - SmartRefreshFooter 各种Footer的集成，除了Layout自带的Footer，其他都在这个包中.

## 简单用例
#### 1.在 buld.gradle 中添加依赖
```
compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.1'
compile 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.1'//如果使用了特殊的Header
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
        @Override
        public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
            return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
        }
    });
//设置全局的Footer构建器
SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
        @Override
        public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
            return new ClassicsFooter(context);//指定为经典Footer，默认是 BallPulseFooter
        }
    });
```

注意：方法一 设置的Header和Footer的优先级是最低的，如果同时还使用了方法二、三，将会被其他方法取代


#### 2.方法二 XML布局文件指定
```xml
    <com.scwang.smartrefresh.layout.SmartRefreshLayout
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/smartLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#444444"
        app:srlPrimaryColor="#444444"
        app:srlAccentColor="@android:color/white"
        app:srlEnablePreviewInEditMode="true">
        <!--srlAccentColor srlPrimaryColor 将会改变 Header 和 Footer 的主题颜色-->
        <!--srlEnablePreviewInEditMode 可以开启和关闭预览功能-->
        <com.scwang.smartrefresh.layout.header.ClassicsHeader
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:srlClassicsSpinnerStyle="FixedBehind"/>
        <!--FixedBehind可以让Header固定在内容的背后，下拉的时候效果同微信浏览器的效果-->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="@dimen/padding_common"
            android:background="@android:color/white"
            android:text="@string/description_define_in_xml"/>
        <com.scwang.smartrefresh.layout.footer.ClassicsFooter
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:srlClassicsSpinnerStyle="FixedBehind"/>
        <!--FixedBehind可以让Footer固定在内容的背后，下拉的时候效果同微信浏览器的效果-->
    </com.scwang.smartrefresh.layout.SmartRefreshLayout>
```

注意：方法二 XML设置的Header和Footer的优先级是中等的，会被方法三覆盖。而且使用本方法的时候，Android Studio 会有预览效果，如下图：

![](art/jpg_preview_xml_define.jpg)

不过不用担心，只是预览效果，运行的时候只有下拉才会出现~

#### 3.方法三 Java代码设置
```java
final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.smartLayout);
//设置 Header 为 Material风格
refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
//设置 Footer 为 球脉冲
refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

```


## 属性 Attributes

|名称-name|格式-format|描述-description|
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
|srlDisableContentWhenRefresh|boolean|是否在刷新的时候禁止内容的一切手势操作（默认false）|
|srlDisableContentWhenLoading|boolean|是否在加载的时候禁止内容的一切手势操作（默认false）|

## 方法 Method

|名称-name|格式-format|描述-description|
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
|finishRefresh|(int delayed)|完成刷新，结束刷新动画|
|finishLoadmore|(int delayed)|完成加载，结束加载动画|
|getRefreshHeader|RefreshHeader|获取Header|
|getRefreshFooter|RefreshFooter|获取Footer|
|getState|RefreshState|获取当前状态|
|isRefreshing|boolean|是否正在刷新|
|isLoading|boolean|是否正在加载|
|autoRefresh|(int delayed)|触发自动刷新|
|autoLoadmore|(int delayed)|触发自动加载|
