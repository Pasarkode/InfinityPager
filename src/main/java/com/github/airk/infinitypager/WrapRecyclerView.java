package com.github.airk.infinitypager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kevin on 15/6/17.
 */
public class WrapRecyclerView extends RecyclerView {
    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);
        if (getAdapter() == null) {
            widthSize = 0;
            heightSize = 0;
        } else {
            View view = getChildAt(0);
            if (view != null) {
                int nw = view.getMeasuredWidth() * getAdapter().getItemCount();
                if (nw < widthSize) {
                    widthSize = nw;
                }
                heightSize = view.getMeasuredHeight();
            }
        }
        int newWSpec = View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.getMode(widthSpec));
        int newHSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.getMode(heightSpec));
        super.onMeasure(newWSpec, newHSpec);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        //TODO how better to let view resize itself really
        postDelayed(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        }, 22);
    }
}
