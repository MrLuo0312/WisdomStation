package com.winsion.component.task.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;
import com.winsion.component.task.R;
import com.winsion.component.task.fragment.patrolplan.PatrolPlanFragment;
import com.winsion.component.task.fragment.problemmanage.ProblemManageFragment;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 网格管理一级界面
 */

public class GridActivity extends BaseActivity {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;

    private final Fragment[] mFragments = {new PatrolPlanFragment(), new ProblemManageFragment()};
    private final int[] mTitles = {R.string.tab_patrol_plan, R.string.tab_problem_manager};

    @SuppressLint("InflateParams")
    @Override
    public int setContentView() {
        return R.layout.basic_fragment_two_pager;
    }

    @Override
    protected void start() {
        initView();
        initAdapter();
    }

    private void initView() {
        vpContent = findViewById(R.id.vp_content);
        mIndicator = findViewById(R.id.mi_container);
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        addOnClickListeners(R.id.iv_back);
    }

    private void initAdapter() {
        vpContent.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mIndicator.setViewPager(vpContent);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTitles[position]);
        }
    }
}
