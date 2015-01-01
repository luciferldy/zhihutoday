package com.example.news;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adapter.StoriesAdapter;
import com.example.listener.StoryItemClickListener;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class LoadingPreNew implements LoadingBaseNews{
	private ListView lv_showshortcontent;
	private TextView lv_header;
	private MainActivity main;
	private PullToRefreshScrollView main_swiperefresh;
	private String date;
	
	public LoadingPreNew(MainActivity main, PullToRefreshScrollView main_swiperefresh) {
		// TODO Auto-generated constructor stub
		this.main = main;
		this.main_swiperefresh = main_swiperefresh;
		initView();
		
	}
	// 初始化视图
	@SuppressLint("InlinedApi")
	public void initView(){
		LinearLayout main_ll = (LinearLayout) main.findViewById(R.id.main_rl);
		LayoutInflater layoutInflater = LayoutInflater.from(main);
		RelativeLayout  rl = (RelativeLayout)layoutInflater.inflate(R.layout.main_storiescontent, null);
		lv_showshortcontent = (ListView)rl.getChildAt(1);
		lv_header = (TextView)rl.getChildAt(0);
		main_ll.addView(rl);
	}
	
	// 加载视图
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group){
		// TODO Auto-generated method stub
		//在ui线程中设置listview
		Calendar calendar = MainActivity.sys_calendar;
		String date = String.valueOf(calendar.get(Calendar.YEAR))+
		       String.valueOf(calendar.get(Calendar.MONTH) + 1)+// 获取当前月份  
		       String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		lv_header.setText(date);
		// 将将数据存入数据库 
		StoriesAdapter loadlistadapter = new StoriesAdapter(main.getApplicationContext(), stories_group);
		lv_showshortcontent.setAdapter(loadlistadapter);
		// 设置当互动到当前的listitem时才去加载图片
		lv_showshortcontent.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		lv_showshortcontent.setOnItemClickListener(new StoryItemClickListener(main.getApplicationContext(), stories_group));
	    main_swiperefresh.onRefreshComplete();
	    
	    // 将系统的时间延后一天
	    MainActivity.sys_calendar.add(Calendar.DATE, -1);
	}
	
}
