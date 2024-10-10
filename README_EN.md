# SmartRefreshLayout

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Arsenal](https://img.shields.io/badge/Arsenal%20-%20SmartRefresh-4cae4c.svg)](https://android-arsenal.com/details/1/6001)
[![JCenter](https://img.shields.io/badge/%20Jcenter%20-2.1.1-5bc0de.svg)](https://bintray.com/scwang90/maven/refresh-layout-kernel/_latestVersion)
[![MinSdk](https://img.shields.io/badge/%20MinSdk%20-%2012%2B%20-f0ad4e.svg)](https://android-arsenal.com/api?level=12)
[![Methods](https://img.shields.io/badge/Methods%20%7C%20Size%20-%20784%20%7C%20122%20KB-d9534f.svg)](http://www.methodscount.com/?lib=com.scwang.smartrefresh%3ASmartRefreshLayout%3A1.1.0)

## English | [中文](README.md)

SmartRefreshLayout's goal is to build a strong, stable and mature pull-down refresh layout framework, and to integrate all kinds of cool, diverse, practical and beautiful Headers and Footers.

## For IOS

SmartRefreshControl：[Link](https://github.com/scwang90/SmartRefreshControl)

## Features

 - Support multi-touch.
 - Support multi-layered nested view structures.
 - Support all the Views（AbsListView、RecyclerView、WebView....View）
 - Support customizing Headers and Footers, and has integrated a lot of cool Headers and Footers.
 - Support synchronous scrolling with ListView and NestedScrolling with CoordinatorLayout.
 - Support automatically refresh, automatically pull-up loading (automatically detect list inertance and scroll to the bottom without having to manually pull).
 - Support customizing rebound animation interpolator, to achieve a variety of cool animation effects.
 - Support setting a theme to fit any scene of App, it won't appear a cool but very awkward situation.
 - Support setting a variety of transformations (Translation, stretching, behind fixed, top fixed, full screen view) for Headers and Footers.
 - Support rewrite and extension, internal implementation without private methods and fields.
 - Support automatically cross-border rebound for all rolling Views (ListView、RecyclerView、ScrollView、WebView...View).
 - Support the interchange of Header and Footer
 - Support AndroidX
 - Support [HorizontalRefresh](https://github.com/scwang90/SmartRefreshHorizontal)
 
## Gateway

 - [Smart place](art/md_smart.md)
 - [Update log](art/md_update.md)
 - [Attribute method](art/md_property.md)
 - [Blog posts](https://segmentfault.com/a/1190000010066071) 
 - [Download the source code](https://github.com/scwang90/SmartRefreshLayout/releases) 
 - [Multi-touch](art/md_multitouch.md) 
## Demo
[Download APK-Demo](art/app-debug.apk)

![](art/png_apk_rqcode.png)

#### Practical
|Weibo|FeedList|
|:---:|:---:|
|![](art/gif_practive_weibo.gif)|![](art/gif_practive_feedlist.gif)|

|Repast|Profile|
|:---:|:---:|
|![](art/gif_practive_repast.gif)|![](art/gif_practive_profile.gif)|

#### Style

|Style|Delivery|DropBox|
|:---:|:---:|:---:|
|Demo|![](art/gif_Delivery.gif)|![](art/gif_Dropbox.gif)|
|Design|[Refresh-your-delivery](https://dribbble.com/shots/2753803-Refresh-your-delivery)|[DropBox-Refresh](https://dribbble.com/shots/3470499-DropBox-Refresh)|

The two above headers are implemented by myself, The following headers are collected and optimized from excellent Headers on github

|Style|BezierRadar|BezierCircle|
|:---:|:---:|:---:|
|Demo|![](art/gif_BezierRadar.gif)|![](art/gif_BezierCircle.gif)|
|From|[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout/blob/master/art/gif_recyclerview2.gif)|[Pull Down To Refresh](https://dribbble.com/shots/1797373-Pull-Down-To-Refresh)|

|Style|FlyRefresh|Classics|
|:---:|:---:|:---:|
|Demo|![](art/gif_FlyRefresh.gif)|![](art/gif_Classics.gif)|
|From|[FlyRefresh](https://github.com/race604/FlyRefresh)|[ClassicsHeader](#1)|

|Style|Phoenix|Taurus|
|:---:|:---:|:---:|
|Demo|![](art/gif_Phoenix.gif)|![](art/gif_Taurus.gif)|
|From|[Yalantis/Phoenix](https://github.com/Yalantis/Phoenix)|[Yalantis/Taurus](https://github.com/Yalantis/Taurus)

|Style|BattleCity|HitBlock|
|:---:|:---:|:---:|
|Demo|![](art/gif_BattleCity.gif)|![](art/gif_HitBlock.gif)|
|From|[FunGame/BattleCity](https://github.com/Hitomis/FunGameRefresh)|[FunGame/HitBlock](https://github.com/Hitomis/FunGameRefresh)

|Style|WaveSwipe|Material|
|:---:|:---:|:---:|
|Demo|![](art/gif_WaveSwipe.gif)|![](art/gif_Material.gif)|
|From|[WaveSwipeRefreshLayout](https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout)|[MaterialHeader](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)

|Style|StoreHouse|WaterDrop|
|:---:|:---:|:---:|
|Demo|![](art/gif_StoreHouse.gif)|![](art/gif_WaterDrop.gif)|
|From|[Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)|[WaterDrop](https://github.com/THEONE10211024/WaterDropListView)


See so many cool headers, is not it feel great? At this point you may be worried that so many headers together, but usually only use one, is not to introduce a lot of useless code and resources?    
Please rest assured that I have divided it into eight packages, when used to reference their own it!

 - refresh-layout-kernel        core
 - refresh-header-classics      ClassicsHeader
 - refresh-header-radar         BezierRadarHeader
 - refresh-header-falsify       FalsifyHeader
 - refresh-header-material      MaterialHeader
 - refresh-header-two-level     TwoLevelHeader
 - refresh-footer-ball          BallPulseFooter
 - refresh-footer-classics      ClassicsFooter

## Usage
#### 1.Add a gradle dependency.

V 2.x changed the package name relative to 1.x, such as `com.scwang.smartrefresh` to `com.scwang.smart.refresh`.
It is suggested that in the new project, if the old project is upgraded,
the package name should be replaced, which is more troublesome.
But the main change is to subcontract SmartRefreshLayout to reduce unnecessary dependencies and avoid code redundancy.
However, there is no subcontracting to SmartRefreshHeader.
There are more than ten headers in it.
It is recommended that you copy the source code into the project whenever you need to use it.

```
// Note: There will be no default Header and Footer after subcontracting. It needs to be added manually!
implementation  'com.scwang.smart:refresh-layout-kernel:2.1.1'      //core
implementation  'com.scwang.smart:refresh-header-classics:2.1.1'    //ClassicsHeader
implementation  'com.scwang.smart:refresh-header-radar:2.1.1'       //BezierRadarHeader
implementation  'com.scwang.smart:refresh-header-falsify:2.1.1'     //FalsifyHeader
implementation  'com.scwang.smart:refresh-header-material:2.1.1'    //MaterialHeader
implementation  'com.scwang.smart:refresh-header-two-level:2.1.1'   //TwoLevelHeader
implementation  'com.scwang.smart:refresh-footer-ball:2.1.1'        //BallPulseFooter
implementation  'com.scwang.smart:refresh-footer-classics:2.1.1'    //ClassicsFooter

```

If you use AndroidX, add it to gradle.properties

```
android.useAndroidX=true
android.enableJetifier=true
```

#### 2.Add SmartRefreshLayout in the layout xml.
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.scwang.smart.refresh.layout.SmartRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <com.scwang.smart.refresh.header.ClassicsHeader
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:overScrollMode="never"
        android:background="#fff" />
    <com.scwang.smart.refresh.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</com.scwang.smart.refresh.layout.SmartRefreshLayout>
```

#### 3.Coding in the Activity or Fragment.
```java
RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
refreshLayout.setRefreshHeader(new ClassicsHeader(this));
refreshLayout.setRefreshFooter(new ClassicsFooter(this));
refreshLayout.setOnRefreshListener(new OnRefreshListener() {
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
    }
});
refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
    @Override
    public void onLoadMore(RefreshLayout refreshlayout) {
        refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
    }
});
```

## Use the specified Header and Footer

#### 1.Global settings
```java
public class App extends Application {
    public void onCreate() {
        super.onCreate();
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
                @Override
                public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                    return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
                }
            });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
                @Override
                public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                    return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
                }
            });
    }
}
```

Note: this method is the lowest priority.


#### 2.Specified in the XML layout file
```xml
<com.scwang.smart.refresh.layout.SmartRefreshLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#444444"
    app:srlPrimaryColor="#444444"
    app:srlAccentColor="@android:color/white"
    app:srlEnablePreviewInEditMode="true">
    <!--srlAccentColor and srlPrimaryColor, Will change the Header and Footer theme colors-->
    <!--srlEnablePreviewInEditMode, Can open and close the preview function-->
    <com.scwang.smart.refresh.header.ClassicsHeader
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="@dimen/dimenPaddingCommon"
        android:background="@android:color/white"
        android:text="@string/description_define_in_xml"/>
    <com.scwang.smart.refresh.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</com.scwang.smart.refresh.layout.SmartRefreshLayout>
```

Note: this method of priority is medium。When using this method, the Android Studio will have preview effect, the following figure:

![](art/jpg_preview_xml_define.jpg)

But don't worry, just a preview effect, run only the drop-down will appear.

#### 3.Specified in the java code
```java
final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
```

## ProGuard

This library does't use serialization and deserialization, JNI, reflection, so there is no need to add confusing filtering code, and it has been confusing tests pass, if you after the confusion in the use of the project appear problem, please inform me.

## Donate

If you like this library's design, feel it help to you, you can point the upper right corner "Star" support Thank you! ^ _ ^
You can also click the PayPal below to ask the author to drink a cup of coffee.

[![](https://www.paypalobjects.com/webstatic/i/logo/rebrand/ppcom.svg 'click to donate')](https://www.paypal.me/scwang90)

If in the donation message note name, it will be record to the list  
[Donation list](art/md_donationlist.md)

#### blogroll
[github/Loror](https://github.com/Loror)  
[github/faith-hb/WidgetCase](https://github.com/faith-hb/WidgetCase)  
[github/Bamboy120315/Freedom](https://github.com/Bamboy120315/Freedom)  
[github/TommyLemon/APIJSON](https://github.com/TommyLemon/APIJSON)  
[github/dengyuhan](https://github.com/dengyuhan)  
[github/zrp2017](https://github.com/zrp2017)  
[github/fly803/BaseProject](https://github.com/fly803/BaseProject)  
[github/razerdp](https://github.com/razerdp)  
[github/SuperChenC/s-mvp](https://github.com/SuperChenC/s-mvp)  
[github/KingJA/LoadSir](https://github.com/KingJA/LoadSir)  
[github/jianshijiuyou](https://github.com/jianshijiuyou)  
[github/zxy198717](https://github.com/zxy198717)  
[github/addappcn](https://github.com/addappcn)  
[github/RainliFu](https://github.com/RainliFu)  
[github/sugarya](https://github.com/sugarya)  
[github/stormzhang](https://github.com/stormzhang)  

## Discuss

Contact me: scwang90@hotmail.com 

## Thanks
[SwipeRefreshLayout](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)  
[Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)  
[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout)  
[BeautifulRefreshLayout](https://github.com/android-cjj/BeautifulRefreshLayout)

## Other Works
[MultiWaveHeader](https://github.com/scwang90/MultiWaveHeader)  
[SmartRefreshHorizontal](https://github.com/scwang90/SmartRefreshHorizontal)  

License
-------

    Copyright 2017 scwang90

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
