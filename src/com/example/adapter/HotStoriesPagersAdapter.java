package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.fragment.MainHotStoriesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HotStoriesPagersAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "one", "two", "three", "four", "five"};
	private ArrayList<HashMap<String, Object>> top_stories = new ArrayList<HashMap<String, Object>>();
	
	public HotStoriesPagersAdapter(FragmentManager fm, ArrayList<HashMap<String, Object>> top_stories) {
		super(fm);
		this.top_stories = top_stories;
		MainHotStoriesFragment.initDisplayImageOptions();
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
		return MainHotStoriesFragment.newInstance(position, top_stories);
	}

}