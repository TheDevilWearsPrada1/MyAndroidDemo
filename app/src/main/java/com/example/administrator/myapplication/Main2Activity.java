package com.example.administrator.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.view.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.bottom;
import static com.example.administrator.myapplication.R.id.listView;


public class Main2Activity extends AppCompatActivity{
    @Bind(listView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_bar)
    RelativeLayout rlBar;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.view_title_bg)
    View viewTitleBg;
    @Bind(R.id.view_action_more_bg)
    View viewActionMoreBg;
    @Bind(R.id.fl_action_more)
    FrameLayout flActionMore;

    private Context mContext;
    private View itemHeaderAdView; // 从ListView获取的广告子View
    private boolean isScrollIdle = true; // ListView是否在滑动
    private int adViewHeight = 0; // 广告视图的高度
    private int adViewTopSpace; // 广告视图距离顶部的距离

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        mContext=this;
        itemHeaderAdView= LayoutInflater.from(this).inflate(R.layout.item, smoothListView, false);
        smoothListView.addHeaderView(itemHeaderAdView);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("haha" + i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);

        smoothListView.setAdapter(arrayAdapter);
        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
//        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnScrollListener(new SmoothListView.OnSmoothScrollListener() {
            @Override
            public void onSmoothScrolling(View view) {}

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isScrollIdle = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScrollIdle && adViewTopSpace < 0) return;

                // 获取广告头部View、自身的高度、距离顶部的高度
                if (itemHeaderAdView == null) {
                    itemHeaderAdView = smoothListView.getChildAt(1-firstVisibleItem);
                    Log.e("自定义控件", ""+(firstVisibleItem));
                }
                if (itemHeaderAdView != null) {
                    adViewTopSpace = DensityUtil.px2dip(mContext, itemHeaderAdView.getTop());
                    adViewHeight = DensityUtil.px2dip(mContext, itemHeaderAdView.getHeight());
                }

                // 处理标题栏颜色渐变
                handleTitleBarColorEvaluate();
            }
        });
    }
    // 处理标题栏颜色渐变
    private void handleTitleBarColorEvaluate() {
        float fraction;
        if (adViewTopSpace > 0) {
            fraction = 1f - adViewTopSpace * 1f / 60;
            if (fraction < 0f) fraction = 0f;
            rlBar.setAlpha(fraction);
            return ;
        }

        float space = Math.abs(adViewTopSpace) * 1f;
        Log.e("自定义控件", "handleTitleBarColorEvaluate() called"+space);
        fraction = space / adViewHeight;
        if (fraction < 0f) fraction = 0f;
        if (smoothListView.getFirstVisiblePosition()>1) fraction = 1f;
        rlBar.setAlpha(1f);

        if (fraction >= 1f) {
            viewTitleBg.setAlpha(0f);
            viewActionMoreBg.setAlpha(0f);
            rlBar.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        } else {
            viewTitleBg.setAlpha(1f - fraction);
            viewActionMoreBg.setAlpha(1f - fraction);
            rlBar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.orange));
        }
    }

}
