package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhihupocket.R;

public class StoriesAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
		private ArrayList<HashMap<String, Object>> stories_group;
		//构造函数
		public StoriesAdapter(Context context, ArrayList<HashMap<String, Object>> stories_group) {
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