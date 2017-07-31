package android.support.v4.view;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

@SuppressWarnings("deprecation")
public class PagerAdapterWrapper extends PagerAdapter {

    protected PagerAdapter wrapped = null;

    public PagerAdapterWrapper(PagerAdapter wrapped) {
        this.wrapped = wrapped;
    }

    public void attachViewPager(ViewPager viewPager) {
        //viewPager.mAdapter = this;
        try {
            Field[] fields = ViewPager.class.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (PagerAdapter.class.equals(field.getType())) {
                        field.setAccessible(true);
                        field.set(viewPager, this);
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setViewPagerObserver(DataSetObserver observer) {
        super.setViewPagerObserver(observer);
    }

    @Override
    public int getCount() {
        return wrapped.getCount();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        wrapped.startUpdate(container);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return wrapped.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        wrapped.destroyItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        wrapped.setPrimaryItem(container, position, object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        wrapped.finishUpdate(container);
    }

    @Override
    @Deprecated
    public void startUpdate(View container) {
        wrapped.startUpdate(container);
    }

    @Override
    @Deprecated
    public Object instantiateItem(View container, int position) {
        return wrapped.instantiateItem(container, position);
    }

    @Override
    @Deprecated
    public void destroyItem(View container, int position, Object object) {
        wrapped.destroyItem(container, position, object);
    }

    @Override
    @Deprecated
    public void setPrimaryItem(View container, int position, Object object) {
        wrapped.setPrimaryItem(container, position, object);
    }

    @Override
    @Deprecated
    public void finishUpdate(View container) {
        wrapped.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return wrapped.isViewFromObject(view, object);
    }

    @Override
    public Parcelable saveState() {
        return wrapped.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        wrapped.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(Object object) {
        return wrapped.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        wrapped.notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        wrapped.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        wrapped.unregisterDataSetObserver(observer);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return wrapped.getPageTitle(position);
    }

    @Override
    public float getPageWidth(int position) {
        return wrapped.getPageWidth(position);
    }
}