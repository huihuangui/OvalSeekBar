package com.github.huihuangui.ovalseekbar.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.huihuangui.ovalseekbar.OvalSeekBar;

/**
 * @author Hui Huangui
 * @email: huihuangui@yeah.net
 */
public class MainActivity extends AppCompatActivity {

    private Button mRotateLeftButton;
    private Button mRotateRightButton;
    private OvalSeekBar mOvalSeekBar;

    private Button mWithViewPagerButton;
    private Button mWithRecyclerViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRotateLeftButton = (Button) findViewById(R.id.rotate_left);
        mRotateRightButton = (Button) findViewById(R.id.rotate_right);
        mOvalSeekBar = (OvalSeekBar) findViewById(R.id.oval_seek_bar);
        mWithViewPagerButton = (Button) findViewById(R.id.with_viewpager);
        mWithRecyclerViewButton = (Button) findViewById(R.id.with_recyclerview);

        mWithViewPagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WithViewpagerActivity.class);
                startActivity(intent);
            }
        });

        mWithRecyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WithRecyclerViewActivity.class);
                startActivity(intent);
            }
        });

        mRotateLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOvalSeekBar.rotate(true);
            }
        });

        mRotateRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOvalSeekBar.rotate(false);
            }
        });

    }
}
