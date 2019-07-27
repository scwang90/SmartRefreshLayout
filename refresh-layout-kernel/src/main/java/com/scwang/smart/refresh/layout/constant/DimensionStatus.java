package com.scwang.smart.refresh.layout.constant;

/**
 * 尺寸值的定义状态，用于在值覆盖的时候决定优先级
 * 越往下优先级越高
 */
@SuppressWarnings("WeakerAccess")
public class DimensionStatus {

    public static final DimensionStatus DefaultUnNotify = new DimensionStatus(0,false);//默认值，但是还没通知确认
    public static final DimensionStatus Default = new DimensionStatus(1,true);//默认值
    public static final DimensionStatus XmlWrapUnNotify = new DimensionStatus(2,false);//Xml计算，但是还没通知确认
    public static final DimensionStatus XmlWrap = new DimensionStatus(3,true);//Xml计算
    public static final DimensionStatus XmlExactUnNotify = new DimensionStatus(4,false);//Xml 的view 指定，但是还没通知确认
    public static final DimensionStatus XmlExact = new DimensionStatus(5,true);//Xml 的view 指定
    public static final DimensionStatus XmlLayoutUnNotify = new DimensionStatus(6,false);//Xml 的layout 中指定，但是还没通知确认
    public static final DimensionStatus XmlLayout = new DimensionStatus(7,true);//Xml 的layout 中指定
    public static final DimensionStatus CodeExactUnNotify = new DimensionStatus(8,false);//代码指定，但是还没通知确认
    public static final DimensionStatus CodeExact = new DimensionStatus(9,true);//代码指定
    public static final DimensionStatus DeadLockUnNotify = new DimensionStatus(10,false);//锁死，但是还没通知确认
    public static final DimensionStatus DeadLock = new DimensionStatus(10,true);//锁死

    public final int ordinal;
    public final boolean notified;

    public static final DimensionStatus[] values = new DimensionStatus[]{
            DefaultUnNotify,
            Default,
            XmlWrapUnNotify,
            XmlWrap,
            XmlExactUnNotify,
            XmlExact,
            XmlLayoutUnNotify,
            XmlLayout,
            CodeExactUnNotify,
            CodeExact,
            DeadLockUnNotify,
            DeadLock
    };

    private DimensionStatus(int ordinal,boolean notified) {
        this.ordinal = ordinal;
        this.notified = notified;
    }

    /**
     * 转换为未通知状态
     * @return 未通知状态
     */
    public DimensionStatus unNotify() {
        if (notified) {
            DimensionStatus prev = values[ordinal - 1];
            if (!prev.notified) {
                return prev;
            }
            return DefaultUnNotify;
        }
        return this;
    }

    /**
     * 转换为通知状态
     * @return 通知状态
     */
    public DimensionStatus notified() {
        if (!notified) {
            return values[ordinal + 1];
        }
        return this;
    }

    /**
     * 是否可以被新的状态替换
     * @param status 新转台
     * @return 小于等于
     */
    public boolean canReplaceWith(DimensionStatus status) {
        return ordinal < status.ordinal || ((!notified || CodeExact == this) && ordinal == status.ordinal);
    }

//    /**
//     * 是否没有达到新的状态
//     * @param status 新转台
//     * @return 大于等于 gte
//     */
//    public boolean gteStatusWith(DimensionStatus status) {
//        return ordinal() >= status.ordinal();
//    }
}