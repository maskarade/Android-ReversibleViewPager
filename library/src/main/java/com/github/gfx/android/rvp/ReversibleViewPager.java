package com.github.gfx.android.rvp;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Originated from https://github.com/konifar/droidkaigi2016/blob/master/app/src/main/java/io/github/droidkaigi/confsched/widget/RtlViewPager.java
 */
public class ReversibleViewPager extends ViewPager {

    @NonNull
    private final SimpleArrayMap<OnPageChangeListener, ReverseOnPageChangeListener> reverseOnPageChangeListeners
            = new SimpleArrayMap<>();

    @Nullable
    private DataSetObserver dataSetObserver;

    private boolean suppressOnPageChangeListeners;

    private boolean reversed = false;

    public ReversibleViewPager(@NonNull Context context) {
        this(context, null);
    }

    public ReversibleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ReversibleViewPager, 0, 0);
            reversed = a.getBoolean(R.styleable.ReversibleViewPager_reversed, false);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerRtlDataSetObserver(getAdapter());
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterRtlDataSetObserver();
        super.onDetachedFromWindow();
    }

    private void registerRtlDataSetObserver(PagerAdapter adapter) {
        if (adapter instanceof ReverseAdapter && dataSetObserver == null) {
            dataSetObserver = new RevalidateIndicesOnContentChange((ReverseAdapter) adapter);
            adapter.registerDataSetObserver(dataSetObserver);
            ((ReverseAdapter) adapter).revalidateIndices();
        }
    }

    private void unregisterRtlDataSetObserver() {
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof ReverseAdapter && dataSetObserver != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
            dataSetObserver = null;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(convertPosition(item), smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        return convertPosition(super.getCurrentItem());
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(convertPosition(item));
    }

    private static int reversePosition(@NonNull PagerAdapter adapter, int position) {
        return adapter.getCount() - position - 1;
    }

    private int convertPosition(int position) {
        if (position >= 0 && reversed) {
            PagerAdapter adapter = getAdapter();
            return adapter == null ? 0 : reversePosition(adapter, position);
        } else {
            return position;
        }
    }

    /**
     * @return The adapter which {@link #setAdapter(PagerAdapter)} takes
     */
    @Nullable
    public PagerAdapter getRawAdapter() {
        PagerAdapter adapter = getAdapter();
        return adapter instanceof ReverseAdapter ? ((ReverseAdapter) adapter).getInnerAdapter() : adapter;
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        unregisterRtlDataSetObserver();

        boolean rtlReady = adapter != null && reversed;
        if (rtlReady) {
            adapter = new ReverseAdapter(adapter);
            registerRtlDataSetObserver(adapter);
        }
        super.setAdapter(adapter);
        if (rtlReady) {
            setCurrentItemWithoutNotification(0);
        }
    }

    @Override
    public void fakeDragBy(float xOffset) {
        super.fakeDragBy(reversed ? xOffset : -xOffset);
    }

    // to workaround ViewGroup+PhotoView problems
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void setCurrentItemWithoutNotification(int index) {
        suppressOnPageChangeListeners = true;
        setCurrentItem(index, false);
        suppressOnPageChangeListeners = false;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        if (reversed) {
            ReverseOnPageChangeListener reverseListener = new ReverseOnPageChangeListener(listener);
            reverseOnPageChangeListeners.put(listener, reverseListener);
            listener = reverseListener;
        }
        super.addOnPageChangeListener(listener);
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        if (reversed) {
            listener = reverseOnPageChangeListeners.remove(listener);
        }
        super.removeOnPageChangeListener(listener);
    }

    static class RevalidateIndicesOnContentChange extends DataSetObserver {

        @NonNull
        private final ReverseAdapter adapter;

        private RevalidateIndicesOnContentChange(@NonNull ReverseAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            super.onChanged();
            adapter.revalidateIndices();
        }
    }

    class ReverseAdapter extends PagerAdapterWrapper {

        private int lastCount;

        public ReverseAdapter(@NonNull PagerAdapter adapter) {
            super(adapter);
            lastCount = adapter.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(convertPosition(position));
        }

        @Override
        public float getPageWidth(int position) {
            return super.getPageWidth(convertPosition(position));
        }

        @Override
        public int getItemPosition(Object object) {
            int itemPosition = super.getItemPosition(object);
            return itemPosition < 0 ? itemPosition : convertPosition(itemPosition);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, convertPosition(position));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, convertPosition(position), object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, lastCount - position - 1, object);
        }

        private int convertPosition(int position) {
            return reversePosition(this, position);
        }

        private void revalidateIndices() {
            int newCount = getCount();
            if (newCount != lastCount) {
                setCurrentItemWithoutNotification(Math.max(0, lastCount - 1));
                lastCount = newCount;
            }
        }
    }

    class ReverseOnPageChangeListener implements OnPageChangeListener {

        @NonNull
        private final OnPageChangeListener original;

        private int pagerPosition = -1;

        private ReverseOnPageChangeListener(@NonNull OnPageChangeListener original) {
            this.original = original;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!suppressOnPageChangeListeners) {

                if (positionOffset == 0f && positionOffsetPixels == 0) {
                    pagerPosition = convertPosition(position);
                } else {
                    pagerPosition = convertPosition(position + 1);
                }

                original.onPageScrolled(pagerPosition, positionOffset > 0 ? 1f - positionOffset : positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (!suppressOnPageChangeListeners) {
                original.onPageSelected(convertPosition(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (!suppressOnPageChangeListeners) {
                original.onPageScrollStateChanged(state);
            }
        }

        private int convertPosition(int position) {
            PagerAdapter adapter = getAdapter();
            return adapter == null ? position : reversePosition(adapter, position);
        }
    }


    static class PagerAdapterWrapper extends PagerAdapter {

        @NonNull
        private final PagerAdapter adapter;

        protected PagerAdapterWrapper(@NonNull PagerAdapter adapter) {
            this.adapter = adapter;
        }

        @NonNull
        public PagerAdapter getInnerAdapter() {
            return adapter;
        }

        @Override
        public int getCount() {
            return adapter.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return adapter.getPageTitle(position);
        }

        @Override
        public float getPageWidth(int position) {
            return adapter.getPageWidth(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return adapter.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return adapter.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            adapter.destroyItem(container, position, object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            adapter.setPrimaryItem(container, position, object);
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

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            adapter.restoreState(state, loader);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            adapter.startUpdate(container);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            adapter.finishUpdate(container);
        }
    }

}
