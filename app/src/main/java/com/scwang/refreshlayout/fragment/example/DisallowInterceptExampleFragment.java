package com.scwang.refreshlayout.fragment.example;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.*;
import com.scwang.refreshlayout.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisallowInterceptExampleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example_disallow_intercept, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public static class MoveView extends AppCompatTextView {


        //移动的阈值
        private static final int TOUCH_SLOP = 20;
        /**
         * 点击按下事件 X坐标记录
         */
        private float mLastMotionX;
        /**
         * 点击按下事件 Y坐标记录
         */
        private float mLastMotionY;

        private boolean mDelay;

        /**
         * 长按模式的标记位
         */
        private boolean isLongPress;

        /**
         * 长按的runnable
         */
        private Runnable mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                isLongPress = true;
                mDelay = false;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        };

        public MoveView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                    mDelay = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isLongPress) {
                        //长按状态下.绘制时间和轴线
                        setText("x=" + x + ";y=" + y);
                    } else if (mDelay && (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                            || Math.abs(mLastMotionY - y) > TOUCH_SLOP)) {
                        //移动超过阈值，则表示移动了
                        removeCallbacks(mLongPressRunnable);
                        mDelay = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                default:
                    //释放了
                    removeCallbacks(mLongPressRunnable);
                    isLongPress = false;
                    mDelay = false;
                    invalidate();
                    break;
            }
            return true;
        }

    }
}
