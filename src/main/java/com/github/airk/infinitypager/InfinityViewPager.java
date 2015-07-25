package com.github.airk.infinitypager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by kevin on 15/6/17.
 */
public class InfinityViewPager extends ViewPager {
    public InfinityViewPager(Context context) {
        super(context);
    }

    public InfinityViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof InfinityPagerAdapter)) {
            throw new IllegalArgumentException("Adapter must be InfinityPagerAdapter.");
        }
        super.setAdapter(adapter);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (getAdapter().getCount() != 0) {
            item = getOffsetAmount() + (item % getAdapter().getCount());
        }
        super.setCurrentItem(item, smoothScroll);
    }

    private int getOffsetAmount() {
        if (getAdapter().getCount() == 0)
            return 0;
        if (getAdapter() instanceof InfinityPagerAdapter && ((InfinityPagerAdapter) getAdapter()).isInfinity()) {
            InfinityPagerAdapter adapter = (InfinityPagerAdapter) getAdapter();
            return adapter.getRealCount() * 100;
        }
        return 0;
    }

    @Override
    public int getCurrentItem() {
        if (getAdapter().getCount() == 0)
            return 0;
        int position = super.getCurrentItem();
        if (getAdapter() instanceof InfinityPagerAdapter && ((InfinityPagerAdapter) getAdapter()).isInfinity()) {
            InfinityPagerAdapter adapter = (InfinityPagerAdapter) getAdapter();
            return adapter.getRealPosition(position);
        }
        return super.getCurrentItem();
    }
}
