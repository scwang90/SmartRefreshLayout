package com.scwang.smartrefresh.layout.constant;

public enum RefreshState {
    None,
    PullDownToRefresh, PullToUpLoad,
    PullDownCanceled, PullUpCanceled,
    ReleaseToRefresh, ReleaseToLoad,
    Refreshing, Loading,
    RefreshFinish, LoadFinish,;

    public boolean isAnimating() {
        return this == Refreshing ||
                this == Loading;
    }

    public boolean isDraging() {
        return ordinal() >= PullDownToRefresh.ordinal()
                && ordinal() <= ReleaseToLoad.ordinal()
                && this != PullDownCanceled
                && this != PullUpCanceled;
    }

    public boolean isDragingHeader() {
        return this == PullDownToRefresh ||
                this == ReleaseToRefresh;
    }

    public boolean isDragingFooter() {
        return this == PullToUpLoad ||
                this == ReleaseToLoad;
    }

    public boolean isHeader() {
        return (ordinal() & 1) == 1;
    }

    public boolean isFooter() {
        return (ordinal() & 1) == 0 && ordinal() > 0;
    }

}