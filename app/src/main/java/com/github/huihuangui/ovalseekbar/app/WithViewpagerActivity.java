package com.github.huihuangui.ovalseekbar.app;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.huihuangui.ovalseekbar.app.widget.ViewPagerOvalSeekBar;

/**
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class WithViewpagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerOvalSeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_viewpager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSeekBar = (ViewPagerOvalSeekBar) findViewById(R.id.oval_seek_bar);
        mViewPager.addOnPageChangeListener(mSeekBar.getViewPagerListener());
        mViewPager.setAdapter(new MyPagerAdapter());
    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Images.DEMO_IMAGES.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView tv = new ImageView(WithViewpagerActivity.this);
            tv.setImageResource(Images.DEMO_IMAGES[position]);
            container.addView(tv);
            return tv;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}
