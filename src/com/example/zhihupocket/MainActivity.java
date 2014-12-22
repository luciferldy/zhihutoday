package com.example.zhihupocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends ActionBarActivity {

	private static final String ZHIHU_API = "http://news-at.zhihu.com/api/3/news/latest";
	private static final String ZHIHU_STORY_API = "http://daily.zhihu.com/story/";
	private ArrayList<HashMap<String, Object>> stories_group;
	private ArrayList<HashMap<String, Object>> topstories_group;
	private Handler main_thead_handler = new Handler();
	private ListView lv_showshortcontent;
	private File pic_cache;
	private ViewFlipper vf_hotstories_show;
	private ProgressDialog main_processdialog;
	private GestureDetector hotstoriesclickgesture;
	private SwipeRefreshLayout main_swiperefresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		vf_hotstories_show = (ViewFlipper)findViewById(R.id.vf_show_hotstories);
		hotstoriesclickgesture = new GestureDetector(new MainGestureDetectorListener());
		main_swiperefresh = (SwipeRefreshLayout)findViewById(R.id.main_swipetorefresh);
		main_swiperefresh.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		main_swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				main_swiperefresh.setRefreshing(true);
				Thread getandparseData = new Thread(new getAndParseJsonData());
				getandparseData.start();
				Log.d("MainActivity.initView", "开始刷新");
				
			}
		});
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		//清空缓存
		File[] files = pic_cache.listFiles();
		for(File file: files){
			file.delete();
		}
		pic_cache.delete();
	}
	
	// 清除没有图片的缓存
	protected void clearImgCache(){
		File[] files = pic_cache.listFiles();
		int flag=0;
		String img_uri;
		ArrayList<String> imgs = new ArrayList<String>();
		
		for(int i=0;i<stories_group.size();i++){
			if(stories_group.get(i).containsKey("imguri")){
				img_uri = stories_group.get(i).get("imguri").toString();
				imgs.add(getPicNameOfUrl(img_uri));
			}
		}
		
		for(File file: files){
			flag=0;
			if (imgs.contains(file.getName())) {
				flag=1;
			}
			if(flag==0){
				file.delete();
			}
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
            case R.id.action_settings:
            	finish();
                return true;
            default:
            	return super.onOptionsItemSelected(item);
        }
    }

	// 获取并解析json数据
	public class getAndParseJsonData implements Runnable{
		
		private String json_data;
		
		public getAndParseJsonData(){
		}	
		@Override
		public void run(){
			if((json_data = getJsonContent()).equals("-1")){
				//显示没有成功从网络处获取到数据
				main_thead_handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "没有成功从网络处获得数据", Toast.LENGTH_SHORT).show();
//						main_processdialog.cancel();
						main_swiperefresh.setRefreshing(false);
					}
				});
			}
			else {
				//解析json数据，将图片之类的都解析出来
				stories_group = transJsonStoriesIntoArrayList(json_data);
				topstories_group = transJsonTopStoriesIntoArrayList(json_data);
				main_thead_handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						main_thead_handler.post(new Runnable() {		
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//在ui线程中设置listview
								lv_showshortcontent = (ListView)findViewById(R.id.lv_showshortcontent);
								loadListAdapter loadlistadapter = new loadListAdapter(getApplicationContext(), stories_group);
								lv_showshortcontent.setAdapter(loadlistadapter);
								lv_showshortcontent.setOnItemClickListener(new StoryItemClickListener());
								
								// 初始化flipview的内容以及滚动的实现
								initViewFlipperData();
								vf_hotstories_show.setVisibility(View.VISIBLE);
								// 添加点击监听器
								vf_hotstories_show.setOnClickListener(new View.OnClickListener() {
									
									@SuppressLint("NewApi")
									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										// getDisplayedChild方法获取的是前一个的
										int i = vf_hotstories_show.getDisplayedChild()+1;
										i = i<5?i:i-5;
										Intent intent = new Intent(MainActivity.this, StoryContent.class);
										intent.putExtra("stories_group", topstories_group);
										// 万万没想到，标记的时候这个是反着来的
										intent.putExtra("story_order", i);
										startActivity(intent);
									}
								});

								// 添加滑动监听器
								vf_hotstories_show.setOnTouchListener(new OnTouchListener() {
									
									@Override
									public boolean onTouch(View arg0, MotionEvent arg1) {
										// TODO Auto-generated method stub
										return hotstoriesclickgesture.onTouchEvent(arg1);
									}
								});
							    main_swiperefresh.setRefreshing(false);
								clearImgCache();
							}
						});
					}
				});
			}
		}
	}
	
	// 将json格式的数据转化成arraylist里的数据
	public ArrayList<HashMap<String, Object>> transJsonStoriesIntoArrayList(String json_data){
		HashMap<String, Object> story_item;
		ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();
		try {
//			System.out.println(json_data);
			JSONObject json_parse_object = new JSONObject(json_data);
			JSONArray json_stories = json_parse_object.getJSONArray("stories");
			for(int i=0; i<json_stories.length(); i++){
				
				story_item = new HashMap<String, Object>();
				story_item.put("title", json_stories.getJSONObject(i).getString("title"));
				if (json_stories.getJSONObject(i).has("share_url")) {
					story_item.put("share_url", json_stories.getJSONObject(i).getString("share_url"));
				}
				else {
					story_item.put("share_url", MainActivity.ZHIHU_STORY_API+json_stories.getJSONObject(i).getString("id"));
				}
				
				// 判断数组中是否存在images这个项目
				if(json_stories.getJSONObject(i).has("images")){
					String str = json_stories.getJSONObject(i).getString("images");
					str = getHandledURL(str);
//					System.out.println(str);
					
					story_item.put("images", str);
					story_item.put("imguri", downloadPic(str, pic_cache));
				}
				stories_group.add(story_item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
		return stories_group;
	}
	
	public String getHandledURL(String ori_url){
		String step_one = ori_url.replace("\\", "");
		String step_two = step_one.replace("[", "");
		String step_three = step_two.replace("]", "");
		String step_four = step_three.replace("\"", "");
		return step_four;
	}
	
	// 将json格式的数据转化成arraylist里的数据
	public ArrayList<HashMap<String, Object>> transJsonTopStoriesIntoArrayList(String json_data){
		HashMap<String, Object> story_item;
		ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
		try {
			
			JSONObject json_parse_object = new JSONObject(json_data);
			JSONArray json_topstories = json_parse_object.getJSONArray("top_stories");
			//循环添加数据
			for(int i=0;i<json_topstories.length();i++){
				story_item = new HashMap<String, Object>();
				story_item.put("title", json_topstories.getJSONObject(i).getString("title"));
				// 最近知乎改了接口，原来的接口不能用了
				if(json_topstories.getJSONObject(i).has("share_url")){
					story_item.put("share_url", json_topstories.getJSONObject(i).getString("share_url"));
				}
				else {
					story_item.put("share_url", MainActivity.ZHIHU_STORY_API+json_topstories.getJSONObject(i).getString("id"));
				}
				String str = json_topstories.getJSONObject(i).getString("image");
				str = getHandledURL(str);
				
				story_item.put("image", str);
				// 同时异步获取图片的uri
				story_item.put("imguri", downloadPic(str, pic_cache));
				topstories_group.add(story_item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return topstories_group;
	}
	
	// 从网络上下载图片
	public Uri downloadPic(String path, File cache){
		String name = getPicNameOfUrl(path);
		File file = new File(cache, name);
		if(file.exists()){
			//这个方法能够获得文件的URI
			return Uri.fromFile(file);
		}else {
			//从网络上获取图片
			try{
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();  
	                FileOutputStream fos = new FileOutputStream(file);  
	                byte[] buffer = new byte[1024];  
	                int len = 0;  
	                while ((len = is.read(buffer)) != -1) {  
	                    fos.write(buffer, 0, len);  
	                }  
	                is.close();  
	                fos.close();  
	                // 返回一个URI对象  
	                return Uri.fromFile(file);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}

	// 获得图片的名称
	public String getPicNameOfUrl(String name){
		String[] item_group = name.split("/");
		return item_group[item_group.length-1];
	}
	
	// 通过请求URL获取数据,返回-1的话代表连接失败
	public String getJsonContent(){
		HttpClient httpclient = null;
		try{
			//httpclient对象
			httpclient = new DefaultHttpClient();
			//httpget对象，构造
			HttpGet httpget = new HttpGet(ZHIHU_API);
			//请求httpclient获得httpresponse
			HttpResponse httpresponse = httpclient.execute(httpget);
			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				HttpEntity httpEntity = httpresponse.getEntity();
				//获得返回的字符串
				String json_data = EntityUtils.toString(httpEntity, "utf-8");
				return json_data;
			}
			else{
				//中断连接
				httpget.abort();
			}
		}
		catch(Exception e){
			e.printStackTrace();	
		}	
		httpclient.getConnectionManager().shutdown();
		return "-1";	
	}	
	// listview自定义适配器
	public class loadListAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
		private ArrayList<HashMap<String, Object>> stories_group;
		//构造函数
		public loadListAdapter(Context context, ArrayList<HashMap<String, Object>> stories_group) {
			// TODO Auto-generated constructor stub
			this.mInflater = LayoutInflater.from(context);
			this.stories_group = stories_group;
		}
			
		
		@Override
		public int getCount(){
			return stories_group.size();
		}
		
		@Override
		public Object getItem(int arg0){
			return null;
		}
		
		@Override
		public long getItemId(int arg0){
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup Parent){
			PicAndTextViewGroup picandtext = null;
			if(convertView == null){
				picandtext = new PicAndTextViewGroup();
				convertView = mInflater.inflate(R.layout.main_lv_item, null);
				picandtext.iv_story_img = (ImageView)convertView.findViewById(R.id.iv_story_img);
				picandtext.tv_story_title = (TextView)convertView.findViewById(R.id.tv_story_title);
				convertView.setTag(picandtext);
			}
			else{
				picandtext = (PicAndTextViewGroup)convertView.getTag();
			}
			
			// 有图片的话显示图片
			if (stories_group.get(position).containsKey("imguri")) {
				picandtext.iv_story_img.setImageURI((Uri)stories_group.get(position).get("imguri"));
			}
			picandtext.tv_story_title.setText(stories_group.get(position).get("title").toString());
			
			return convertView;
		}
		
		private class PicAndTextViewGroup{
			ImageView iv_story_img;
			TextView tv_story_title;
		}
		
	}
	
	// list项目单选的监听器
	public class StoryItemClickListener implements OnItemClickListener{
		
		public StoryItemClickListener() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			// 将stories和顺序传递过去
			Intent intent = new Intent(MainActivity.this, StoryContent.class);
			intent.putExtra("stories_group", stories_group);
			intent.putExtra("story_order", arg2);
			startActivity(intent);
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
	
	// 初始化ViewFlipper程序
	public void initViewFlipperOfTopStories(){
		vf_hotstories_show.removeAllViews();
//		rg_show_topstory_ifchecked.removeAllViews();
		
	}
	
	// 初始化ViewFlipper的数据
	@SuppressWarnings("deprecation")
	public void initViewFlipperData(){
		
		FrameLayout fl_page_item;
		Uri uri;
		Drawable drawble;
		TextView tv;
		ImageView img;
//		RadioGroup rg;
//		RadioButton rb;
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 0:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_one);
				break;
			case 1:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_two);
				break;
			case 2:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_three);
				break;
			case 3:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_four);
				break;
			case 4:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_five);
				break;
			default:
				fl_page_item = (FrameLayout)findViewById(R.id.topstory_page_one);
			}
			Log.v("MainActivity", topstories_group.size()+"");
			// 获取图片uri
			uri = (Uri)topstories_group.get(i).get("imguri");
			drawble = Drawable.createFromPath(uri.getPath());
			fl_page_item.setBackgroundDrawable(drawble);
			img = (ImageView)fl_page_item.getChildAt(0);
			img.setImageDrawable(drawble);
			tv = (TextView)fl_page_item.getChildAt(1);
			tv.setText(topstories_group.get(i).get("title").toString());
		}
		
	}

	// 手势监听器
	public class MainGestureDetectorListener implements OnGestureListener{
		
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			// 向前翻页
			if(arg0.getX()-arg1.getX()>60){
//				vf_hotstories_show.showNext();
				showNextPage();
				return true;
			}
			// 向后翻页
			else if(arg1.getX()-arg0.getX()>60){
//				vf_hotstories_show.showPrevious();
				showPrevPage();
				return true;
			}
			else {
				return false;
			}
			
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
		// 向前翻页
		public void showPrevPage(){
			if(vf_hotstories_show.getChildCount() <= 1){
				return;
			}
			int in = R.anim.push_left_in;
			int out = R.anim.push_right_out;
			vf_hotstories_show.setInAnimation(MainActivity.this, in);
			vf_hotstories_show.setOutAnimation(MainActivity.this, out);
			vf_hotstories_show.showPrevious();
			
		}
		
		// 向后翻页
		public void showNextPage(){
			if(vf_hotstories_show.getChildCount() <= 1){
				return;
			}
			int in = R.anim.push_right_in;
			int out = R.anim.push_left_out;
			vf_hotstories_show.setInAnimation(MainActivity.this, in);
			vf_hotstories_show.setOutAnimation(MainActivity.this, out);
			vf_hotstories_show.showNext();
		}
		
	}
	
}
