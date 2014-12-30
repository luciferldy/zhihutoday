package news;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.adapter.HotStoriesPagersAdapter;
import com.example.adapter.StoriesAdapter;
import com.example.listener.StoryItemClickListener;
import com.example.task.GetStoriesAndParseTask;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
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
	@SuppressLint("InlinedApi")
	public void initView(){
		lv_showshortcontent = (ListView)main.findViewById(R.id.lv_todaynews);
		hotstoriespagers = (ViewPager)main.findViewById(R.id.hotstoriespagers);
		tv_todaynews = (TextView)main.findViewById(R.id.tv_todaynews);
	}
	
	// 加载视图
	@Override
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group){
		// TODO Auto-generated method stub
		//在ui线程中设置listview
		tv_todaynews.setText(formatDate(MainActivity.end_date.get(0)));
		
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
	}
	
	public String formatDate(String str){
		if (str.length()==8) {
			return str.substring(0, 4)+"年"+str.substring(4, 6)+"月"+str.substring(6, 8)+"日";
		}
		else {
			return str;
		}
	}
	
}
