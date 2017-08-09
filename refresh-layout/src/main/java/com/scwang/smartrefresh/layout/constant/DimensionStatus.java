package com.scwang.smartrefresh.layout.constant;

/**
 * 尺寸值的定义状态，用于在值覆盖的时候决定优先级
 * 越往下优先级越高
 */
public enum DimensionStatus {
    DefaultUnNotify(false),//默认值，但是还没通知确认
    Default(true),//默认值
    XmlWrap(true),//Xml计算
    XmlExact(true),//Xml 的view 指定
    XmlLayoutUnNotify(false),//Xml 的layout 中指定，但是还没通知确认
    XmlLayout(true),//Xml 的layout 中指定
    CodeExactUnNotify(false),//代码指定，但是还没通知确认
    CodeExact(true),//代码指定
    DeadLockUnNotify(false),//锁死，但是还没通知确认
    DeadLock(true);//锁死
    public final boolean notifyed;

    DimensionStatus(boolean notifyed) {
        this.notifyed = notifyed;
    }

    /**
     * 转换为未通知状态
     */
    public DimensionStatus unNotify() {
        if (notifyed) {
            DimensionStatus prev = values()[ordinal() - 1];
            if (!prev.notifyed) {
                return prev;
            }
            return DefaultUnNotify;
        }
        return this;
    }

    /**
     * 转换为通知状态
     */
    public DimensionStatus notifyed() {
        if (!notifyed) {
            return values()[ordinal() + 1];
        }
        return this;
    }

    /**
     * 小于等于
     */
    public boolean canReplaceWith(DimensionStatus status) {
        return ordinal() < status.ordinal() || ((!notifyed || CodeExact == this) && ordinal() == status.ordinal());
    }

    /**
     * 大于等于
     */
    public boolean gteReplaceWith(DimensionStatus status) {
        return ordinal() >= status.ordinal();
    }
}