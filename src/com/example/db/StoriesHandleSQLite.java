package com.example.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.zhihupocket.MainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class StoriesHandleSQLite {

	
	public boolean handleStories(ArrayList<HashMap<String, Object>> stories_group, Context context){
		try {
			MainDBHelper dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String date = MainActivity.end_date.get(MainActivity.end_date.size()-1);
			for (int i = 0; i < stories_group.size(); i++) {
				 values.put("date", date);
				 values.put("images", stories_group.get(i).get("images").toString());
				 values.put("id", stories_group.get(i).get("id").toString());
				 values.put("type", stories_group.get(i).get("type").toString());
				 values.put("title", stories_group.get(i).get("title").toString());
				 values.put("share_url", stories_group.get(i).get("share_url").toString());
				 db.insert(MainDBHelper.TABLE_STORIES, "id", values);
				 values.clear();
			}
			db.close();
			dbhelper.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
}
