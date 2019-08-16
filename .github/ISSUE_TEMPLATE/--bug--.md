---
name: 提交BUG报告
about: 创建一个bug详情报告，以方便我们更好地改进
title: "[不是bug或者不确定是bug，请使用错误报告模板]"
labels: bug
assignees: ''

---

**详细描述**
对问题进行清晰而简明的描述，把握问题的关键点。

**使用版本**
```gradle
implementation 'com.scwang.smartrefresh:SmartRefreshLayout:x.x.x' 
implementation 'com.scwang.smartrefresh:SmartRefreshHeader:x.x.x' 
```
**使用代码**
```java
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
RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
refreshLayout.setOnRefreshListener(new OnRefreshListener() {
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.finishRefresh(2000);
    }
});
refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
    @Override
    public void onLoadMore(RefreshLayout refreshlayout) {
        refreshlayout.finishLoadMore(2000);
    }
});
```

**布局代码**
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
    <com.scwang.smartrefresh.layout.header.ClassicsHeader
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="@dimen/dimenPaddingCommon"
        android:background="@android:color/white"
        android:text="@string/description_define_in_xml"/>
    <com.scwang.smartrefresh.layout.footer.ClassicsFooter
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</com.scwang.smartrefresh.layout.SmartRefreshLayout>
```

**问题重现**
问题重现操作不走:
1. 进入主页 '...'
2.点击按钮 '....'
3.滚动列表 '....'
4.发现问题

**预期行为**
对你期望发生的事情的清晰而简明的描述。

**屏幕截图**
如果适用，添加屏幕截图以帮助解释您的问题。

**设备信息**
请填写一下你运行设备的信息，信息越全越有助于我理解问题
 - 设备名: [e.g. 华为P20]
 - Android版本: [e.g. Android 7.0]
 - 设备型号 [e.g. ]
 - 系统版本（手机厂商定制rom）

**附加信息**
在此处添加任何有关该问题的任何其他说明。
