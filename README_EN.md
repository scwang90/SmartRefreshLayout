# Android Smart Refresh Layout Framework

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Arsenal](https://img.shields.io/badge/%20Arsenal%20-%20SmartRefresh%20-4cae4c.svg?style=flat)](https://android-arsenal.com/details/1/6001)
[![Jcenter](https://img.shields.io/badge/%20Jcenter%20-1.0.3-5bc0de.svg) ](https://bintray.com/scwang90/maven/SmartRefreshLayout/_latestVersion) 
[![MinSdk](https://img.shields.io/badge/%20MinSdk%20-%2012%2B%20-f0ad4e.svg?style=flat)](https://android-arsenal.com/api?level=12)
[![Methods](https://img.shields.io/badge/%20Methods%20%7C%20Size%20-%201251%20%7C%20129%20KB-d9534f.svg)](http://www.methodscount.com/?lib=com.scwang.smartrefresh%3ASmartRefreshLayout%3A1.0.2)

<!-- [![Platform](https://img.shields.io/badge/Platform-Android-f0ad4e.svg)](https://www.android.com) -->
<!-- [![Author](https://img.shields.io/badge/Author-scwang90-11bbff.svg)](https://github.com/scwang90) -->


## English | [中文](README.md)

As the name says, SmartRefreshLayout is a "smart" refresh layout，Because of its "smart", it does not just support all the View as other refresh layouts said, but also support multi-layered nested view structures.   
In addition to "smart", SmartRefreshLayout also has a lot of features.  
It extends from ViewGroup rather than the other FrameLayout or LinearLayout, improving performance.  
It absorbs the advantages of various refresh layout in fashion now，Including Google official [SwipeRefreshLayout](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)、[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout) 、[Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)。Also integrates various cool Header and Footer.  
SmartRefreshLayout's goal is to build a strong, stable and mature refresh layout framework, and integrate all kinds of cool and diverse, practical, beautiful Header and Footer.

## Features

 - Support all of the View (AbsListView RecyclerView WebView... View) and  multi-layered nested view structures.
 - Support custom Header and Footer, and has integrated a lot of cool Header and Footer.
 - Support synchronous scrolling with ListView and NestedScrolling width RecyclerView、AppBarLayout、CoordinatorLayout.
 - Support preview in the Android Studio xml editor.
 - Support three ways (default、xml、java) to set the Header and Footer.
 - Support automatic refresh, automatic loading (automatic detection list, scroll to the bottom without having to manually pull).
 - Support generic refresh listener (OnRefreshListener) and more detailed scrolling listener (OnMultiPurposeListener).
 - Support custom rebound animation interpolator, to achieve a variety of cool animation effects.
 - Support set the theme to fit any scene of App, won't appear cool but very awkward situation.
 - Support for setting a variety of transformations (Translation, stretching, behind the fixed, top fixed, full screen) for Header and Footer.
 - Support content size adaptation (wrap_content).
 - Support rewrite and extension, internal implementation without private methods and fields.
 - Support cross-border rebound (Listview、RecyclerView、ScrollView、WebView...View).
 
## Gateway

 - [Smart place](art/md_smart.md)
 - [Update log](art/md_update.md)
 - [Attribute method](art/md_property.md)
 - [Blog posts](https://segmentfault.com/a/1190000010066071) 
 - [Download the source code](https://github.com/scwang90/SmartRefreshLayout/releases) 
 
## Demo
[Download APK-Demo](art/app-debug.apk)

![](art/png_apk_rqcode.png)

#### Practical
![](art/gif_practive_weibo.gif) ![](art/gif_practive_feedlist.gif)

![](art/gif_practive_repast.gif) ![](art/gif_practive_profile.gif)

#### Style
![](art/gif_Delivery.gif) ![](art/gif_Dropbox.gif)

The two above is my own implementation of the Header, the design comes from: [Refresh-your-delivery](https://dribbble.com/shots/2753803-Refresh-your-delivery)，[Dropbox-Refresh](https://dribbble.com/shots/3470499-Dropbox-Refresh)

![](art/gif_BezierRadar.gif) ![](art/gif_Circle.gif)

[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout/blob/master/art/gif_recyclerview2.gif)，[Pull Down To Refresh](https://dribbble.com/shots/1797373-Pull-Down-To-Refresh)

![](art/gif_FlyRefresh.gif) ![](art/gif_Classics.gif)

[FlyRefresh](https://github.com/race604/FlyRefresh)，[ClassicsHeader](#1)

![](art/gif_Phoenix.gif) ![](art/gif_Taurus.gif)

[Yalantis/Phoenix](https://github.com/Yalantis/Phoenix)，[Yalantis/Taurus](https://github.com/Yalantis/Taurus)

![](art/gif_BattleCity.gif) ![](art/gif_HitBlock.gif)

[FunGame/BattleCity](https://github.com/Hitomis/FunGameRefresh)，[FunGame/HitBlock](https://github.com/Hitomis/FunGameRefresh)

![](art/gif_WaveSwipe.gif) ![](art/gif_Material.gif)

[WaveSwipeRefreshLayout](https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout)，[MaterialHeader](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)

![](art/gif_StoreHouse.gif) ![](art/gif_WaterDrop.gif)

[Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)，[WaterDrop](https://github.com/THEONE10211024/WaterDropListView)


See so many cool headers, is not it feel great? At this point you may be worried that so many headers together, but usually only use one, is not to introduce a lot of useless code and resources?    
Please rest assured that I have divided it into three packages, when used to reference their own it!

 - **SmartRefreshLayout:** The core to realize，Bring ClassicsHeader and BezierRadarHeader.
 - **SmartRefreshHeader:** Integration of various kinds of the Header.
 - **SmartRefreshFooter:** Integration of various kinds of the Footer.

## Usage
#### 1.Add a gradle dependency.
```
compile 'com.android.support:appcompat-v7:25.3.1'
compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.3'
compile 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.3'//If you use the special Header
```

#### 2.Add SmartRefreshLayout in the layout xml.
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

#### 3.Coding in the Activity or Fragment.
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
    public void onLoadmore(RefreshLayout refreshlayout) {
        refreshlayout.finishLoadmore(2000);
    }
});
```

## Use the specified Header and Footer

#### 1.Global settings
```java
public class App extends Application {
    public void onCreate() {
        super.onCreate();
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
                @Override
                public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                    return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
                }
            });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
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
<com.scwang.smartrefresh.layout.SmartRefreshLayout
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
    <com.scwang.smartrefresh.layout.header.ClassicsHeader
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="@dimen/padding_common"
        android:background="@android:color/white"
        android:text="@string/description_define_in_xml"/>
    <com.scwang.smartrefresh.layout.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
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
You can also scan the qr code below to ask the author to drink a cup of coffee.

![](art/pay_alipay.jpg) ![](art/pay_wxpay.jpg) ![](art/pay_tencent.jpg)

If in the donation message note name, will be record to the list  
[Donation list](art/md_donationlist.md)

## Discuss

Contact me: scwang90@hotmail.com 

## Thanks
[SwipeRefreshLayout](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)  
[TwinklingRefreshLayout](https://github.com/lcodecorex/TwinklingRefreshLayout)  
[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)  

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