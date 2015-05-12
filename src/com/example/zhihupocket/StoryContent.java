package com.example.zhihupocket;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class StoryContent extends Activity{
	
	private ArrayList<HashMap<String, Object>> top_or_not_stories_group;
	private int story_order;
	@SuppressWarnings("unused")
	private int story_number;
	private WebView wv_show_story;
	private ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_content);
		//添加一个回退键
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		initStoriesGroup();
		initAllView();
		loadStory();
		
	}

	// 初始化listView数组
	@SuppressWarnings("unchecked")
	public void initStoriesGroup(){
		try {
			
			top_or_not_stories_group = new ArrayList<HashMap<String,Object>>();
			top_or_not_stories_group = (ArrayList<HashMap<String,Object>>) this.getIntent().getSerializableExtra("stories_group");
			story_order = this.getIntent().getIntExtra("story_order", 0);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
		story_number = top_or_not_stories_group.size();
	}
	
	// 初始化webview控件
	@SuppressLint("SetJavaScriptEnabled")
	public void initAllView(){
		wv_show_story = (WebView)findViewById(R.id.wv_show_story);
		progress = (ProgressBar)findViewById(R.id.pb_show_progress);
		// 支持缩放
		wv_show_story.getSettings().setBuiltInZoomControls(false);
		wv_show_story.getSettings().setJavaScriptEnabled(true);
	}
	
	// 加载数据
	public void loadStory(){
		// 加载链接
		wv_show_story.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
//				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Oh no, "+description, Toast.LENGTH_SHORT).show();
//				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		wv_show_story.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress){
				super.onProgressChanged(view, newProgress);
				progress.setProgress(newProgress);
				// 当进度到100%时，结束，视图变为不见
				if (newProgress == 100 || wv_show_story.getHeight()!=0) {
					progress.setVisibility(View.GONE);
				}
			}
		});
		wv_show_story.loadUrl(top_or_not_stories_group.get(story_order).get("share_url").toString());
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub		
		if(keyCode == KeyEvent.KEYCODE_BACK && wv_show_story.canGoBack()){
			wv_show_story.goBack();
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.story_share:
			shareStory();
			return true;
		case R.id.story_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// 初始化ActionBar
		getMenuInflater().inflate(R.menu.story_content_actionbar, menu);
		return true;
	}
	
	// 分享故事
	public void shareStory(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
	}
	
	// 分享界面的适配器
}