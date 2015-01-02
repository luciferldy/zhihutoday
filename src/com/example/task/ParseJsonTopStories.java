package com.example.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.zhihupocket.MainActivity;

public class ParseJsonTopStories {
	
	ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
	public ParseJsonTopStories(String json_data){
		transJsonTopStoriesIntoArrayList(json_data);
	}
	
	public ArrayList<HashMap<String, Object>> getTopStories(){
		return topstories_group;
	}
	
	// 将json格式的数据转化成arraylist里的数据
	public void transJsonTopStoriesIntoArrayList(String json_data){
		HashMap<String, Object> story_item;
		try {
			JSONObject json_parse_object = new JSONObject(json_data);
			JSONArray json_topstories = json_parse_object.getJSONArray("top_stories");
			//循环添加数据
			for(int i=0;i<json_topstories.length();i++){
				story_item = new HashMap<String, Object>();
				story_item.put("title", json_topstories.getJSONObject(i).getString("title"));
				story_item.put("type", json_topstories.getJSONObject(i).getString("type"));
				story_item.put("id", json_topstories.getJSONObject(i).getString("id"));
				story_item.put("ga_prefix", json_topstories.getJSONObject(i).getString("ga_prefix"));
				// 最近知乎改了接口，原来的接口不能用了
				if(json_topstories.getJSONObject(i).has("share_url")){
					story_item.put("share_url", json_topstories.getJSONObject(i).getString("share_url"));
				}
				else {
					story_item.put("share_url", MainActivity.ZHIHU_STORY_API+json_topstories.getJSONObject(i).getString("id"));
				}
				String str = json_topstories.getJSONObject(i).getString("image");
				str = HandleStringAndImage.getHandledURL(str);
				story_item.put("image", str);
				// 同时异步获取图片的uri
//				story_item.put("imguri", HandleStringAndImage.downloadPic(str, MainActivity.pic_cache));
				topstories_group.add(story_item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
