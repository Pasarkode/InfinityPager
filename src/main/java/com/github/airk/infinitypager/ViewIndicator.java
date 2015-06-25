package com.github.airk.infinitypager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;

/**
 * Created by kevin on 15/6/17.
 */
public abstract class ViewIndicator extends LinearLayout {
    private WrapRecyclerView indicatorContainer;
    protected IndicatorAdapter indicatorAdapter;

    public ViewIndicator(Context context) {
        this(context, null);
    }

    public ViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        indicatorContainer = new WrapRecyclerView(getContext());
        indicatorContainer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorContainer.setLayoutParams(lp);
        addView(indicatorContainer);

        indicatorAdapter = getIndicatorAdapter();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    protected abstract IndicatorAdapter getIndicatorAdapter();

    public void setPager(ViewPager pager) {
        if (indicatorAdapter == null) {
            throw new IllegalArgumentException("IndicatorAdapter has not set yet. Please set it or just use other ViewIndicator like CircleIndicator and so on.");
        }
        setPager(pager, indicatorAdapter);
    }

    private void setPager(ViewPager pager, IndicatorAdapter indicatorAdapter) {
        if (!(pager.getAdapter() instanceof InfinityPagerAdapter)) {
            throw new IllegalArgumentException("Must be InfinityPagetAdapter!");
        }
        InfinityPagerAdapter parentAdapter = (InfinityPagerAdapter) pager.getAdapter();
        AdapterInternal adapterInternal = new AdapterInternal(indicatorContainer, pager, getContext(), parentAdapter, indicatorAdapter);
        indicatorContainer.setAdapter(adapterInternal);
    }

    private static class AdapterInternal extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewPager.OnPageChangeListener {
        private final IndicatorAdapter indicatorAdapter;
        private final RecyclerView parent;
        private final ViewPager pager;
        private final Context context;
        private final InfinityPagerAdapter adapter;
        private HashMap<Integer, RecyclerView.ViewHolder> activeVH;
        private int lastPrimaryPosition = -1;

        private AdapterInternal(RecyclerView parent, ViewPager pager, Context context, InfinityPagerAdapter adapter, IndicatorAdapter indicatorAdapter) {
            this.parent = parent;
            this.pager = pager;
            this.context = context;
            this.adapter = adapter;
            this.indicatorAdapter = indicatorAdapter;
            this.pager.addOnPageChangeListener(this);
            activeVH = new HashMap<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(indicatorAdapter.getIndicatorLayoutRes(), parent, false);
            return new InnerHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (lastPrimaryPosition == -1)
                lastPrimaryPosition = pager.getCurrentItem();
            indicatorAdapter.initIndicator(holder.itemView, position);
            if (position == pager.getCurrentItem()) {
                indicatorAdapter.onSelected(holder.itemView, position, true);
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int key = holder.getAdapterPosition();
            activeVH.put(key, holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            int key = holder.getAdapterPosition();
            activeVH.remove(key);
        }

        @Override
        public int getItemCount() {
            return adapter.getRealCount();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = adapter.getRealPosition(position);
            boolean backward = lastPrimaryPosition > realPosition;
            Log.d("TAG", "Last: " + lastPrimaryPosition + " Position: " + realPosition + " offset: " + positionOffset);
            View current;
            View next;
            if (!backward) {
                current = activeVH.get(realPosition) == null ? null : activeVH.get(realPosition).itemView;
                next = activeVH.get(realPosition + 1) == null ? null : activeVH.get(realPosition + 1).itemView;
            } else {
                current = activeVH.get(lastPrimaryPosition) == null ? null : activeVH.get(lastPrimaryPosition).itemView;
                next = activeVH.get(realPosition) == null ? null : activeVH.get(realPosition).itemView;
            }
            indicatorAdapter.onTranslate(current, next, backward ? 1f - positionOffset : positionOffset, !backward);
        }

        @Override
        public void onPageSelected(int position) {
            final int realPosition = adapter.getRealPosition(position);
            if ((lastPrimaryPosition == 0 && realPosition == getItemCount() - 1) ||
                    (lastPrimaryPosition == getItemCount() - 1 && realPosition == 0)) {
                parent.scrollToPosition(realPosition);
                RecyclerView.ViewHolder prev = activeVH.get(lastPrimaryPosition);
                if (prev != null) {
                    indicatorAdapter.onSelected(prev.itemView, lastPrimaryPosition, false);
                }
                lastPrimaryPosition = realPosition;
                pager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(realPosition);
                    }
                }, 200);
                return;
            }
            Log.d("TAG", "onPageSelected position: " + realPosition);
            int newPrimary = realPosition;
            int oldPrimary = lastPrimaryPosition;
            RecyclerView.ViewHolder old = activeVH.get(oldPrimary);
            RecyclerView.ViewHolder newp = activeVH.get(newPrimary);
            if (old != null) {
                indicatorAdapter.onSelected(old.itemView, oldPrimary, false);
            }
            if (newp != null) {
                indicatorAdapter.onSelected(newp.itemView, newPrimary, true);
            }

            int firstVisibleAdapterPosition = parent.getChildAdapterPosition(parent.getChildAt(0));
            int lastVisibleAdapterPosition = parent.getChildAdapterPosition(parent.getChildAt(parent.getChildCount() - 1));
            boolean forward = realPosition - lastPrimaryPosition > 0;
            lastPrimaryPosition = realPosition;

            if (forward && realPosition - firstVisibleAdapterPosition > parent.getChildCount() / 2) {
                parent.scrollToPosition(lastVisibleAdapterPosition + 1);
            } else if (!forward && lastVisibleAdapterPosition - realPosition > parent.getChildCount() / 2) {
                parent.scrollToPosition(firstVisibleAdapterPosition - 1);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        static class InnerHolder extends RecyclerView.ViewHolder {
            public InnerHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
