package com.example.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;

import com.example.zhihupocket.MainActivity;

public class ParseJsonStories {
	
	ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();

	public ParseJsonStories(String json_data){
		transJsonStoriesIntoArrayList(json_data);
	}
	
	public ArrayList<HashMap<String, Object>> getStories(){
		return stories_group;
	}
	
	// 将json格式的数据转化成arraylist里的数据
	public void transJsonStoriesIntoArrayList(String json_data){
		HashMap<String, Object> story_item;
		try {
//				System.out.println(json_data);
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
//						System.out.println(str);
					
					story_item.put("images", str);
					story_item.put("imguri", downloadPic(str, MainActivity.pic_cache));
				}
				stories_group.add(story_item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public String getHandledURL(String ori_url){
		String step_one = ori_url.replace("\\", "");
		String step_two = step_one.replace("[", "");
		String step_three = step_two.replace("]", "");
		String step_four = step_three.replace("\"", "");
		return step_four;
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
}
