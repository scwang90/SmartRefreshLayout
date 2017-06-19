# SmartRefreshLayout

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

![](art/gif_BezierRadarHeader.gif)
![](art/gif_BattleCity.gif)
![](art/gif_BezierRadarHeader.gif)
![](art/gif_Circle.gif)
![](art/gif_HitBlock.gif)
![](art/gif_Material.gif)
![](art/gif_Phoenix.gif)
![](art/gif_StoreHouse.gif)
![](art/gif_Taurus.gif)
![](art/gif_WaterDrop.gif)
![](art/gif_WaveSwipe.gif)