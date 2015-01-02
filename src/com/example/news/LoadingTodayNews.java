package com.example.news;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.HotStoriesPagersAdapter;
import com.example.adapter.StoriesAdapter;
import com.example.listener.StoryItemClickListener;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class LoadingTodayNews implements LoadingBaseNews{
	
	private ViewPager hotstoriespagers;
	private ListView lv_showshortcontent;
	private TextView tv_todaynews;
	private MainActivity main;
	private PullToRefreshScrollView main_swiperefresh; 
	
	public LoadingTodayNews(MainActivity main, PullToRefreshScrollView main_swiperefresh) {
		// TODO Auto-generated constructor stub
		this.main = main;
		this.main_swiperefresh = main_swiperefresh;
		initView();
	}
	// 初始化视图
	@SuppressLint({ "InlinedApi", "InflateParams" })
	public void initView(){
		
		LinearLayout main_ll = (LinearLayout) main.findViewById(R.id.main_rl);
		// 清除layout里面的视图
		main_ll.removeAllViewsInLayout();
		
		LayoutInflater layoutInflater = LayoutInflater.from(main);
		LinearLayout ll = (LinearLayout)layoutInflater.inflate(R.layout.main_hotstoriescontent, null);
		hotstoriespagers = (ViewPager)ll.getChildAt(0);
		tv_todaynews = (TextView)ll.getChildAt(1);
		lv_showshortcontent = (ListView)ll.getChildAt(2);
		main_ll.addView(ll);
		
	}
	
	// 加载视图
	@Override
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group){
		// TODO Auto-generated method stub
		//在ui线程中设置listview
		Calendar calendar = MainActivity.sys_calendar;
		String date = String.valueOf(calendar.get(Calendar.YEAR))+"年"+
		       String.valueOf(calendar.get(Calendar.MONTH) + 1)+"月"+// 获取当前月份  
		       String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"日";// 获取当前月份的日期号码
		tv_todaynews.setText(date);
		
		StoriesAdapter loadlistadapter = new StoriesAdapter(main.getApplicationContext(), stories_group);
		lv_showshortcontent.setAdapter(loadlistadapter);
		
		// 设置当互动到当前的listitem时才去加载图片
		lv_showshortcontent.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		lv_showshortcontent.setOnItemClickListener(new StoryItemClickListener(main.getApplicationContext(), stories_group));
		hotstoriespagers.setAdapter(new HotStoriesPagersAdapter(main.getSupportFragmentManager(), topstories_group));
		hotstoriespagers.setVisibility(View.VISIBLE);
		// 添加点击监听器
		hotstoriespagers.setOnClickListener(new View.OnClickListener() {	
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// getDisplayedChild方法获取的是前一个的
				Log.v("MainActivity", "viewpagerclick!");
				int i = hotstoriespagers.getCurrentItem()+1;
				i = i<5?i:i-5;
				Intent intent = new Intent(main, StoryContent.class);
				intent.putExtra("stories_group", topstories_group);
				// 万万没想到，标记的时候这个是反着来的
				intent.putExtra("story_order", i);
				main.startActivity(intent);
			}
		});
	    main_swiperefresh.onRefreshComplete();
	    // 更新时间,时间延后一天
	    MainActivity.sys_calendar.add(Calendar.DATE, -1);
	}
	
}
