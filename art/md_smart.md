# 智能之处

智能是什么玩意？有什么用？

>智能主要体现 SmartRefreshLayout 对未知布局的自动识别上，这样可以让我们更高效的实现我们所需的功能，也可以实现一些非寻常的功能。
>下面通过**自定义Header** 和 **嵌套Layout作为内容** 来了解 SmartRefreshLayout 的智能之处。

## 自定义Header

我们来看这一下这个伪代码例子：
```xml
    <SmartRefreshLayout>
        <ClassicsHeader/>
        <TextView/>
        <ClassicsFooter/>
    </SmartRefreshLayout>
```
在Android Studio 中的预览效果图

![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/jpg_preview_xml_define.jpg)

对比代码和我们预想的一样，那我们来对代码做一些改动，ClassicsHeader换成一个简单的TextView，看看会发生什么？
```xml
    <SmartRefreshLayout>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="100dp"
            android:gravity="center"
            android:background="#444"
            android:textColor="#fff"
            android:text="看看我会不会变成Header"/>
        <TextView/>
        <ClassicsFooter/>
    </SmartRefreshLayout>
```
在Android Studio 中的预览效果图 和 运行效果图

![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/jpg_preview_textheader.jpg) ![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/gif_preview_textheader.gif)

这时发现我们我们替换的 TextView 自动就变成了Header，只是它还不会动。要动起来？那么太简单啦，网上随便一搜索就一大堆的 gif 。如这里：[拖拖拖 ~~垃机C4D](http://www.ui.cn/detail/255143.html)，类似的我们还可以找到很多，又如：[环游东京30天：GIF版旅行指南](http://www.xueui.cn/appreciate/motion-design/gif-version-of-tokyo-travel-guide.html)

那我们就选择 [环游东京30天：GIF版旅行指南](http://www.xueui.cn/appreciate/motion-design/gif-version-of-tokyo-travel-guide.html) 中的这张：

![](http://78rbeb.com1.z0.glb.clouddn.com/wp-content/uploads/2017/05/201705031493854833.gif)

接着我们来改代码:

```
compile 'pl.droidsonroids.gif:android-gif-drawable:1.2.3'//一个开源gif控件
```
```xml
    <SmartRefreshLayout xmlns:app="http://schemas.android.com/apk/res-auto"
        app:srlDragRate="0.7"
        app:srlHeaderMaxDragRate="1.3">
        <pl.droidsonroids.gif.GifImageView
            android:layout_width="match_parent"
            android:layout_height="150dp"
            android:scaleType="centerCrop"
            android:src="@mipmap/gif_header_repast"
            app:layout_srlSpinnerStyle="Scale"
            app:layout_srlBackgroundColor="@android:color/transparent"/>
        <ListView/>
        <ClassicsFooter/>
    </SmartRefreshLayout>
```
在 Android Studio 中的预览效果图 和 运行效果图

![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/jpg_preview_gifheader.jpg) ![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/gif_practive_repast.gif)

哈哈！一行Java代码都不用写，就完成了一个自定义的Header

## 嵌套Layout作为内容

如果boos要求在列表的前面**固定**一个广告条怎么办？这好办呀，一般我们会开开心心的下下这样的代码：
```xml
<LinearLayout
    android:orientation="vertical">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:gravity="center"
        android:text="我就是boos要求加上的广告条啦"/>
    <SmartRefreshLayout>
        <ListView/>
    </SmartRefreshLayout>
</LinearLayout>
```
但是在运行下拉刷新的时候，我们发现 Header是在广告条之下的，看着会别扭~，其实我们可以试试另一种方式，把广告条写到 RefreshLayout内部，看看会发生什么？
```xml
<SmartRefreshLayout>
	<LinearLayout
	    android:orientation="vertical">
	    <TextView
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        android:gravity="center"
	        android:text="我就是boos要求加上的广告条啦"/>
	    <ListView/>
	</LinearLayout>
</SmartRefreshLayout>
```
由于伪代码过于简单，而且运行效果过于丑陋，这里还是贴出在实际项目中的实际情况吧~

![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/gif_practive_feedlist.gif) ![](https://github.com/scwang90/SmartRefreshLayout/raw/master/art/gif_practive_smart.gif)

我们注意看右边的图，仔细观察手指触摸的位置和下拉效果。可以看到在**列表已经滚动到中部时，轻微下拉列表是不会触发刷新的，但是如果是触摸固定的布局，则可以触发下拉**。从这里可以看出 SmartRefreshLayout 对滚动边界的判断是动态的，智能的！当然如果 SmartRefreshLayout 的智能还是不能满足你，可以通过 setListener 自己实现滚动边界的判断，更为准确！
