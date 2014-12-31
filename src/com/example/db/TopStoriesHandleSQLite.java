package com.example.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zhihupocket.MainActivity;

public class TopStoriesHandleSQLite {
	
	public boolean handleTopStories(Context context, ArrayList<HashMap<String, Object>> topstories_group){
		try {
			MainDBHelper dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String date = MainActivity.end_date.get(MainActivity.end_date.size()-1);
			for (int i = 0; i < topstories_group.size(); i++) {
				 values.put("date", date);
				 values.put("image", topstories_group.get(i).get("image").toString());
				 values.put("id", topstories_group.get(i).get("id").toString());
				 values.put("type", topstories_group.get(i).get("type").toString());
				 values.put("title", topstories_group.get(i).get("title").toString());
				 values.put("share_url", topstories_group.get(i).get("share_url").toString());
				 db.insert(MainDBHelper.TABLE_TOPSTORIES, "id", values);
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
