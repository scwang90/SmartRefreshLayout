package com.scwang.smartrefreshlayout.constant;

public enum RefreshState {
    None,
    PullDownRefresh, PullUpLoad,
    PullDownCanceled, PullUpCanceled,
    ReleaseRefresh, ReleaseLoad,
    Refreshing, Loading,
    RefreshFinish, LoadingFinish,
}