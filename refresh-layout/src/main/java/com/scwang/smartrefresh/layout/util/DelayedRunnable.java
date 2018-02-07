package com.scwang.smartrefresh.layout.util;

public class DelayedRunnable implements Runnable {
    public long delayMillis;
    public Runnable runnable = null;
    public DelayedRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
    public DelayedRunnable(Runnable runnable, long delayMillis) {
        this.runnable = runnable;
        this.delayMillis = delayMillis;
    }
    @Override
    public void run() {
        try {
            if (runnable != null) {
                runnable.run();
                runnable = null;
            }
        } catch (Throwable e) {
            if (!(e instanceof NoClassDefFoundError)) {
                e.printStackTrace();
            }
        }
    }
}