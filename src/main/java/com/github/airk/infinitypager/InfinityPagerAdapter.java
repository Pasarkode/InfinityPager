package com.github.airk.infinitypager;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kevin on 15/6/17.
 */
public class InfinityPagerAdapter extends PagerAdapter {
    private final boolean infinity;
    private final PagerAdapter adapter;

    public static InfinityPagerAdapter wrap(PagerAdapter adapter, boolean infinity) {
        return new InfinityPagerAdapter(adapter, infinity);
    }

    private InfinityPagerAdapter(PagerAdapter adapter, boolean infinity) {
        this.adapter = adapter;
        this.infinity = infinity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = getRealPosition(position);
        return adapter.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        position = getRealPosition(position);
        adapter.destroyItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        position = getRealPosition(position);
        adapter.setPrimaryItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        position = getRealPosition(position);
        return adapter.getPageTitle(position);
    }

    @Override
    public float getPageWidth(int position) {
        position = getRealPosition(position);
        return adapter.getPageWidth(position);
    }

    @Override
    public int getCount() {
        if (getRealCount() == 0)
            return 0;
        if (infinity) {
            return Integer.MAX_VALUE;
        } else {
            return adapter.getCount();
        }
    }

    public boolean isInfinity() {
        return infinity;
    }

    public int getRealCount() {
        return adapter.getCount();
    }

    public int getRealPosition(int position) {
        return position % getRealCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return adapter.isViewFromObject(view, o);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        adapter.restoreState(state, loader);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }
}
