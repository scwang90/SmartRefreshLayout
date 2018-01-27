package com.scwang.smartrefresh.layout.constant;

/**
 * 刷新状态
 */
@SuppressWarnings("unused")
public enum RefreshState {
    None(0,false),
    PullDownToRefresh(1,true), PullUpToLoad(2,true),
    PullDownCanceled(1,false), PullUpCanceled(2,false),
    ReleaseToRefresh(1,true), ReleaseToLoad(2,true),
    ReleaseToTwoLevel(1, true), TwoLevelReleased(1,false),
    RefreshReleased(1,false), LoadReleased(2,false),
    Refreshing(1,false,true), Loading(2,false,true), TwoLevel(1, false, true),
    RefreshFinish(1,false,false,true), LoadFinish(2,false,false,true), TwoLevelFinish(1,false,false,true),;

    private final int role;
    public final boolean dragging;// 正在拖动状态：PullDownToRefresh PullUpToLoad ReleaseToRefresh ReleaseToLoad ReleaseToTwoLevel
    public final boolean opening;// 正在刷新状态：Refreshing Loading TwoLevel
    public final boolean finishing;//正在完成状态：RefreshFinish LoadFinish TwoLevelFinish

    RefreshState(int role, boolean dragging) {
        this.role = role;
        this.dragging = dragging;
        this.opening = false;
        this.finishing = false;
    }

    RefreshState(int role, boolean dragging, boolean opening) {
        this.role = role;
        this.dragging = dragging;
        this.opening = opening;
        this.finishing = false;
    }

    RefreshState(int role, boolean dragging, boolean opening, boolean finishing) {
        this.role = role;
        this.dragging = dragging;
        this.opening = opening;
        this.finishing = finishing;
    }

    public boolean isHeader() {
        return role == 1;
    }

    public boolean isFooter() {
        return role == 2;
    }

}