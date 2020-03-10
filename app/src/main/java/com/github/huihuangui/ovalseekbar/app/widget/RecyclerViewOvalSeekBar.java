package com.github.huihuangui.ovalseekbar.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;

import com.github.huihuangui.ovalseekbar.OvalSeekBar;

/**
 * 配合RecyclerView使用, demo只支持CarouselLayoutManager
 *
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class RecyclerViewOvalSeekBar extends OvalSeekBar {

    protected RecyclerView.OnScrollListener mRecyclerViewListener;

    public RecyclerViewOvalSeekBar(Context context) {
        this(context, null);
    }

    public RecyclerViewOvalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView.OnScrollListener getRecyclerViewListener() {
        if (mRecyclerViewListener == null) {
            mRecyclerViewListener = new RecyclerViewListener();
        }
        return mRecyclerViewListener;
    }

    private class RecyclerViewListener extends RecyclerView.OnScrollListener {

        private int mPagerCurrent; //结合RecyclerView拖拽或翻页时记录当前页面下标

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (OvalSeekBar.DEBUG_TOUCH) {
                Log.d(OvalSeekBar.TAG, "onScrollStateChanged newState: " + newState);
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                doRotate(recyclerView);
                endDrag();
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                startDrag();
            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (OvalSeekBar.DEBUG_TOUCH) {
                Log.d(OvalSeekBar.TAG, "onScrolled dx: " + dx + ", dy: " + dy);
            }
            if (!bIsRotating) {
                if (bIsDragging) {
                    dragging(dx);
                }
            }

        }

        private void doRotate(RecyclerView recyclerView) {
            int position = ((CarouselLayoutManager) recyclerView.getLayoutManager())
                    .getCenterItemPosition();
            if (OvalSeekBar.DEBUG_TOUCH) {
                Log.d(OvalSeekBar.TAG, "onScrollStateChanged position: " + position
                        + ", mPagerCurrent: " + mPagerCurrent);
            }
            if (mPagerCurrent < position) {
                rotate(true);
            } else if (mPagerCurrent > position) {
                rotate(false);
            }
            mPagerCurrent = position;
        }
    }

    /**
     * 横向拖拽
     *
     * @param dx
     */
    public void dragging(int dx) {
        if (OvalSeekBar.DEBUG_TOUCH) {
            Log.d(OvalSeekBar.TAG, "--drag-- dragging dx: " + dx);
        }
        boolean left;
        if (dx > 0) {
            left = true;
        } else {
            left = false;
        }
        dragInner(left, 2);
    }
}
