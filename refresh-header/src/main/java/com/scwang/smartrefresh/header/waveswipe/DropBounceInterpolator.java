package com.scwang.smartrefresh.header.waveswipe;
/*
 * Copyright (C) 2015 RECRUIT LIFESTYLE CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

/**
 * @author amyu
 *
 * {@link WaveView#mDropBounceHorizontalAnimator} と {@link WaveView#mDropVertexAnimator} にセットするInterpolator
 * WavePullToRefresh/DropBounceInterpolator.gcxにグラフの詳細
 */
public class DropBounceInterpolator implements Interpolator {

    public DropBounceInterpolator() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public DropBounceInterpolator(Context context, AttributeSet attrs) {
    }

    /**
     * {@inheritDoc}
     * @param v 动画帧
     * @return 加速值
     */
    @Override
    public float getInterpolation(float v) {
        //y = -19 * (x - 0.125)^2 + 1.3     (0 <= x < 0.25)
        //y = -6.5 * (x - 0.625)^2 + 1.1    (0.5 <= x < 0.75)
        //y = 0                             (0.25 <= x < 0.5 && 0.75 <= x <=1)

        if (v < 0.25f) {
            return -38.4f * (float) Math.pow(v - 0.125, 2) + 0.6f;
        } else if (v >= 0.5 && v < 0.75) {
            return -19.2f * (float) Math.pow(v - 0.625, 2) + 0.3f;
        } else {
            return 0;
        }
    }
}