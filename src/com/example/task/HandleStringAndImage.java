package com.example.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;

public class HandleStringAndImage {
	
	public static String getHandledURL(String ori_url){
		String step_one = ori_url.replace("\\", "");
		String step_two = step_one.replace("[", "");
		String step_three = step_two.replace("]", "");
		String step_four = step_three.replace("\"", "");
		return step_four;
	}
	
	// 从网络上下载图片
	public static Uri downloadPic(String path, File cache){
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
	public static String getPicNameOfUrl(String name){
		String[] item_group = name.split("/");
		return item_group[item_group.length-1];
	}
	
	// 清除没有图片的缓存
	public static void clearImgCache(ArrayList<HashMap<String, Object>> stories_group, 
			File pic_cache){
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
}
