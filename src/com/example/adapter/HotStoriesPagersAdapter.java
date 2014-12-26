package com.example.adapter;

import com.example.fragment.MainHotStoriesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HotStoriesPagersAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "one", "two", "three", "four", "five"};

	public HotStoriesPagersAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		return MainHotStoriesFragment.newInstance(position);
	}

}