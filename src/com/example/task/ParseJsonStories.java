package com.example.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

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
					str = HandleStringAndImage.getHandledURL(str);
//						System.out.println(str);
					
					story_item.put("images", str);
					story_item.put("imguri", HandleStringAndImage.downloadPic(str, MainActivity.pic_cache));
				}
				stories_group.add(story_item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
