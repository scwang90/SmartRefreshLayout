package com.scwang.smartrefresh.layout.constant;

@SuppressWarnings("unused")
public enum RefreshState {
    None(0,false),
    PullDownToRefresh(1,true), PullToUpLoad(2,true),
    PullDownCanceled(1,false), PullUpCanceled(2,false),
    ReleaseToRefresh(1,true), ReleaseToLoad(2,true),
    ReleaseToTwoLevel(1, true), TwoLevelReleased(1,false),
    RefreshReleased(1,false), LoadReleased(2,false),
    Refreshing(1,false,true), Loading(2,false,true), TwoLevel(1, false, true),
    RefreshFinish(1,false,false,true), LoadFinish(2,false,false,true), TwoLevelFinish(1,false,false,true),;

    private final int role;
    public final boolean draging;
    public final boolean opening;
    public final boolean finishing;

    RefreshState(int role, boolean draging) {
        this.role = role;
        this.draging = draging;
        this.opening = false;
        this.finishing = false;
    }

    RefreshState(int role, boolean draging, boolean opening) {
        this.role = role;
        this.draging = draging;
        this.opening = opening;
        this.finishing = false;
    }

    RefreshState(int role, boolean draging, boolean opening, boolean finishing) {
        this.role = role;
        this.draging = draging;
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