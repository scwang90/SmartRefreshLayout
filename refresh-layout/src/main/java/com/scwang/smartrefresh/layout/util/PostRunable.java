package com.scwang.smartrefresh.layout.util;

public class PostRunable implements Runnable {
    Runnable runnable = null;
    public PostRunable(Runnable runnable) {
        this.runnable = runnable;
    }
    @Override
    public void run() {
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                runnable = null;
            }
        }
    }
}