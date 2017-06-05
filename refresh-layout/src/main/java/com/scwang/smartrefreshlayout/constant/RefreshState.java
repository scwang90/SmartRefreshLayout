package com.scwang.smartrefreshlayout.constant;

public enum RefreshState {
    None,
    PullDownRefresh, PullUpLoad,
    PullDownCanceled, PullUpCanceled,
    ReleaseToRefresh, ReleaseToLoad,
    Refreshing, Loading,
    RefreshFinish, LoadingFinish,
}