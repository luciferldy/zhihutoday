package com.example.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.zhihupocket.MainActivity;

public class TopStoriesHandleSQLite {
	
	private Context context;
	private MainDBHelper dbhelper;
	private SQLiteDatabase db;
	private String date;
	
	public TopStoriesHandleSQLite(Context context){
		this.context = context;
		Date format = MainActivity.sys_calendar.getTime();
		date = MainActivity.DATEFORMAT.format(format);
	}
	
	public boolean storedTopStoriesIntoDB(ArrayList<HashMap<String, Object>> topstories_group){
		try {
			dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			// 判断数据库中是否有原来记录
			db = dbhelper.getReadableDatabase();
			Cursor cursor = db.query(MainDBHelper.TABLE_TOPSTORIES, new String[]{"id"},"date="+date, null, null, null, null, null);
			// 删除数据库中原来有记录
			db = dbhelper.getWritableDatabase();
			if (cursor.getCount()!=0) {
				String del = "delete from '"+MainDBHelper.TABLE_TOPSTORIES+"' where date='"+date+"'";
				db.execSQL(del);
			}
			
			ContentValues values = new ContentValues();
			for (int i = 0; i < topstories_group.size(); i++) {
				 values.put("date", date);
				 values.put("image", topstories_group.get(i).get("image").toString());
				 values.put("id", topstories_group.get(i).get("id").toString());
				 values.put("type", topstories_group.get(i).get("type").toString());
				 values.put("title", topstories_group.get(i).get("title").toString());
				 values.put("share_url", topstories_group.get(i).get("share_url").toString());
				 values.put("ga_prefix", topstories_group.get(i).get("ga_prefix").toString());
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
	
	public ArrayList<HashMap<String, Object>> getTopStoriesFromDB(){
		try {
			dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			db = dbhelper.getReadableDatabase();
			Cursor cursor = db.query(MainDBHelper.TABLE_TOPSTORIES, new String[]{"id", "image", "title", "type", "share_url", "ga_prefix"},"date="+date, null, null, null, "ga_prefix DESC", null);
			Log.v("TopStoriesHandleSQLite.getStoriesFromDB", cursor.getCount()+"");
			if (cursor.getCount() == 0) {
				Toast.makeText(context, "数据库出现错误！", Toast.LENGTH_SHORT).show();
				return null;
			}
			else {
				ArrayList<HashMap<String, Object>> topstories = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> item;
				while (cursor.moveToNext()) {
					item = new HashMap<String, Object>();
					item.put("id", cursor.getString(cursor.getColumnIndex("id")));
					item.put("title", cursor.getString(cursor.getColumnIndex("title")));
					item.put("image", cursor.getString(cursor.getColumnIndex("image")));
					item.put("type", cursor.getString(cursor.getColumnIndex("type")));
					item.put("ga_prefix", cursor.getString(cursor.getColumnIndex("ga_prefix")));
					item.put("share_url", cursor.getString(cursor.getColumnIndex("share_url")));
					topstories.add(item);
				}
				db.close();
				dbhelper.close();
				return topstories;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			db.close();
			dbhelper.close();
			return null;
		}
	}
}
