package com.github.huihuangui.ovalseekbar.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.github.huihuangui.ovalseekbar.OvalSeekBar;

/**
 * 配合ViewPager使用
 *
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class ViewPagerOvalSeekBar extends OvalSeekBar {

    protected ViewPager.OnPageChangeListener mViewPagerListener;

    public ViewPagerOvalSeekBar(Context context) {
        this(context, null);
    }

    public ViewPagerOvalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPager.OnPageChangeListener getViewPagerListener() {
        if (mViewPagerListener == null) {
            mViewPagerListener = new ViewPagerListener();
        }
        return mViewPagerListener;
    }


    private class ViewPagerListener implements ViewPager.OnPageChangeListener {

        private int mPagerCurrent; //结合ViewPager拖拽或翻页时记录当前页面下标

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            dragging(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            if (mPagerCurrent < position) {
                rotate(true);
            } else if (mPagerCurrent > position) {
                rotate(false);
            }
            mPagerCurrent = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                startDrag();
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                startSettle();
            } else {
                endDrag();
            }
        }
    }

    /**
     * ViewPager 拖拽进行
     *
     * @param position
     * @param positionOffset
     * @param pixels
     */
    public void dragging(int position, float positionOffset, int pixels) {
        if (OvalSeekBar.DEBUG_TOUCH) {
            Log.d(OvalSeekBar.TAG, "--drag-- dragging position: " + position + ", positionOffset: "
                    + positionOffset + ", pixels: " + pixels + ", mDragOffset: " + mDragOffset
                    + ", pixels / positionOffset: " + (pixels / positionOffset));
        }

        if (Math.abs(mDragOffset - position) < 0.01) {
            return;
        }
        mDragOffset = positionOffset;
        if (positionOffset != 0 && pixels != 0) {
            float w = pixels / positionOffset;
            if (positionOffset <= 0.5) { //往左拖拽
                dragInner(true, 2);
            } else { //往右拖拽
                dragInner(false, 2);
            }
        }
    }

    /**
     * ViewPager定居开始
     */
    protected void startSettle() {
        if (OvalSeekBar.DEBUG_TOUCH) {
            Log.d(OvalSeekBar.TAG, "--drag-- startSettle: ");
        }
    }

}
