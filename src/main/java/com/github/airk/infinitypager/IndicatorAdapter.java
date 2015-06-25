package com.github.airk.infinitypager;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by kevin on 15/6/18.
 */
public interface IndicatorAdapter {
    @LayoutRes
    int getIndicatorLayoutRes();

    void initIndicator(View view, int position);

    void onSelected(View view, int position, boolean selected);

    void onTranslate(@Nullable View current, @Nullable View next, float factor, boolean forward);
}
