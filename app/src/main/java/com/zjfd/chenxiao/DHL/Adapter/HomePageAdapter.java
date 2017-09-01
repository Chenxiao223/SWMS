package com.zjfd.chenxiao.DHL.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zjfd.chenxiao.DHL.Fragment.ChukuFragment;
import com.zjfd.chenxiao.DHL.Fragment.PandianFragment;
import com.zjfd.chenxiao.DHL.Fragment.PosterFragment;
import com.zjfd.chenxiao.DHL.Fragment.RukuFragment;
import com.zjfd.chenxiao.DHL.Fragment.ShangjiaFragment;
import com.zjfd.chenxiao.DHL.Fragment.YiweiFragment;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class HomePageAdapter extends FragmentPagerAdapter {
    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new PosterFragment();
        }else if(position==1){
            return new RukuFragment();
        }else if(position==2){
            return new ShangjiaFragment();
        }else if(position==3){
            return new YiweiFragment();
        }else if(position==4){
            return new ChukuFragment();
        }else {
            return new PandianFragment();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}
