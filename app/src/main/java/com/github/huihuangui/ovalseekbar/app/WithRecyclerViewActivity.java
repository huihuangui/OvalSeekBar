package com.github.huihuangui.ovalseekbar.app;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;

import com.github.huihuangui.ovalseekbar.OvalSeekBar;
import com.github.huihuangui.ovalseekbar.app.widget.RecyclerViewOvalSeekBar;
/**
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class WithRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private OvalSeekBar mOvalSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_recycler_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mOvalSeekBar = (RecyclerViewOvalSeekBar) findViewById(R.id.oval_seek_bar);
        mRecyclerView.addOnScrollListener((
                (RecyclerViewOvalSeekBar) mOvalSeekBar).getRecyclerViewListener());
        CarouselLayoutManager clm = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        clm.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        mRecyclerView.addOnScrollListener(new CenterScrollListener());
        mRecyclerView.setLayoutManager(clm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ImageView tv = new ImageView(WithRecyclerViewActivity.this);
            return new MyViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
            viewHolder.tv.setImageResource(Images.DEMO_IMAGES[i]);
        }

        @Override
        public int getItemCount() {
            return Images.DEMO_IMAGES.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView tv;

            public MyViewHolder(ImageView v) {
                super(v);
                tv = v;
            }
        }

    }
}
