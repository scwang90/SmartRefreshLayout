package com.scwang.smartrefresh.layout.util;

import java.lang.ref.WeakReference;

public class PostRunable implements Runnable {
    private WeakReference<Runnable> runnableWeakReference = null;
    public PostRunable(Runnable runnable) {
        this.runnableWeakReference = new WeakReference<>(runnable);
    }
    @Override
    public void run() {
        Runnable runnable = runnableWeakReference.get();
        if (runnable != null) {
            runnable.run();
        }
        runnableWeakReference = null;
    }
}