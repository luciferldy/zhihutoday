package com.example.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.HotStoriesPagersAdapter;
import com.example.adapter.StoriesAdapter;
import com.example.task.HttpRequestData;
import com.example.task.ParseJsonHotStories;
import com.example.task.ParseJsonStories;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;

public class GetStoriesAndParse implements Runnable{
		
		private String json_data;
		private MainActivity main;
		private Handler main_thread_handler;
		private SwipeRefreshLayout main_swiperefresh;
		private ArrayList<HashMap<String, Object>> stories_group;
		private ArrayList<HashMap<String, Object>> topstories_group;
		
		public GetStoriesAndParse(MainActivity main, Handler main_thread_handler, SwipeRefreshLayout main_swiperefresh){
			this.main = main;
			this.main_thread_handler = main_thread_handler;
			this.main_swiperefresh = main_swiperefresh;
		}	
		@Override
		public void run(){
			Log.v("GetStoriesAndParse", "run!");
			json_data = HttpRequestData.getJsonContent();
			if(json_data.equals("-1")){
				//显示没有成功从网络处获取到数据
				main_thread_handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(main, "没有成功从网络处获得数据", Toast.LENGTH_SHORT).show();
//						main_processdialog.cancel();
						main_swiperefresh.setRefreshing(false);
						main_swiperefresh.setEnabled(true);
					}
				});
			}
			else {
				//解析json数据，将图片之类的都解析出来
				Log.v("GetStoriesAndParse", "parsestories!");
				stories_group = (new ParseJsonStories(json_data)).getStories();
				topstories_group = (new ParseJsonHotStories(json_data)).getHotStories();
				main_thread_handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
							Log.v("GetStoriesAndParse", "runview");
							main.runView(stories_group, topstories_group);
						}
				});
			}
		}
		
		
		
	}