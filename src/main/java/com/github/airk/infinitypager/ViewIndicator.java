package com.github.airk.infinitypager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by kevin on 15/7/22.
 */
public abstract class ViewIndicator extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private InfinityViewPager mPager;
    private InfinityPagerAdapter mPagerAdapter;
    private IndicatorAdapter mIndicatorAdapter;
    private Indicator mSelectedIndicator;
    private LinearLayout mContainer;
    private int mLastScrollX;
    private int mLastPrimaryItem;

    private class Indicator {
        View view;
        int position;
        Indicator prev;
    }

    public ViewIndicator(Context context) {
        this(context, null);
    }

    public ViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(true);
        setHorizontalScrollBarEnabled(false);
        mIndicatorAdapter = getIndicatorAdapter();
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mContainer);
    }

    protected abstract IndicatorAdapter getIndicatorAdapter();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setPager(InfinityViewPager pager) {
        mPager = pager;
        mPagerAdapter = (InfinityPagerAdapter) pager.getAdapter();
        initIndicatorViews();
    }

    private void initIndicatorViews() {
        int count = mPagerAdapter.getRealCount();
        for (int i = 0; i < count; i++) {
            View indicator = LayoutInflater.from(getContext())
                    .inflate(mIndicatorAdapter.getIndicatorLayoutRes(), this, false);
            mContainer.addView(indicator);
            mIndicatorAdapter.initIndicator(indicator, i);
        }
        mPager.addOnPageChangeListener(this);
        mSelectedIndicator = new Indicator();
        int realPosition = getRealPosition(mPager.getCurrentItem());
        scrollToChild(realPosition, 0);
        setSelectedIndicator(mContainer.getChildAt(realPosition), realPosition);
    }

    private int getRealPosition(int position) {
        return mPagerAdapter.getRealPosition(position);
    }

    private void setSelectedIndicator(View v, int position) {
        if (mSelectedIndicator.view != v) {
            if (mSelectedIndicator.view != null) {
                mSelectedIndicator.prev = new Indicator();
                mSelectedIndicator.prev.view = mSelectedIndicator.view;
                mSelectedIndicator.prev.position = mSelectedIndicator.position;
            }
            mSelectedIndicator.view = v;
            mSelectedIndicator.position = position;
            mIndicatorAdapter.onSelected(mSelectedIndicator.view, mSelectedIndicator.position, true);
        }
        if (mSelectedIndicator.prev != null) {
            mIndicatorAdapter.onSelected(mSelectedIndicator.prev.view,
                    mSelectedIndicator.prev.position, false);
        }
    }

    private void scrollToChild(int position, int offset) {
        View selected = mContainer.getChildAt(position);
        int half = (getWidth() - selected.getWidth()) / 2;
        View match = findViewByLeft(selected.getLeft() - half - selected.getWidth());
        if (match == null)
            return;

        View target;
        if (match == selected)
            offset = 0;
        int diff = selected.getLeft() - match.getLeft();
        if (diff <= 0) {
            target = selected;
        } else if (diff < half) {
            return;
        } else {
            target = match;
        }

        int newScrollX = target.getLeft() + offset;

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private View findViewByLeft(int left) {
        if (left < 0) {
            left = 0;
        }
        int count = mContainer.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = mContainer.getChildAt(i);
                int cLeft = child.getLeft();
                if (cLeft >= left) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition = getRealPosition(position);
        boolean backward = mLastPrimaryItem > realPosition;

        View current;
        View next;
        if (!backward) {
            current = mContainer.getChildAt(realPosition);
            next = mContainer.getChildAt(realPosition + 1);
        } else {
            current = mContainer.getChildAt(mLastPrimaryItem);
            next = mContainer.getChildAt(realPosition);
        }
        mIndicatorAdapter.onTranslate(current, next, backward ? 1f - positionOffset : positionOffset, !backward);
        int offset = (int) (positionOffset * mContainer.getChildAt(realPosition).getWidth());
        scrollToChild(realPosition, offset);
    }

    @Override
    public void onPageSelected(int position) {
        mLastPrimaryItem = getRealPosition(position);
        setSelectedIndicator(mContainer.getChildAt(mLastPrimaryItem), mLastPrimaryItem);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
