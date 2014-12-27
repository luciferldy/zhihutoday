package com.example.zhihupocket;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.directionalviewpager.DirectionalViewPager;
import com.example.adapter.HotStoriesPagersAdapter;
import com.example.adapter.StoriesAdapter;
import com.example.task.HandleStringAndImage;
import com.example.thread.GetStoriesAndParse;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
/*
 * PagedHeadListView可以用这个开源项目做
 */
public class MainActivity extends FragmentActivity {

	public static final String ZHIHU_API = "http://news-at.zhihu.com/api/3/news/latest";
	public static final String ZHIHU_STORY_API = "http://daily.zhihu.com/story/";
	private Handler main_thread_handler = new Handler();
	private ListView lv_showshortcontent;
	public static File pic_cache;
	private DirectionalViewPager hotstoriespagers;
	private ProgressDialog main_processdialog;
	private SwipeRefreshLayout main_swiperefresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("MainActivity", "nonono!");
		setContentView(R.layout.activity_main);	
		initView();
		//创建缓存目录，程序一启动就创建
		pic_cache = new File(Environment.getExternalStorageDirectory(), "cache");
		if(!pic_cache.exists()){
				pic_cache.mkdir();
		}
	}

	//初始化视图
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void initView(){
		lv_showshortcontent = (ListView)findViewById(R.id.lv_showshortcontent);
		hotstoriespagers = (DirectionalViewPager)findViewById(R.id.hotstoriespagers);
		main_swiperefresh = (SwipeRefreshLayout)findViewById(R.id.main_swipetorefresh);
		main_swiperefresh.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		main_swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				main_swiperefresh.setRefreshing(true);
				Thread getandparseData = new Thread(new GetStoriesAndParse(MainActivity.this, main_thread_handler, main_swiperefresh));
				getandparseData.start();
				Log.v("MainActivity.initView", "开始刷新");
				
			}
		});
		main_swiperefresh.setRefreshing(true);
		// 防止刷新时又被刷新
		main_swiperefresh.setEnabled(false);
		Log.v("MainActivity", "第一遍刷新?");
		Thread getandparseData = new Thread(new GetStoriesAndParse(MainActivity.this, main_thread_handler, main_swiperefresh));
		getandparseData.start();
	}
	
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group){
		// TODO Auto-generated method stub
		//在ui线程中设置listview
		lv_showshortcontent = (ListView)findViewById(R.id.lv_showshortcontent);
		StoriesAdapter loadlistadapter = new StoriesAdapter(getApplicationContext(), stories_group);
		lv_showshortcontent.setAdapter(loadlistadapter);
		hotstoriespagers.setAdapter(new HotStoriesPagersAdapter(getSupportFragmentManager(), topstories_group));
		hotstoriespagers.setVisibility(View.VISIBLE);
		// 添加点击监听器
		hotstoriespagers.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// getDisplayedChild方法获取的是前一个的
				int i = hotstoriespagers.getCurrentItem()+1;
				i = i<5?i:i-5;
				Intent intent = new Intent(MainActivity.this, StoryContent.class);
				intent.putExtra("stories_group", topstories_group);
				// 万万没想到，标记的时候这个是反着来的
				intent.putExtra("story_order", i);
				startActivity(intent);
			}
		});
	    main_swiperefresh.setRefreshing(false);
	    main_swiperefresh.setEnabled(true);;
		HandleStringAndImage.clearImgCache(stories_group, topstories_group, pic_cache);
	}
	
	// 获得图片的名称
	public String getPicNameOfUrl(String name){
		String[] item_group = name.split("/");
		return item_group[item_group.length-1];
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
            	finish();
                return true;
            default:
            	return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() ==0){
			System.exit(0);
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
