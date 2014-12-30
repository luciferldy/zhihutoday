package com.example.task;

import java.util.ArrayList;
import java.util.HashMap;

import news.LoadingBaseNews;

import com.example.zhihupocket.MainActivity;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.os.AsyncTask;
import android.widget.Toast;

public class GetStoriesAndParseTask extends AsyncTask<Void, Integer, Boolean>{
	private String json_data;
	private ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
	private LoadingBaseNews loadingnews;
	private MainActivity main;
	private PullToRefreshScrollView main_swiperefresh;
	private String query_type;
	
	public GetStoriesAndParseTask(MainActivity main, LoadingBaseNews loadingnews, 
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
		json_data = HttpRequestData.getJsonContent(query_type); 
		if (json_data.equals("-1")) {
			return false;
		}
		else {
			stories_group = (new ParseJsonStories(json_data)).getStories();
			topstories_group = (new ParseJsonHotStories(json_data)).getHotStories();
			return true;
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
