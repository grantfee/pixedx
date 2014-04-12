package com.pixbox.adapter;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class MyFragmentPageAdapter extends FragmentPagerAdapter{

	List<Fragment> fragments;
	public MyFragmentPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

	/**
     * 自定义的构造函数
     * @param fm
     * @param fragments2 ArrayList<Fragment>
     */
    public MyFragmentPageAdapter(FragmentManager fm,List<Fragment> fragments2) {
        super(fm);
        this.fragments = fragments2;
    }



	
}
