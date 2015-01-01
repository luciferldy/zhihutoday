package com.example.zhihupocket;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;
import com.example.news.LoadingPreNew;
import com.example.news.LoadingTodayNews;
import com.example.task.StoriesGetTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
/*
 * PagedHeadListView可以用这个开源项目做
 */
public class MainActivity extends FragmentActivity {

	public static final String ZHIHU_API_TODAY = "http://news-at.zhihu.com/api/3/news/latest";
	public static final String ZHIHU_STORY_API = "http://daily.zhihu.com/story/";
	public static final String ZHIHU_API_BEFORE = "http://news.at.zhihu.com/api/3/news/before/";
	public static Calendar sys_calendar;
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("MainActivity", "nonono!");
		setContentView(R.layout.activity_main);
		initImageLoaderConfigurations();
		initPullToRefresh();
		initCalendar();
	}
	
	public void initCalendar(){
		MainActivity.sys_calendar = Calendar.getInstance();
	}
	
	// 初始化下拉菜单
	public void initPullToRefresh(){
		final PullToRefreshScrollView main_swiperefresh = (PullToRefreshScrollView)findViewById(R.id.main_sv);
		
		OnRefreshListener<ScrollView> swiperefresh_listener = new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				// 下拉刷新
				if (PullToRefreshBase.Mode.PULL_FROM_START == main_swiperefresh.getCurrentMode()) {
					new StoriesGetTask(MainActivity.this, new LoadingTodayNews(MainActivity.this, main_swiperefresh), main_swiperefresh, "today").execute();
				}
				// 下拉加载
				else if (PullToRefreshBase.Mode.PULL_FROM_END == main_swiperefresh.getCurrentMode()) {
					new StoriesGetTask(MainActivity.this, new LoadingPreNew(MainActivity.this, main_swiperefresh), main_swiperefresh, "before").execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "出错了……", Toast.LENGTH_SHORT).show();
				}
			}
		};
		main_swiperefresh.setOnRefreshListener(swiperefresh_listener);
		
	}
	
	// 初始化图片加载配置
	public boolean initImageLoaderConfigurations(){
		try {
			//创建缓存目录，程序一启动就创建
			File pic_cache = new File(Environment.getExternalStorageDirectory(), "zhihupocketcache");
			if(!pic_cache.exists()){
					pic_cache.mkdir();
			}
			@SuppressWarnings("deprecation")
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.discCache(new UnlimitedDiscCache(pic_cache))
			.diskCacheSize(50 * 1024 * 1024) // 50 Mb
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
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
            case R.id.action_exit:
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
