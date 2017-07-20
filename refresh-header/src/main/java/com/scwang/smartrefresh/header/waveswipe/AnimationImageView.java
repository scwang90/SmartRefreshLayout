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

package com.scwang.smartrefresh.header.waveswipe;

import android.content.Context;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * @author amyu
 */
public class AnimationImageView extends ImageView {

  /**
   * AnimationのStartとEnd時にListenerにアレする
   */
  private Animation.AnimationListener mListener;

  /**
   * コンストラクタ
   * {@inheritDoc}
   */
  public AnimationImageView(Context context) {
    super(context);
  }

  /**
   * {@link AnimationImageView#mListener} のセット
   *
   * @param listener {@link android.view.animation.Animation.AnimationListener}
   */
  public void setAnimationListener(Animation.AnimationListener listener) {
    mListener = listener;
  }

  /**
   * ViewのAnimationのStart時にセットされたListenerの {@link android.view.animation.Animation.AnimationListener#onAnimationStart(Animation)}
   * を呼ぶ
   */
  @Override public void onAnimationStart() {
    super.onAnimationStart();
    if (mListener != null) {
      mListener.onAnimationStart(getAnimation());
    }
  }

  /**
   * ViewのAnimationのEnd時にセットされたListenerの {@link android.view.animation.Animation.AnimationListener#onAnimationEnd(Animation)}
   * (Animation)} を呼ぶ
   */
  @Override public void onAnimationEnd() {
    super.onAnimationEnd();
    if (mListener != null) {
      mListener.onAnimationEnd(getAnimation());
    }
  }
}