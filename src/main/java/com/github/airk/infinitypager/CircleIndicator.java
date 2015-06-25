package com.github.airk.infinitypager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kevin on 15/6/18.
 */
public class CircleIndicator extends ViewIndicator {
    public CircleIndicator(Context context) {
        super(context);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected IndicatorAdapter getIndicatorAdapter() {
        return new DefaultIndicatorAdapter();
    }

    private static class DefaultIndicatorAdapter implements IndicatorAdapter {

        @Override
        public int getIndicatorLayoutRes() {
            return R.layout.indicator_item_circle;
        }

        @Override
        public void initIndicator(View view, int position) {
            view.setScaleX(.7f);
            view.setScaleY(.7f);
        }

        @Override
        public void onSelected(View view, int position, boolean selected) {
            if (selected) {
                view.setScaleX(1f);
                view.setScaleY(1f);
            } else {
                view.setScaleX(.7f);
                view.setScaleY(.7f);
            }
            view.setSelected(selected);
        }

        @Override
        public void onTranslate(@Nullable View current, @Nullable View next, float factor, boolean forward) {
            if (current != null) {
                float scaleXY = 1.0f - factor * 0.3f;
                current.setScaleX(scaleXY);
                current.setScaleY(scaleXY);
            }
            if (next != null) {
                float scaleXY = 0.7f + factor * 0.3f;
                next.setScaleX(scaleXY);
                next.setScaleY(scaleXY);
            }
        }
    }

}
