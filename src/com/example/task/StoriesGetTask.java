package com.example.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.example.db.StoriesHandleSQLite;
import com.example.db.TopStoriesHandleSQLite;
import com.example.news.LoadingBaseNews;
import com.example.zhihupocket.MainActivity;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.os.AsyncTask;
import android.widget.Toast;

public class StoriesGetTask extends AsyncTask<Void, Integer, Boolean>{
	private String json_data;
	private ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
	private LoadingBaseNews loadingnews;
	private MainActivity main;
	private PullToRefreshScrollView main_swiperefresh;
	private String query_type;
	
	public StoriesGetTask(MainActivity main, LoadingBaseNews loadingnews, 
			PullToRefreshScrollView main_swiperefresh, String query_type) {
		// TODO Auto-generated constructor stub
		this.loadingnews = loadingnews;
		this.main_swiperefresh = main_swiperefresh;
		this.main = main;
		this.query_type = query_type;
	}
	
	@Override  
    protected void onPreExecute() { 
    }
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (query_type.equals("today")) {
			return getTodayNews();
		}
		else {
			return getPreNews();
		}
	}
	
	// 获得今天的数据
	public boolean getTodayNews(){
		// 获得今天的消息
		Calendar calendar = Calendar.getInstance();
		TopStoriesHandleSQLite top = new TopStoriesHandleSQLite(main, calendar);
		StoriesHandleSQLite general = new StoriesHandleSQLite(main, calendar);
		// 先尝试从网络上获取今日的消息
		if (getTodayNewsFromOnLine()) {
			
			// 存入数据库
			if (top.storedTopStoriesIntoDB(topstories_group)&&general.storedStoriesIntoDB(stories_group)) {
				// 在runviews之后进行修改系统时间
				return true;
			}
			return false;
		}
		// 从数据库中查询有没有软件
		else {
			topstories_group = (top).getTopStoriesFromDB();
			stories_group = (general).getStoriesFromDB();
			if (stories_group != null && topstories_group != null) {
				// 在runviews之后进行修改系统时间
				return true;
			}
			return false;
		}
	}
	
	// 获得今天的数据
	public boolean getPreNews(){
		Calendar calendar = MainActivity.sys_calendar;
		StoriesHandleSQLite general = new StoriesHandleSQLite(main, calendar);
		stories_group = general.getStoriesFromDB();
		// 先从数据库中取数据
		if (stories_group!=null) {
			return true;
		}
		else {
			if (getPreNewsFromOnLine(calendar)) {	
				// 存入数据库
				if (general.storedStoriesIntoDB(stories_group)) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	// 从网络上刷新今日的消息
	public boolean getTodayNewsFromOnLine(){
		json_data = HttpRequestData.getJsonContent(MainActivity.ZHIHU_API_TODAY); 
		if (json_data.equals("-1")) {
			return false;
		}
		else {
			stories_group = (new ParseJsonStories(json_data)).getStories();
			topstories_group = (new ParseJsonHotStories(json_data)).getHotStories();
			if (stories_group!=null&&topstories_group!=null) {
				return true;
			}
			return false;
		}
	}
	
	// 从网络上获得之前新闻
	public boolean getPreNewsFromOnLine(Calendar calendar){
		// 将日历提前一天
		calendar.add(Calendar.DATE, 1);
		Date format = calendar.getTime();
		String date = MainActivity.DATEFORMAT.format(format);
		json_data = HttpRequestData.getJsonContent(MainActivity.ZHIHU_API_BEFORE+date); 
		if (json_data.equals("-1")) {
			return false;
		}
		else {
			stories_group = (new ParseJsonStories(json_data)).getStories();
			topstories_group = (new ParseJsonHotStories(json_data)).getHotStories();
			if (stories_group!=null) {
				return true;
			}
			return false;
		}
	}
	
	@Override  
	protected void onProgressUpdate(Integer... values) {  
		
	}
	
	@Override  
    protected void onPostExecute(Boolean result) {    
        if (result) {  
            loadingnews.runView(stories_group, topstories_group);
        } else {  
        	Toast.makeText(main, "没有成功从网络处获得数据", Toast.LENGTH_SHORT).show();
			main_swiperefresh.onRefreshComplete();
        }  
    }  

}
