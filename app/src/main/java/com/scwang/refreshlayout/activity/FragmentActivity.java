package com.scwang.refreshlayout.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import static android.R.id.widget_frame;

/**
 * Fragment 容器页面
 * Created by SCWANG on 2017/10/10.
 */

@SuppressWarnings({"UnusedParameters", "unused"})
public class FragmentActivity extends AppCompatActivity {

    protected static final String EXTRA_FRAGMENT = "EXTRA_FRAGMENT";
    private Fragment mFragment;

    //<editor-fold desc="跳转封装">
    private static void start(Context context, Class<?> clazz, Object... params){
        if (context != null && Fragment.class.isAssignableFrom(clazz)) {
            context.startActivity(newIntent(clazz, context, params));
        }
    }

    public static void start(Activity activity, Class<?> clazz, Object... params){
        start((Context) activity, clazz, params);
    }

    public static void start(Fragment fragment, Class<?> clazz, Object... params){
        if (fragment != null) {
            start((Context) fragment.getActivity(), clazz, params);
        }
    }

    private static Intent newIntent(Class<?> clazz, Context context, Object... params) {
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtra(EXTRA_FRAGMENT, clazz.getName());
        return intent;
    }
    //</editor-fold>

    //<editor-fold desc=方法">

    //@InjectExtra(value = EXTRA_FRAGMENT,remark = "Fragment类名")
    protected String mFragmentClazz = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentClazz = getIntent().getStringExtra(EXTRA_FRAGMENT);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(widget_frame);
        setContentView(frameLayout);
        replaceFragment();
    }

    protected void replaceFragment() {
        try {
            mFragment = (Fragment) getFragmentClass().newInstance();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(widget_frame, mFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
//            AfExceptionHandler.handle(e, "AfFragmentActivity Fragment 类型错误：" + mFragmentClazz);
        }
    }

    //<editor-fold desc="反射缓存">
    private static Map<String, Class> typeCache = new HashMap<>();
    private Class<?> getFragmentClass() throws ClassNotFoundException {
        Class type = typeCache.get(mFragmentClazz);
        if (type == null) {
            typeCache.put(mFragmentClazz, type = Class.forName(mFragmentClazz));
        }
        return type;
    }
    //</editor-fold>

    public Fragment getFragment() {
        return mFragment;
    }

    //</editor-fold>


}
