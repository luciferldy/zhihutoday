package com.example.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.zhihupocket.StoryContent;

public class StoryItemClickListener implements OnItemClickListener{
	Context context;
	ArrayList<HashMap<String, Object>> stories_group;
	public StoryItemClickListener(Context context, ArrayList<HashMap<String, Object>> stories_group) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.stories_group = stories_group;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		// 将stories和顺序传递过去
		Intent intent = new Intent(context, StoryContent.class);
		intent.putExtra("stories_group", stories_group);
		intent.putExtra("story_order", arg2);
		context.startActivity(intent);
	}
}